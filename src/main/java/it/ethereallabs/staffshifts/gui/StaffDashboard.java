package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.manager.AFKManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StaffDashboard extends BaseMenu {
    public StaffDashboard() {
        super("Staff Dashboard", 27);
    }

    @Override
    public void draw(Player p) {
        inv.clear();

        inv.setItem(11, createShiftItem(p));

        inv.setItem(15, createShiftHistoryItem());
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {

    }

    public ItemStack createShiftItem(Player p) {
        ArrayList<String> shiftItemLore = new ArrayList<>();

        if(!StaffShifts.getShiftsManager().hasActiveSession(p.getUniqueId())){
            shiftItemLore.add("");
            shiftItemLore.add("§cNot in duty");
            shiftItemLore.add("");
            shiftItemLore.add("§a(Left Click) Start a new Shift");
        }
        else {
            shiftItemLore.add("");
            shiftItemLore.add("§eActive Time: " + StaffShifts.getShiftsManager().getShift(p.getUniqueId()).getActiveMillis());
            shiftItemLore.add("§cIdle Time: " + StaffShifts.getShiftsManager().getShift(p.getUniqueId()).getIdleMillis());
            shiftItemLore.add("§aTotal Time: " + StaffShifts.getShiftsManager().getShift(p.getUniqueId()).getTotalDuration());
            shiftItemLore.add("");
            shiftItemLore.add("§c(Left Click) End this Shift");
            shiftItemLore.add("§e(Right Click) Manage Notes");
        }

        return createItem("§aCurrent Shift", Material.CLOCK, shiftItemLore, 1);
    }

    public ItemStack createShiftHistoryItem() {
        return createItem("Shift History", Material.LECTERN,
                List.of("", "§7(Left Click) View your Shift History")
                , 1);
    }
}
