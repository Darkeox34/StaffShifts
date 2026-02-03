package it.ethereallabs.staffshifts.utils;

public class TimeUtils {
    public static String formatDuration(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static long parseDuration(String input) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)([hms])");
        java.util.regex.Matcher matcher = pattern.matcher(input);

        long totalMillis = 0;
        boolean found = false;

        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "h":
                    totalMillis += value * 3600000;
                    break;
                case "m":
                    totalMillis += value * 60000;
                    break;
                case "s":
                    totalMillis += value * 1000;
                    break;
            }
        }

        return found ? totalMillis : -1;
    }
}
