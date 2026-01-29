package it.ethereallabs.staffshifts;

import it.ethereallabs.staffshifts.models.Shift;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class StaffShifts extends JavaPlugin {

    private static StaffShifts instance;

    public HashMap<UUID, Shift> shifts = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
    }

    public static StaffShifts getInstance() {
        return instance;
    }
}
