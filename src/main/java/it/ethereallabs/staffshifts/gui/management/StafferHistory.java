package it.ethereallabs.staffshifts.gui.management;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.models.Staffer;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StafferHistory extends BaseMenu {

    private List<Shift> shifts = new ArrayList<>();
    private Staffer staffer;
    private Boolean fromLeaderboard;
    private int page = 0;
    private final int ITEMS_PER_PAGE = 21;

    public StafferHistory(Player p, Boolean fromLeaderboard) {
        super(p.getDisplayName() + "'s History", 54);
        this.fromLeaderboard = fromLeaderboard;
    }

    @Override
    public void open(Player p) {
        StaffShifts.getShiftsManager().getStafferProfile(p.getUniqueId(), profile -> {
            StaffShifts.getShiftsManager().getAllShifts(p.getUniqueId(), shiftsData -> {
                this.shifts = shiftsData;
                this.staffer = profile;
                super.open(p);
            });
        });
    }

    @Override
    public void draw(Player p) {
        inv.clear();

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, shifts.size());

        int count = 0;
        for (int i = start; i < end; i++) {
            Shift shift = shifts.get(i);

            int slot = 10 + (count / 7 * 9) + (count % 7);

            inv.setItem(slot, createShiftItem(shift));
            count++;
        }

        inv.setItem(49, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of("§7Click to return to Management"), 1));

        inv.setItem(4, createHeadItem());

        if (page > 0) {
            inv.setItem(48, createItem("§ePrevious Page", Material.ARROW, List.of("§7Go to page " + page), 1));
        }

        if (end < shifts.size()) {
            inv.setItem(50, createItem("§eNext Page", Material.ARROW, List.of("§7Go to page " + (page + 2)), 1));
        }
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if (slot == 49) {
            if(fromLeaderboard)
                new LeaderboardMenu().open(p);
            else
                new ActiveStaffMenu(p).open(p);
            return;
        }

        if (slot == 48 && page > 0) {
            page--;
            draw(p);
            return;
        }

        if (slot == 50 && (page + 1) * ITEMS_PER_PAGE < shifts.size()) {
            page++;
            draw(p);
        }
    }

    private ItemStack createHeadItem(){
        ItemStack head = createItem("§e" + staffer.getUsername(), Material.PLAYER_HEAD, List.of(
                "§8-----------------------",
                "§fTotal Time: " + TimeUtils.formatDuration(staffer.getTotalActiveMillis()),
                "§aTotal Active Time: " + TimeUtils.formatDuration(staffer.getTotalActiveMillis()),
                "§cTotal Idle Time: " + TimeUtils.formatDuration(staffer.getTotalIdleMillis()),
                "§8-----------------------"
        ), 1);

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if(skullMeta != null){
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(staffer.getUuid()));
            head.setItemMeta(skullMeta);
        }
        return head;
    }

    private ItemStack createShiftItem(Shift shift) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateStr = sdf.format(new Date(shift.getStartTime()));
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