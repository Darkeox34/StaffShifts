package it.ethereallabs.staffshifts.events;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.models.Shift;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if(!player.hasPermission("staffshifts.staffer")) return;

        StaffShifts.getInstance().shifts.put(player.getUniqueId(), new Shift(player.getUniqueId()));
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(!player.hasPermission("staffshifts.staffer")) return;
        Shift currentShift = StaffShifts.getInstance().shifts.get(player.getUniqueId());
        currentShift.endShift();
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if(!player.hasPermission("staffshifts.staffer")) return;
    }
}
