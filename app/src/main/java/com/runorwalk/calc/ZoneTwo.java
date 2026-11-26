package com.runorwalk.calc;

/**
 * Zone 2 pace range in seconds per km. {@code fastestSecPerKm} is the lower number
 * (e.g. 6:00 = 360), {@code slowestSecPerKm} the higher one (e.g. 7:00 = 420).
 */
public record ZoneTwo(int fastestSecPerKm, int slowestSecPerKm) {
    public static final ZoneTwo DEFAULT = new ZoneTwo(6 * 60, 7 * 60);

    public ZoneTwo {
        if (fastestSecPerKm <= 0 || slowestSecPerKm <= 0) {
            throw new IllegalArgumentException("Paces must be greater than 0:00");
        }
        if (fastestSecPerKm >= slowestSecPerKm) {
            throw new IllegalArgumentException("Fast limit must be quicker than slow limit");
        }
    }

    /** Builds a zone from two {@code m:ss} texts. */
    public static ZoneTwo parse(String fastest, String slowest) {
        return new ZoneTwo(PaceFormat.parsePace(fastest), PaceFormat.parsePace(slowest));
    }

    /** True when the pace lies inside the range (inclusive). NaN is never inside. */
    public boolean contains(double secPerKm) {
        return !Double.isNaN(secPerKm) && secPerKm >= fastestSecPerKm && secPerKm <= slowestSecPerKm;
    }
}
