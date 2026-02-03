package it.ethereallabs.staffshifts.manager;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.database.DatabaseManager;
import it.ethereallabs.staffshifts.gui.model.LeaderboardEntry;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.models.Staffer;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class ShiftsManager {

    private final StaffShifts plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, Shift> activeShifts;
    private final Set<UUID> staffInDuty;
    private final Map<UUID, Staffer> stafferCache = new HashMap<>();
    private BukkitTask autoSaveTask;

    public ShiftsManager(StaffShifts plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.activeShifts = new HashMap<>();
        this.staffInDuty = new HashSet<>();
        startAutoSaveTask();
    }

    private void startAutoSaveTask() {
        this.autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Shift shift : activeShifts.values()) {
                databaseManager.saveShift(shift, true);
            }
        }, 6000L, 6000L);
    }

    public void shutdown() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
    }

    public void terminateGhostShifts() {
        databaseManager.terminateGhostShifts();
    }

    public void addShift(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            if (hasActiveSession(uuid)) {
                player.sendMessage(MessageUtils.getMessage("already-on-duty"));
                return;
            }

            Shift shift = new Shift(uuid);
            activeShifts.put(uuid, shift);
            staffInDuty.add(uuid);

            player.sendMessage(MessageUtils.getMessage("shift-started"));

            databaseManager.saveShift(shift, true);
        }
    }

    public void endShift(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        if (!hasActiveSession(uuid)) return;

        Shift shift = activeShifts.remove(uuid);
        staffInDuty.remove(uuid);

        if (shift != null) {
            long now = System.currentTimeMillis();
            shift.setEndTime(now);

            databaseManager.saveShift(shift, false);

            getStafferProfile(uuid, staffer -> {
                staffer.addStats(shift.getActiveMillis(), shift.getIdleMillis());
                staffer.setLastJoined(System.currentTimeMillis());
                databaseManager.updateStafferData(staffer);
                stafferCache.put(uuid, staffer);
            });

            MessageUtils.sendMessage(player, "shift-ended", formatDuration(shift.getTotalDuration()));
        }
    }

    public Map<UUID, Shift> getActiveShifts() {
        return Collections.unmodifiableMap(activeShifts);
    }

    public void getRecentShifts(UUID uuid, int limit, Consumer<List<Shift>> callback) {
        databaseManager.fetchRecentShifts(uuid, limit, shifts ->
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(shifts))
        );
    }

    public void getAllShifts(UUID uuid, Consumer<List<Shift>> callback) {
        databaseManager.fetchAllShifts(uuid, shifts ->
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(shifts))
        );
    }

    public void getWeeklyLeaderboard(long since, Consumer<Map<UUID, LeaderboardEntry>> callback) {
        databaseManager.fetchWeeklyLeaderboard(since, data ->
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(data))
        );
    }

    public void getStafferProfile(UUID uuid, Consumer<Staffer> callback) {
        if (stafferCache.containsKey(uuid)) {
            callback.accept(stafferCache.get(uuid));
            return;
        }

        databaseManager.getStaffer(uuid, rs -> {
            try {
                Staffer staffer;

                String currentName = Bukkit.getOfflinePlayer(uuid).getName();
                if (currentName == null) currentName = "Unknown";

                if (rs.next()) {
                    staffer = new Staffer(
                            uuid,
                            currentName,
                            rs.getLong("total_active_millis"),
                            rs.getLong("total_idle_millis"),
                            rs.getLong("last_joined")
                    );

                    if (!rs.getString("username").equals(currentName)) {
                        databaseManager.updateStafferData(staffer);
                    }
                } else {
                    staffer = new Staffer(uuid, currentName, 0, 0, System.currentTimeMillis());
                    databaseManager.updateStafferData(staffer);
                }

                stafferCache.put(uuid, staffer);
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(staffer));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean hasActiveSession(UUID uuid) {
        return activeShifts.containsKey(uuid);
    }

    public Shift getShift(UUID uuid) {
        return activeShifts.get(uuid);
    }

    public Set<UUID> getStaffInDuty() {
        return staffInDuty;
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}