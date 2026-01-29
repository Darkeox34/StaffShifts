package it.ethereallabs.staffshifts.manager;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.models.Shift;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AFKManager {

    private final StaffShifts plugin;
    private final ShiftsManager shiftsManager;

    private final Map<UUID, Long> lastActivityMap;
    private final Set<UUID> afkPlayers;

    private BukkitTask task;
    private final long afkThresholdMillis;

    public AFKManager(StaffShifts plugin, ShiftsManager shiftsManager) {
        this.plugin = plugin;
        this.shiftsManager = shiftsManager;
        this.lastActivityMap = new HashMap<>();
        this.afkPlayers = new HashSet<>();

        long seconds = 10;  //plugin.getConfig().getLong("afk-timeout-seconds", 300);
        this.afkThresholdMillis = seconds * 1000L;
    }

    public void startTask() {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::checkAfkStatus, 20L, 20L);
    }

    public void stopTask() {
        if (this.task != null) this.task.cancel();
        this.lastActivityMap.clear();
        this.afkPlayers.clear();
    }

    public void recordActivity(UUID uuid) {
        if (!shiftsManager.hasActiveSession(uuid)) return;

        lastActivityMap.put(uuid, System.currentTimeMillis());

        if (afkPlayers.contains(uuid)) {
            afkPlayers.remove(uuid);

            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent(ChatColor.GREEN + "Welcome Back! You are in Duty again."));
            }
        }
    }

    public void removePlayer(UUID uuid) {
        lastActivityMap.remove(uuid);
        afkPlayers.remove(uuid);
    }

    private void checkAfkStatus() {
        long now = System.currentTimeMillis();

        for (UUID uuid : shiftsManager.getStaffInDuty()) {
            lastActivityMap.putIfAbsent(uuid, now);

            long timeSinceLastActivity = now - lastActivityMap.get(uuid);
            Shift currentShift = shiftsManager.getShift(uuid);
            if (currentShift == null) continue;

            if (timeSinceLastActivity >= afkThresholdMillis) {
                afkPlayers.add(uuid);

                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "You are AFK "));
                }

                currentShift.addIdleTime(1000);

            } else {
                currentShift.addActiveTime(1000);
            }
        }
    }

    public boolean isAfk(UUID uuid) {
        return afkPlayers.contains(uuid);
    }
}