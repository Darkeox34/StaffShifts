package it.ethereallabs.staffshifts.models;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.manager.ShiftsManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shift {

    private final UUID shiftId;
    private final UUID staffUuid;
    private final long startTime;
    private long endTime;

    private long activeMillis;
    private long idleMillis;

    private final List<String> notes;

    public Shift(UUID staffUuid) {
        this.shiftId = UUID.randomUUID();
        this.staffUuid = staffUuid;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.activeMillis = 0;
        this.idleMillis = 0;
        this.notes = new ArrayList<>();
    }

    public Shift(UUID shiftId, UUID staffUuid, long startTime, long endTime, long activeMillis, long idleMillis, List<String> notes) {
        this.shiftId = shiftId;
        this.staffUuid = staffUuid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activeMillis = activeMillis;
        this.idleMillis = idleMillis;
        this.notes = notes != null ? notes : new ArrayList<>();
    }

    public void endShift() {
        if (this.endTime == 0) {
            this.endTime = System.currentTimeMillis();
        }
        ShiftsManager manager = StaffShifts.getShiftsManager();
        manager.getShifts().remove(this.staffUuid);
        manager.getStaffInDuty().remove(this.staffUuid);
        manager.addCompletedShift(this.staffUuid, this);
    }

    public boolean isOngoing() {
        return this.endTime == 0;
    }

    public void addActiveTime(long millis) {
        this.activeMillis += millis;
    }

    public void addIdleTime(long millis) {
        this.idleMillis += millis;
    }

    public void addNote(String note) {
        String formattedNote = String.format("[%s] %s", Instant.now().toString(), note);
        this.notes.add(formattedNote);
    }

    public UUID getShiftId() { return shiftId; }
    public UUID getStaffUuid() { return staffUuid; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getActiveMillis() { return activeMillis; }
    public long getIdleMillis() { return idleMillis; }
    public List<String> getNotes() { return new ArrayList<>(notes); }

    public long getTotalDuration() {
        if (endTime != 0) {
            return endTime - startTime;
        }
        return activeMillis + idleMillis;
    }
}