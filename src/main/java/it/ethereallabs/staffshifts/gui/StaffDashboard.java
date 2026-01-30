package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.manager.AFKManager;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StaffDashboard extends BaseMenu {
    public StaffDashboard(Player p) {
        super("Staff Dashboard", 27);

        Bukkit.getScheduler().runTaskTimer(StaffShifts.getInstance(), () -> draw(p), 20L, 20L);
    }

    @Override
    public void draw(Player p) {
        inv.clear();
        inv.setItem(11, createShiftItem(p));
        if(p.hasPermission("staffshifts.management") || p.isOp())
            inv.setItem(13, createManagementItem());
        inv.setItem(15, createShiftHistoryItem());
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        boolean leftClick = e.isLeftClick();
        boolean rightClick = e.isRightClick();

        if(slot == 13 && (p.hasPermission("staffshifts.management") || p.isOp())){
            new ManagementMenu().open(p);
        }

        if (slot == 11) {
            if(leftClick) {
                boolean isCurrentlyInShift = StaffShifts.getShiftsManager().hasActiveSession(p.getUniqueId());
                String title = isCurrentlyInShift ? "§0End your shift?" : "§0Start a new shift?";

                new ConfirmationMenu(title, () -> {
                    var manager = StaffShifts.getShiftsManager();
                    if (!isCurrentlyInShift) {
                        manager.addShift(p.getUniqueId());
                        MessageUtils.sendMessage(p, "§aShift Started!");
                    } else {
                        manager.getShift(p.getUniqueId()).endShift();
                        MessageUtils.sendMessage(p,"§cShift Terminated!");
                    }
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    new StaffDashboard(p).open(p);
                }, () -> new StaffDashboard(p).open(p)).open(p);
            }
        }
        if(slot == 15){
            new ShiftHistory().open(p);
        }
    }

    public ItemStack createShiftItem(Player p) {
        var manager = StaffShifts.getShiftsManager();
        var uuid = p.getUniqueId();
        boolean isInShift = manager.hasActiveSession(uuid);

        List<String> shiftItemLore = new ArrayList<>();

        if (isInShift) {
            var shift = manager.getShift(uuid);

            String activeTime = TimeUtils.formatDuration(shift.getActiveMillis());
            String idleTime = TimeUtils.formatDuration(shift.getIdleMillis());
            String totalTime = TimeUtils.formatDuration(shift.getTotalDuration());

            shiftItemLore.add("§8-----------------------");
            shiftItemLore.add("§aActive Time: " + activeTime);
            shiftItemLore.add("§cIdle Time: " + idleTime);
            shiftItemLore.add("§9Total Time: " + totalTime);
            shiftItemLore.add("§8-----------------------");
            shiftItemLore.add("§c(L-Click) End this Shift");
            shiftItemLore.add("§b(R-Click) Manage Notes");
        } else {
            shiftItemLore.add("");
            shiftItemLore.add("§cNot in duty");
            shiftItemLore.add("");
            shiftItemLore.add("§b(L-Click) Start a new Shift");
        }

        ItemStack item = createItem("§eCurrent Shift", Material.CLOCK, shiftItemLore, 1);

        if (isInShift) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    public ItemStack createShiftHistoryItem() {
        return createItem("§eShift History", Material.LECTERN,
                List.of("", "§b(L-Click) View your Shift History")
                , 1);
    }

    public ItemStack createManagementItem(){
        return createItem("§cManagement", Material.NETHER_STAR,
                List.of("", "§b(L-Click) Open Management Menu"),
                1);
    }
}
