package it.ethereallabs.staffshifts.gui.management;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.gui.model.LeaderboardEntry;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class LeaderboardMenu extends BaseMenu {

    private Map<UUID, LeaderboardEntry> weeklyActiveTime;
    private final Map<Integer, UUID> slotToUuid = new java.util.HashMap<>();

    public LeaderboardMenu() {
        super("Staff Leaderboard (Weekly)", 54);
    }

    @Override
    public void open(Player p) {
        long oneWeekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        StaffShifts.getShiftsManager().getWeeklyLeaderboard(oneWeekAgo, data -> {
            this.weeklyActiveTime = data;
            super.open(p);
        });
    }

    @Override
    public void draw(Player p) {
        if (weeklyActiveTime == null) return;

        slotToUuid.clear();
        int rank = 1;

        for (Map.Entry<UUID, LeaderboardEntry> entry : weeklyActiveTime.entrySet()) {
            if (rank > 21) break;

            UUID stafferUuid = entry.getKey();
            LeaderboardEntry stats = entry.getValue();

            int slot = 10 + ((rank - 1) / 7 * 9) + ((rank - 1) % 7);

            inv.setItem(slot, createLeaderboardItem(stats, rank, p));
            slotToUuid.put(slot, stafferUuid);

            rank++;
        }

        inv.setItem(49, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of("§7Click to return to Management"), 1));
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if (slot == 49) {
            if (p.hasPermission("staffshifts.management") || p.isOp())
                new ManagementMenu().open(p);
            else
                p.closeInventory();
            return;
        }

        UUID clickedUuid = slotToUuid.get(slot);
        if (clickedUuid != null) {
            Player target = Bukkit.getPlayer(clickedUuid);
            if(target == null) return;
            new StafferHistory(target, true).open(p);
        }
    }

    private ItemStack createLeaderboardItem(LeaderboardEntry entry, int rank, Player p) {
        List<String> lore = new ArrayList<>();
        lore.add("§8-----------------------");
        lore.add("§7Rank: §e#" + rank);
        lore.add("§7Weekly Active: §e" + TimeUtils.formatDuration(entry.activeTime));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        lore.add("§7Last Online: §e" + (entry.lastJoined > 0 ? sdf.format(new Date(entry.lastJoined)) : "Never"));
        lore.add("§8-----------------------");
        lore.add("");
        lore.add("§b(L-Click) View Staffer History");


        ItemStack head = createItem("§e" + (entry.name != null ? entry.name : "Unknown"), Material.PLAYER_HEAD, lore, 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if(skullMeta != null){
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
            head.setItemMeta(skullMeta);
        }

        return head;
    }
}
