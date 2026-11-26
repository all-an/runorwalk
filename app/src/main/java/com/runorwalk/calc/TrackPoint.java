package com.runorwalk.calc;

/** One GPS fix. Pure data, no Android types, so it can be unit tested on the JVM. */
public record TrackPoint(long timeMs, double latitude, double longitude, float accuracyM) {
}
