package it.ethereallabs.staffshifts.models;

import java.util.UUID;

public class Staffer {

    private final UUID uuid;
    private final String username;

    private long totalActiveMillis;
    private long totalIdleMillis;
    private long lastJoined;

    public Staffer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.totalActiveMillis = 0;
        this.totalIdleMillis = 0;
    }

    public Staffer(UUID uuid, String username, long totalActive, long totalIdle, long lastJoined) {
        this.uuid = uuid;
        this.username = username;
        this.totalActiveMillis = totalActive;
        this.totalIdleMillis = totalIdle;
        this.lastJoined = lastJoined;
    }

    public void addStats(long active, long idle) {
        this.totalActiveMillis += active;
        this.totalIdleMillis += idle;
    }

    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public long getTotalActiveMillis() { return totalActiveMillis; }
    public long getLastJoined() { return lastJoined; }
    public void setLastJoined(long lastJoined) { this.lastJoined = lastJoined; }
    public long getTotalIdleMillis() { return totalIdleMillis; }
}