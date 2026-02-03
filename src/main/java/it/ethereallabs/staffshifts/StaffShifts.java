package it.ethereallabs.staffshifts;

import it.ethereallabs.staffshifts.commands.CommandRegistry;
import it.ethereallabs.staffshifts.database.DatabaseManager;
import it.ethereallabs.staffshifts.events.PlayerEvents;
import it.ethereallabs.staffshifts.manager.AFKManager;
import it.ethereallabs.staffshifts.manager.ShiftsManager;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class StaffShifts extends JavaPlugin {

    private static StaffShifts instance;
    private static AFKManager afkManager;
    private static ShiftsManager shiftsManager;
    private static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        MessageUtils.loadMessages();

        databaseManager = new DatabaseManager(this);
        databaseManager.setup();

        shiftsManager = new ShiftsManager(this, databaseManager);
        shiftsManager.terminateGhostShifts();

        afkManager = new AFKManager(this, shiftsManager);

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(afkManager, shiftsManager), this);

        CommandRegistry mainCommand = new CommandRegistry();
        Objects.requireNonNull(getCommand("ss")).setExecutor(mainCommand);
        Objects.requireNonNull(getCommand("ss")).setTabCompleter(mainCommand);

        afkManager.startTask();
    }

    @Override
    public void onDisable() {
        if (afkManager != null) {
            afkManager.stopTask();
        }
        if (shiftsManager != null) {
            shiftsManager.shutdown();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    public static StaffShifts getInstance() {return instance;}
    public static ShiftsManager getShiftsManager() { return shiftsManager; }
    public static AFKManager getAFKManager() { return afkManager; }
    public static DatabaseManager getDatabaseManager() {return databaseManager;}
}
