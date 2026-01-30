package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftHistory extends BaseMenu {
    public ShiftHistory(){
        super("Shift History", 27);
    }

    @Override
    public void draw(Player p) {
        List<Shift> shifts = StaffShifts.getShiftsManager().getCompletedShiftsFor(p.getUniqueId());

        List<Shift> recentShifts = shifts.stream()
                .sorted((s1, s2) -> Long.compare(s2.getStartTime(), s1.getStartTime()))
                .limit(5)
                .toList();

        for (int i = 0; i < recentShifts.size(); i++) {
            int slot = 11 + i;
            Shift shift = recentShifts.get(i);

            inv.setItem(slot, createShiftItem(shift, i+1));
        }

        inv.setItem(26, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of(), 1));
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if (slot == 26) {
            new StaffDashboard(p).open(p);
        }
    }

    private ItemStack createShiftItem(Shift shift, int amount) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateStr = sdf.format(new Date(shift.getStartTime()));
            item.setAmount(amount);
            meta.setDisplayName("§e" + dateStr);

            List<String> lore = new ArrayList<>();
            lore.add("§8-----------------------");
            lore.add("§7Total Time: §f" + TimeUtils.formatDuration(shift.getTotalDuration()));
            lore.add("§7Active Time: §a" + TimeUtils.formatDuration(shift.getActiveMillis()));
            lore.add("§7Idle Time: §c" + TimeUtils.formatDuration(shift.getIdleMillis()));
            lore.add("");

            List<String> notes = shift.getNotes();
            lore.add("§7Notes (§b" + notes.size() + "§7):");

            if (notes.isEmpty()) {
                lore.add(" §8- None");
            } else {
                for (String note : notes) {
                    lore.add(" §f• §7" + note);
                }
            }
            lore.add("§8-----------------------");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}
