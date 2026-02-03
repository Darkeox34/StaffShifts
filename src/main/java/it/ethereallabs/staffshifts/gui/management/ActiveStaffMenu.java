package it.ethereallabs.staffshifts.gui.management;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActiveStaffMenu extends BaseMenu {
    
    private final BukkitTask task;
    private final Map<Integer, UUID> slotToUuid = new java.util.HashMap<>();

    public ActiveStaffMenu(Player p) {
        super("Active Staff", 36);
        this.task = Bukkit.getScheduler().runTaskTimer(StaffShifts.getInstance(), () -> {
            if (inv != null && !inv.getViewers().isEmpty()) {
                draw(p);
            }
        }, 20L, 20L);
    }

    @Override
    public void draw(Player p) {
        Map<UUID, Shift> shifts = StaffShifts.getShiftsManager().getActiveShifts();
        slotToUuid.clear();

        int currentSlot = 0;

        for (int i = 0; i < 35; i++) inv.setItem(i, null);

        for (Map.Entry<UUID, Shift> entry : shifts.entrySet()) {
            if (currentSlot >= 35) break;

            UUID stafferUuid = entry.getKey();
            Shift shift = entry.getValue();

            inv.setItem(currentSlot, createStaffItem(shift));
            slotToUuid.put(currentSlot, stafferUuid);
            currentSlot++;
        }

        inv.setItem(35, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of(), 1));
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if (slot == 35) {
            new ManagementMenu().open(p);
        }

        UUID clickedStafferUuid = slotToUuid.get(slot);
        if (clickedStafferUuid != null) {
            Player target = Bukkit.getPlayer(clickedStafferUuid);
            if (target != null) {
                new StafferHistory(target, false).open(p);
            }
        }
    }
    
    @Override
    public void onClose(InventoryCloseEvent e) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        super.onClose(e);
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
        staffItemLore.add("");
        staffItemLore.add("§b(L-Click) View Staffer History");

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