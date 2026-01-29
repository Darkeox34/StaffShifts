package it.ethereallabs.staffshifts.manager;

import it.ethereallabs.staffshifts.models.Shift;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ShiftsManager {
    private HashSet<UUID> staffInDuty;
    private HashMap<UUID, Shift> shifts;

    public ShiftsManager() {
        staffInDuty = new HashSet<>();
        shifts = new HashMap<>();
    }

    public HashSet<UUID> getStaffInDuty() {return staffInDuty;}
    public HashMap<UUID, Shift> getShifts() {return shifts;}
    public boolean hasActiveSession(UUID uuid) {return shifts.containsKey(uuid);}
    public Shift getShift(UUID uuid) {return shifts.get(uuid);}
}
