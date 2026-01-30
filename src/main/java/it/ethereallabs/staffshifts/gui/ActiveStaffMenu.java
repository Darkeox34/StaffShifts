package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.manager.ShiftsManager;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ActiveStaffMenu extends BaseMenu {
    public ActiveStaffMenu(Player p) {
        super("Active Staff", 36);
        Bukkit.getScheduler().runTaskTimer(StaffShifts.getInstance(), () -> draw(p), 20L, 20L);
    }

    @Override
    public void draw(Player p) {
        HashMap<UUID, Shift> shifts = StaffShifts.getShiftsManager().getShifts();
        final int[] slot = {0};
        shifts.forEach((uuid, shift) -> {
            inv.setItem(slot[0], createStaffItem(shift));
            slot[0]++;
        });

        inv.setItem(35, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of(), 1));
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if(slot == 35){
            if(p.hasPermission("staffshifts.management") || p.isOp())
                new ManagementMenu().open(p);
            else
                p.closeInventory();
        }
    }

    private ItemStack createStaffItem(Shift shift){
        List<String> staffItemLore = new ArrayList<>();

        String activeTime = TimeUtils.formatDuration(shift.getActiveMillis());
        String idleTime = TimeUtils.formatDuration(shift.getIdleMillis());
        String totalTime = TimeUtils.formatDuration(shift.getTotalDuration());

        staffItemLore.add("§8-----------------------");
        staffItemLore.add("§aActive Time: " + activeTime);
        staffItemLore.add("§cIdle Time: " + idleTime);
        staffItemLore.add("§9Total Time: " + totalTime);
        staffItemLore.add("§8-----------------------");

        Player p = Bukkit.getPlayer(shift.getStaffUuid());
        if(p == null)
            return null;
        ItemStack head = createItem("§e" + p.getDisplayName(), Material.PLAYER_HEAD, staffItemLore, 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if(skullMeta != null){
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
            head.setItemMeta(skullMeta);
        }
        return head;
    }
}
