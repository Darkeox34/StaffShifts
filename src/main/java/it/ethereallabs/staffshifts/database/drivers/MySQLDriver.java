package it.ethereallabs.staffshifts.database.drivers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.models.Staffer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;
import java.util.function.Consumer;

public class MySQLDriver implements Driver {

    private final StaffShifts plugin;
    private HikariDataSource dataSource;

    public MySQLDriver(StaffShifts plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() throws SQLException {
        String host = plugin.getConfig().getString("database.mysql.host");
        int port = plugin.getConfig().getInt("database.mysql.port");
        String database = plugin.getConfig().getString("database.mysql.database");
        String username = plugin.getConfig().getString("database.mysql.username");
        String password = plugin.getConfig().getString("database.mysql.password");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);

        createTable();
    }

    private void createTable() throws SQLException {
        String shiftsTable = "CREATE TABLE IF NOT EXISTS staff_shifts (" +
                "shift_id VARCHAR(36) PRIMARY KEY, " +
                "staff_uuid VARCHAR(36) NOT NULL, " +
                "start_time BIGINT NOT NULL, " +
                "end_time BIGINT, " +
                "active_millis BIGINT DEFAULT 0, " +
                "idle_millis BIGINT DEFAULT 0, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "notes TEXT, " +
                "last_updated BIGINT DEFAULT 0" +
                ")";

        String stafferTable = "CREATE TABLE IF NOT EXISTS staffer (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "username VARCHAR(16), " +
                "total_active_millis BIGINT DEFAULT 0, " +
                "total_idle_millis BIGINT DEFAULT 0, " +
                "last_joined BIGINT" +
                ")";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(shiftsTable);
            stmt.execute(stafferTable);
        }
    }

    @Override
    public void saveShift(Shift shift, boolean isActive) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO staff_shifts (shift_id, staff_uuid, start_time, end_time, active_millis, idle_millis, is_active, notes, last_updated) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                     "ON DUPLICATE KEY UPDATE end_time = ?, active_millis = ?, idle_millis = ?, is_active = ?, notes = ?, last_updated = ?")) {

                    String notes = String.join(";;;", shift.getNotes());
                    long now = System.currentTimeMillis();

                    ps.setString(1, shift.getShiftId().toString());
                    ps.setString(2, shift.getStaffUuid().toString());
                    ps.setLong(3, shift.getStartTime());
                    ps.setLong(4, shift.getEndTime());
                    ps.setLong(5, shift.getActiveMillis());
                    ps.setLong(6, shift.getIdleMillis());
                    ps.setBoolean(7, isActive);
                    ps.setString(8, notes);
                    ps.setLong(9, now);

                    ps.setLong(10, shift.getEndTime());
                    ps.setLong(11, shift.getActiveMillis());
                    ps.setLong(12, shift.getIdleMillis());
                    ps.setBoolean(13, isActive);
                    ps.setString(14, notes);
                    ps.setLong(15, now);

                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void getRecentShifts(UUID uuid, int limit, Consumer<ResultSet> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "SELECT * FROM staff_shifts WHERE staff_uuid = ? AND is_active = FALSE ORDER BY start_time DESC LIMIT ?")) {
                    ps.setString(1, uuid.toString());
                    ps.setInt(2, limit);
                    callback.accept(ps.executeQuery());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void clearAllData() {
        try (Connection conn = dataSource.getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate("TRUNCATE TABLE staff_shifts");
            st.executeUpdate("TRUNCATE TABLE staffer");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getWeeklyLeaderboard(long since, Consumer<ResultSet> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "SELECT s.staff_uuid, SUM(s.active_millis) as total_active, st.username, st.last_joined " +
                                     "FROM staff_shifts s LEFT JOIN staffer st ON s.staff_uuid = st.uuid " +
                                     "WHERE s.start_time >= ? GROUP BY s.staff_uuid ORDER BY total_active DESC")) {
                    ps.setLong(1, since);
                    callback.accept(ps.executeQuery());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void updateStafferData(Staffer staffer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO staffer (uuid, username, total_active_millis, total_idle_millis, last_joined) " +
                                     "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                                     "username = VALUES(username), total_active_millis = VALUES(total_active_millis), " +
                                     "total_idle_millis = VALUES(total_idle_millis), last_joined = VALUES(last_joined)")) {
                    ps.setString(1, staffer.getUuid().toString());
                    ps.setString(2, staffer.getUsername());
                    ps.setLong(3, staffer.getTotalActiveMillis());
                    ps.setLong(4, staffer.getTotalIdleMillis());
                    ps.setLong(5, staffer.getLastJoined());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void getStaffer(UUID uuid, Consumer<ResultSet> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT * FROM staffer WHERE uuid = ?")) {
                    ps.setString(1, uuid.toString());
                    callback.accept(ps.executeQuery());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void terminateGhostShifts() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE staff_shifts SET is_active = FALSE, end_time = last_updated WHERE is_active = TRUE")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (dataSource != null) dataSource.close();
    }
}