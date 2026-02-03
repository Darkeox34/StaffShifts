package it.ethereallabs.staffshifts.database;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.database.drivers.Driver;
import it.ethereallabs.staffshifts.database.drivers.MySQLDriver;
import it.ethereallabs.staffshifts.gui.model.LeaderboardEntry;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.models.Staffer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class DatabaseManager {

    private final StaffShifts plugin;
    private final Driver driver;

    public DatabaseManager(StaffShifts plugin) {
        this.plugin = plugin;
        this.driver = new MySQLDriver(plugin);
    }

    public void setup() {
        try {
            driver.setup();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error during Database Setup!");
            e.printStackTrace();
        }
    }

    public void saveShift(Shift shift, boolean isActive) {
        driver.saveShift(shift, isActive);
    }

    public void fetchRecentShifts(UUID uuid, int limit, Consumer<List<Shift>> callback) {
        driver.getRecentShifts(uuid, limit, rs -> {
            List<Shift> shifts = new ArrayList<>();
            try {
                while (rs.next()) {
                    shifts.add(mapResultSetToShift(rs, uuid));
                }
                callback.accept(shifts);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void fetchAllShifts(UUID uuid, Consumer<List<Shift>> callback) {
        driver.getAllShifts(uuid, rs -> {
            List<Shift> shifts = new ArrayList<>();
            try {
                while (rs.next()) {
                    shifts.add(mapResultSetToShift(rs, uuid));
                }
                callback.accept(shifts);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateStafferData(Staffer staffer) {
        driver.updateStafferData(staffer);
    }

    public void getStaffer(UUID uuid, Consumer<ResultSet> callback) {
        driver.getStaffer(uuid, callback);
    }

    public void getStafferByName(String name, Consumer<ResultSet> callback) {
        driver.getStafferByName(name, callback);
    }

    public void fetchWeeklyLeaderboard(long since, Consumer<Map<UUID, LeaderboardEntry>> callback) {
        driver.getWeeklyLeaderboard(since, rs -> {
            Map<UUID, LeaderboardEntry> leaderboard = new LinkedHashMap<>();
            try {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("staff_uuid"));
                    long totalActive = rs.getLong("total_active");
                    String name = rs.getString("username");
                    long lastJoined = rs.getLong("last_joined");

                    leaderboard.put(uuid, new LeaderboardEntry(name, totalActive, lastJoined));
                }
                callback.accept(leaderboard);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void terminateGhostShifts() {
        driver.terminateGhostShifts();
    }

    public void clearAllData() {
        driver.clearAllData();
    }

    public void close() {
        driver.close();
    }

    private Shift mapResultSetToShift(ResultSet rs, UUID uuid) throws SQLException {
        UUID shiftId = UUID.fromString(rs.getString("shift_id"));
        long startTime = rs.getLong("start_time");
        long endTime = rs.getLong("end_time");
        long activeMillis = rs.getLong("active_millis");
        long idleMillis = rs.getLong("idle_millis");
        String notesRaw = rs.getString("notes");
        List<String> notes = new ArrayList<>();
        if (notesRaw != null && !notesRaw.isEmpty()) {
            Collections.addAll(notes, notesRaw.split(";;;"));
        }
        return new Shift(shiftId, uuid, startTime, endTime, activeMillis, idleMillis, notes);
    }
}