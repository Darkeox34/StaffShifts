package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LeaderboardMenu extends BaseMenu {

    public LeaderboardMenu() {
        super("Staff Leaderboard (Weekly)", 36);
    }

    @Override
    public void draw(Player p) {
        long oneWeekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        Map<UUID, Long> weeklyActiveTime = getWeeklyActiveTime(oneWeekAgo);

        List<Map.Entry<UUID, Long>> sortedList = weeklyActiveTime.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .toList();

        int slot = 0;
        int rank = 1;
        for (Map.Entry<UUID, Long> entry : sortedList) {
            if (slot >= inv.getSize()) break;

            UUID uuid = entry.getKey();
            long totalActive = entry.getValue();

            inv.setItem(slot, createStafferItem(uuid, totalActive, rank));

            slot++;
            rank++;
        }

        inv.setItem(35, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of(), 1));
    }

    @NotNull
    private static Map<UUID, Long> getWeeklyActiveTime(long oneWeekAgo) {
        HashMap<UUID, ArrayList<Shift>> allShifts = StaffShifts.getShiftsManager().getCompletedShifts();

        Map<UUID, Long> weeklyActiveTime = new HashMap<>();

        allShifts.forEach((uuid, shiftList) -> {
            long totalActive = shiftList.stream()
                    .filter(s -> s.getEndTime() > oneWeekAgo)
                    .mapToLong(Shift::getActiveMillis)
                    .sum();

            if (totalActive > 0) {
                weeklyActiveTime.put(uuid, totalActive);
            }
        });
        return weeklyActiveTime;
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

    private ItemStack createStafferItem(UUID uuid, long activeMillis, int rank) {
        List<String> lore = new ArrayList<>();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String name = player.getName() != null ? player.getName() : "Unknown";

        lore.add("§8-----------------------");
        lore.add("§7Rank: §6#" + rank);
        lore.add("§aActive Time: §f" + TimeUtils.formatDuration(activeMillis));
        lore.add("§8-----------------------");

        ItemStack head = createItem("§e" + name, Material.PLAYER_HEAD, lore, rank);

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            head.setItemMeta(skullMeta);
        }

        return head;
    }
}