package it.ethereallabs.staffshifts.manager;

import it.ethereallabs.staffshifts.models.Shift;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ShiftsManager {
    private HashSet<UUID> staffInDuty;
    private HashMap<UUID, Shift> shifts;
    private HashMap<UUID, ArrayList<Shift>> completedShifts;

    public ShiftsManager() {
        staffInDuty = new HashSet<>();
        shifts = new HashMap<>();
        completedShifts = new HashMap<>();
    }

    public void addShift(UUID uuid) {
        if(hasActiveSession(uuid)) {return;}

        Shift shift = new Shift(uuid);
        shifts.put(uuid, shift);
        staffInDuty.add(uuid);
    }

    public HashSet<UUID> getStaffInDuty() {return staffInDuty;}
    public HashMap<UUID, Shift> getShifts() {return shifts;}
    public boolean hasActiveSession(UUID uuid) {return shifts.containsKey(uuid);}
    public Shift getShift(UUID uuid) {return shifts.get(uuid);}
    public HashMap<UUID, ArrayList<Shift>> getCompletedShifts(){return completedShifts;}
    public ArrayList<Shift> getCompletedShiftsFor(UUID uuid) {
        return completedShifts.getOrDefault(uuid, new ArrayList<>());
    }
    public void addCompletedShift(UUID uuid, Shift shift) {
        completedShifts.computeIfAbsent(uuid, k -> new ArrayList<>()).add(shift);
    }
}
