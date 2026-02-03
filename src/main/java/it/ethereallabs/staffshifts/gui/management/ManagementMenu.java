package it.ethereallabs.staffshifts.gui.management;

import it.ethereallabs.staffshifts.gui.StaffDashboard;
import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import it.ethereallabs.staffshifts.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ManagementMenu extends BaseMenu {
    public ManagementMenu(){
        super("Management Menu", 27);
    }

    @Override
    public void draw(Player p) {
        inv.setItem(11, createActiveStaffItem(p));
        inv.setItem(15, createLeaderboardItem());
        inv.setItem(26, createItem("§cBack", Material.RED_STAINED_GLASS_PANE, List.of(), 1));
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if(slot == 11){
            new ActiveStaffMenu(p).open(p);
        }
        else if(slot == 15){
            new LeaderboardMenu().open(p);
        }
        else if(slot == 26){
            new StaffDashboard(p).open(p);
        }
    }

    private ItemStack createLeaderboardItem(){
        return createItem("§eStaff Leaderboard", Material.GOLD_INGOT, List.of(
                "", "§b(L-Click) Open Staff Leaderboard"
        ), 1);
    }

    private ItemStack createActiveStaffItem(Player p){
        ItemStack head = createItem("§eActive Staff", Material.PLAYER_HEAD, List.of(
                "", "§b(L-Click) View Staffers on Duty"
        ), 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if(skullMeta != null){
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
            head.setItemMeta(skullMeta);
        }
        return head;
    }
}
