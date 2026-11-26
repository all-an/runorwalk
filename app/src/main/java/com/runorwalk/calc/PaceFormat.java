package com.runorwalk.calc;

import java.util.Locale;

/** Parsing and formatting of paces, distances and durations. Pace unit: seconds per km. */
public final class PaceFormat {
    public static final String NO_PACE = "--:--";

    private PaceFormat() {
    }

    /** Parses {@code m:ss} (e.g. {@code 6:30}) into seconds per km. */
    public static int parsePace(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Pace is required (m:ss)");
        }
        String[] parts = text.trim().split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Use the format m:ss, e.g. 6:30");
        }
        try {
            int minutes = Integer.parseInt(parts[0].trim());
            int seconds = Integer.parseInt(parts[1].trim());
            if (minutes < 0 || seconds < 0 || seconds > 59) {
                throw new IllegalArgumentException("Seconds must be 00-59");
            }
            int total = minutes * 60 + seconds;
            if (total <= 0) {
                throw new IllegalArgumentException("Pace must be greater than 0:00");
            }
            return total;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Use the format m:ss, e.g. 6:30");
        }
    }

    /** Formats seconds per km as {@code m:ss}; {@link #NO_PACE} for unknown or absurd values. */
    public static String formatPace(double secPerKm) {
        if (Double.isNaN(secPerKm) || Double.isInfinite(secPerKm) || secPerKm <= 0 || secPerKm >= 100 * 60) {
            return NO_PACE;
        }
        int total = (int) Math.round(secPerKm);
        return String.format(Locale.US, "%d:%02d", total / 60, total % 60);
    }

    /** Formats meters as kilometers with two decimals. */
    public static String formatKm(double meters) {
        return String.format(Locale.US, "%.2f", meters / 1000.0);
    }

    /** Formats a duration as {@code mm:ss}, or {@code h:mm:ss} from one hour on. */
    public static String formatDuration(long ms) {
        long totalSeconds = Math.max(0, ms) / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return h > 0
                ? String.format(Locale.US, "%d:%02d:%02d", h, m, s)
                : String.format(Locale.US, "%02d:%02d", m, s);
    }
}
