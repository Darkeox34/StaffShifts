package it.ethereallabs.staffshifts.gui;

import it.ethereallabs.staffshifts.gui.abs.BaseMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ConfirmationMenu extends BaseMenu {

    private final Runnable onConfirm;
    private final Runnable onCancel;

    public ConfirmationMenu(String title, Runnable onConfirm, Runnable onCancel) {
        super(title, 9);
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    public void draw(Player p) {
        inv.setItem(3, createItem("§aConfirm", Material.GREEN_WOOL, List.of("§7Click to proceed"), 1));
        inv.setItem(5, createItem("§cCancel", Material.RED_WOOL, List.of("§7Click to go back"), 1));
    }

    @Override
    public void handleClick(Player p, int slot, InventoryClickEvent e) {
        if (slot == 3) {
            p.closeInventory();
            onConfirm.run();
        } else if (slot == 5) {
            p.closeInventory();
            onCancel.run();
        }
    }
}