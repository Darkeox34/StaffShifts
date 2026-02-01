package it.ethereallabs.staffshifts.gui.model;

public class LeaderboardEntry {
    public final String name;
    public final long activeTime;
    public final long lastJoined;

    public LeaderboardEntry(String name, long activeTime, long lastJoined) {
        this.name = name;
        this.activeTime = activeTime;
        this.lastJoined = lastJoined;
    }
}
