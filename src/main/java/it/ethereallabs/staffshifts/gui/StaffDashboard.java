package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.gui.management.ManagementMenu;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class StaffDashboard extends BaseMenu {
    
    private BukkitTask task;

    public StaffDashboard(Player p) {
        super("Staff Dashboard", 27);
        this.task = Bukkit.getScheduler().runTaskTimer(StaffShifts.getInstance(), () -> {
            if (inv != null && !inv.getViewers().isEmpty()) {
                draw(p);
            }
        }, 20L, 20L);
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
    public void onClose(InventoryCloseEvent e) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        super.onClose(e);
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        boolean leftClick = e.isLeftClick();

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
                    } else {
                        manager.endShift(p.getUniqueId());
                    }
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    new StaffDashboard(p).open(p);
                }, () -> new StaffDashboard(p).open(p)).open(p);
            }
        }
        else if (slot == 15) {
            StaffShifts.getShiftsManager().getStafferProfile(p.getUniqueId(), staffer -> {
                new ShiftHistory(staffer).open(p);
            });
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