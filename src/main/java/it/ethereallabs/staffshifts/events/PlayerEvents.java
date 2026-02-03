package it.ethereallabs.staffshifts.events;

import it.ethereallabs.staffshifts.manager.AFKManager;
import it.ethereallabs.staffshifts.manager.ShiftsManager;
import it.ethereallabs.staffshifts.utils.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private final AFKManager afkManager;
    private final ShiftsManager shiftsManager;

    public PlayerEvents(AFKManager afkManager, ShiftsManager shiftsManager) {
        this.afkManager = afkManager;
        this.shiftsManager = shiftsManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission(Permissions.STAFFER)) return;

        shiftsManager.addShift(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission(Permissions.STAFFER)) return;
        
        if (shiftsManager.hasActiveSession(player.getUniqueId())) {
            shiftsManager.endShift(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!shiftsManager.hasActiveSession(player.getUniqueId())) return;

        afkManager.recordActivity(player.getUniqueId());
    }
}
