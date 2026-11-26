package com.runorwalk.calc;

/** Geographic math. */
public final class Geo {
    private static final double EARTH_RADIUS_M = 6_371_008.8;

    private Geo() {
    }

    /** Great-circle (haversine) distance between two fixes in meters. */
    public static double distanceMeters(TrackPoint a, TrackPoint b) {
        double lat1 = Math.toRadians(a.latitude());
        double lat2 = Math.toRadians(b.latitude());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.longitude() - a.longitude());
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS_M * Math.asin(Math.sqrt(h));
    }
}
