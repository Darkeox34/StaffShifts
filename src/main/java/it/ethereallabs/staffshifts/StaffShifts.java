package it.ethereallabs.staffshifts;

import it.ethereallabs.staffshifts.commands.CommandRegistry;
import it.ethereallabs.staffshifts.events.PlayerEvents;
import it.ethereallabs.staffshifts.gui.abs.ChatPrompts;
import it.ethereallabs.staffshifts.manager.AFKManager;
import it.ethereallabs.staffshifts.manager.ShiftsManager;
import it.ethereallabs.staffshifts.models.Shift;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class StaffShifts extends JavaPlugin {

    private static StaffShifts instance;
    private static AFKManager afkManager;
    private static ShiftsManager shiftsManager;

    public HashMap<UUID, Shift> shifts = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        shiftsManager = new ShiftsManager();
        afkManager = new AFKManager(this, shiftsManager);
        Bukkit.getPluginManager().registerEvents(ChatPrompts.getInstance(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this, afkManager, shiftsManager), this);

        CommandRegistry mainCommand = new CommandRegistry();
        Objects.requireNonNull(getCommand("ss")).setExecutor(mainCommand);
        Objects.requireNonNull(getCommand("ss")).setTabCompleter(mainCommand);

        afkManager.startTask();
    }

    @Override
    public void onDisable() {
        afkManager.stopTask();
    }

    public static StaffShifts getInstance() {return instance;}
    public static AFKManager getAFKManager() { return afkManager; }
    public static ShiftsManager getShiftsManager() { return shiftsManager; }
}
