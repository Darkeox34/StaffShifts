package it.ethereallabs.staffshifts.manager;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

        long seconds = plugin.getConfig().getLong("afk-threshold-seconds", 300);
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

                currentShift.addIdleTime(1000);

            } else {
                currentShift.addActiveTime(1000);
            }
        }
    }
}