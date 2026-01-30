package it.ethereallabs.staffshifts.utils;

public class TimeUtils {
    public static String formatDuration(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
