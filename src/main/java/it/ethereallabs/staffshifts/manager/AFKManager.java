package it.ethereallabs.staffshifts.manager;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.models.Shift;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKManager {

    private final StaffShifts plugin;
    private final ShiftsManager shiftsManager;

    private final Map<UUID, Long> lastActivityMap;

    private BukkitTask task;
    private long afkThresholdMillis;

    public AFKManager(StaffShifts plugin, ShiftsManager shiftsManager) {
        this.plugin = plugin;
        this.shiftsManager = shiftsManager;
        this.lastActivityMap = new HashMap<>();

        reloadConfig();
    }

    public void reloadConfig() {
        long seconds = plugin.getConfig().getLong("afk-threshold-seconds", 300);
        this.afkThresholdMillis = seconds * 1000L;
    }

    public void startTask() {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::checkAfkStatus, 20L, 20L);
    }

    public void stopTask() {
        if (this.task != null) this.task.cancel();
        this.lastActivityMap.clear();
    }

    public void recordActivity(UUID uuid) {
        if (!shiftsManager.hasActiveSession(uuid)) return;

        lastActivityMap.put(uuid, System.currentTimeMillis());
    }

    private void checkAfkStatus() {
        long now = System.currentTimeMillis();

        for (UUID uuid : shiftsManager.getStaffInDuty()) {
            lastActivityMap.putIfAbsent(uuid, now);

            long timeSinceLastActivity = now - lastActivityMap.get(uuid);
            Shift currentShift = shiftsManager.getShift(uuid);
            if (currentShift == null) continue;

            if (timeSinceLastActivity >= afkThresholdMillis) {
                currentShift.addIdleTime(1000);

            } else {
                currentShift.addActiveTime(1000);
            }
        }
    }
}