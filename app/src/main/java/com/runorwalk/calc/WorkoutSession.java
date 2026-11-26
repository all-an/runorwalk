package com.runorwalk.calc;

import java.util.ArrayDeque;

/**
 * Accumulates distance, pace and Zone 2 time from a stream of GPS fixes.
 * Pure Java: no Android dependencies.
 */
public final class WorkoutSession {
    static final float MAX_ACCURACY_M = 25f;
    static final double MIN_STEP_M = 3.0;
    static final double MAX_SPEED_MS = 12.0;
    static final long PACE_WINDOW_MS = 15_000;
    static final double MIN_WINDOW_M = 5.0;

    private final ZoneTwo zone;
    private final long startMs;
    private final ArrayDeque<double[]> window = new ArrayDeque<>();

    private TrackPoint last;
    private float lastAccuracyM = Float.NaN;
    private double distanceM;
    private long zoneTwoMs;
    private double zoneTwoM;

    public WorkoutSession(ZoneTwo zone, long startMs) {
        this.zone = zone;
        this.startMs = startMs;
    }

    /** Feeds one GPS fix; returns true when it was accepted (not filtered as noise). */
    public boolean onPoint(TrackPoint p) {
        lastAccuracyM = p.accuracyM();
        if (p.accuracyM() > MAX_ACCURACY_M) {
            return false;
        }
        if (last == null) {
            last = p;
            window.add(new double[] {p.timeMs(), 0});
            return true;
        }
        long dt = p.timeMs() - last.timeMs();
        if (dt <= 0) {
            return false;
        }
        double step = Geo.distanceMeters(last, p);
        if (step < MIN_STEP_M || step / (dt / 1000.0) > MAX_SPEED_MS) {
            return false;
        }
        distanceM += step;
        last = p;
        window.add(new double[] {p.timeMs(), distanceM});
        trimWindow(p.timeMs());
        if (zone.contains(currentPaceSecPerKm())) {
            zoneTwoMs += dt;
            zoneTwoM += step;
        }
        return true;
    }

    /** Drops samples that are no longer needed: keeps the newest sample at or before the window start. */
    private void trimWindow(long nowMs) {
        long windowStart = nowMs - PACE_WINDOW_MS;
        while (window.size() > 2) {
            double[] second = window.stream().skip(1).findFirst().orElseThrow();
            if (second[0] > windowStart) {
                break;
            }
            window.removeFirst();
        }
    }

    /** Accuracy of the latest received fix (accepted or not) in meters, NaN if none yet. */
    public float lastAccuracyM() {
        return lastAccuracyM;
    }

    public boolean hasFix() {
        return last != null;
    }

    public double distanceMeters() {
        return distanceM;
    }

    public long elapsedMs(long nowMs) {
        return Math.max(0, nowMs - startMs);
    }

    /** Pace over the last ~15 seconds in sec/km, or NaN when not enough movement. */
    public double currentPaceSecPerKm() {
        double[] first = window.peekFirst();
        double[] end = window.peekLast();
        if (first == null || end == null) {
            return Double.NaN;
        }
        double meters = end[1] - first[1];
        if (meters < MIN_WINDOW_M) {
            return Double.NaN;
        }
        return ((end[0] - first[0]) / 1000.0) / (meters / 1000.0);
    }

    /** Whole-workout average pace in sec/km, or NaN before any distance. */
    public double averagePaceSecPerKm(long nowMs) {
        if (distanceM <= 0) {
            return Double.NaN;
        }
        return (elapsedMs(nowMs) / 1000.0) / (distanceM / 1000.0);
    }

    public long zoneTwoMs() {
        return zoneTwoMs;
    }

    public double zoneTwoMeters() {
        return zoneTwoM;
    }

    /** Share of elapsed time spent inside Zone 2, 0..100. */
    public int zoneTwoPercent(long nowMs) {
        long elapsed = elapsedMs(nowMs);
        return elapsed <= 0 ? 0 : (int) Math.min(100, Math.round(zoneTwoMs * 100.0 / elapsed));
    }

    public ZoneTwo zone() {
        return zone;
    }
}

