package it.ethereallabs.staffshifts.models;

import java.util.UUID;

public class Staffer {

    private final UUID uuid;
    private final String username;

    private long totalActiveMillis;
    private long totalIdleMillis;

    public Staffer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.totalActiveMillis = 0;
        this.totalIdleMillis = 0;
    }

    public Staffer(UUID uuid, String username, long totalActiveMillis, long totalIdleMillis) {
        this.uuid = uuid;
        this.username = username;
        this.totalActiveMillis = totalActiveMillis;
        this.totalIdleMillis = totalIdleMillis;
    }

    public void addStats(long active, long idle) {
        this.totalActiveMillis += active;
        this.totalIdleMillis += idle;
    }

    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public long getTotalActiveMillis() { return totalActiveMillis; }
    public long getTotalIdleMillis() { return totalIdleMillis; }
}