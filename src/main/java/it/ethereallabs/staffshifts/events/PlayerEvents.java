package it.ethereallabs.staffshifts.events;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.manager.AFKManager;
import it.ethereallabs.staffshifts.manager.ShiftsManager;
import it.ethereallabs.staffshifts.models.Shift;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private StaffShifts staffShifts;
    private AFKManager afkManager;
    private ShiftsManager shiftsManager;

    public PlayerEvents(StaffShifts staffShifts, AFKManager afkManager, ShiftsManager shiftsManager) {
        this.staffShifts = staffShifts;
        this.afkManager = afkManager;
        this.shiftsManager = shiftsManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if(!player.hasPermission("staffshifts.staffer")) return;

        staffShifts.shifts.put(player.getUniqueId(), new Shift(player.getUniqueId()));
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(!player.hasPermission("staffshifts.staffer")) return;
        Shift currentShift = staffShifts.shifts.get(player.getUniqueId());
        currentShift.endShift();
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if(!shiftsManager.hasActiveSession(player.getUniqueId())) return;

        afkManager.recordActivity(player.getUniqueId());
    }
}
