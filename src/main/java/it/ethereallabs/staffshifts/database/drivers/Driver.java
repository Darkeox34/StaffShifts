package it.ethereallabs.staffshifts.database.drivers;

import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.models.Staffer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public interface Driver {

    void setup() throws SQLException;

    void clearAllData();

    void saveShift(Shift shift, boolean isActive);

    void getRecentShifts(UUID uuid, int limit, Consumer<ResultSet> callback);

    void getAllShifts(UUID uuid, Consumer<ResultSet> callback);

    void getWeeklyLeaderboard(long since, Consumer<ResultSet> callback);

    void updateStafferData(Staffer staffer);

    void getStaffer(UUID uuid, Consumer<ResultSet> callback);

    void getStafferByName(String name, Consumer<ResultSet> callback);

    void terminateGhostShifts();

    void close();
}