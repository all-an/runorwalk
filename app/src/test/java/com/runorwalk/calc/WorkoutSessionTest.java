package com.runorwalk.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WorkoutSessionTest {
    /** ~111.2 m per 0.001 degree of latitude. */
    private static final double DEG_PER_METER = 0.001 / 111.2;

    private static TrackPoint point(long seconds, double meters, float accuracy) {
        return new TrackPoint(seconds * 1000, 45.0 + meters * DEG_PER_METER, 10.0, accuracy);
    }

    @Test
    public void haversineMatchesKnownDistance() {
        double d = Geo.distanceMeters(new TrackPoint(0, 45.0, 10.0, 5), new TrackPoint(0, 45.001, 10.0, 5));
        assertEquals(111.2, d, 0.5);
    }

    @Test
    public void accumulatesDistanceAndPace() {
        WorkoutSession s = new WorkoutSession(ZoneTwo.DEFAULT, 0);
        // 4 m/s => 250 sec/km
        for (int i = 0; i <= 10; i++) {
            assertTrue(s.onPoint(point(i, i * 4.0, 5)));
        }
        assertEquals(40.0, s.distanceMeters(), 0.5);
        assertEquals(250, s.currentPaceSecPerKm(), 5);
        assertEquals(250, s.averagePaceSecPerKm(10_000), 5);
        assertTrue(s.hasFix());
    }

    @Test
    public void rejectsInaccurateFixes() {
        WorkoutSession s = new WorkoutSession(ZoneTwo.DEFAULT, 0);
        assertFalse(s.onPoint(point(0, 0, 50)));
        assertFalse(s.hasFix());
    }

    @Test
    public void remembersAccuracyOfRejectedFixes() {
        WorkoutSession s = new WorkoutSession(ZoneTwo.DEFAULT, 0);
        assertTrue(Float.isNaN(s.lastAccuracyM()));
        s.onPoint(point(0, 0, 40));
        assertEquals(40f, s.lastAccuracyM(), 0.01f);
    }

    @Test
    public void ignoresJitterAndTeleports() {
        WorkoutSession s = new WorkoutSession(ZoneTwo.DEFAULT, 0);
        s.onPoint(point(0, 0, 5));
        assertFalse("jitter under 3 m", s.onPoint(point(1, 1.5, 5)));
        assertFalse("500 m in 1 s", s.onPoint(point(2, 500, 5)));
        assertFalse("same timestamp", s.onPoint(point(0, 10, 5)));
        assertEquals(0.0, s.distanceMeters(), 0.001);
    }

    @Test
    public void countsOnlyZoneTwoTimeAndDistance() {
        // zone 6:00-7:00 => 2.38 to 2.78 m/s. Run at 2.5 m/s (400 s/km) for 20 s, then 4 m/s for 20 s.
        WorkoutSession s = new WorkoutSession(ZoneTwo.DEFAULT, 0);
        s.onPoint(point(0, 0, 5));
        double meters = 0;
        for (int t = 1; t <= 20; t++) {
            meters += 2.5;
            s.onPoint(point(t, meters, 5));
        }
        long zoneMs = s.zoneTwoMs();
        assertTrue("zone time recorded while at 6:40", zoneMs > 10_000);
        for (int t = 21; t <= 40; t++) {
            meters += 4.0;
            s.onPoint(point(t, meters, 5));
        }
        assertTrue("fast part adds little zone time", s.zoneTwoMs() < 30_000);
        assertTrue(s.zoneTwoMeters() > 0 && s.zoneTwoMeters() < s.distanceMeters());
        assertTrue(s.zoneTwoPercent(40_000) > 0 && s.zoneTwoPercent(40_000) < 100);
    }

    @Test
    public void unknownPaceBeforeMovement() {
        WorkoutSession s = new WorkoutSession(ZoneTwo.DEFAULT, 0);
        assertTrue(Double.isNaN(s.currentPaceSecPerKm()));
        assertTrue(Double.isNaN(s.averagePaceSecPerKm(5000)));
        assertEquals(0, s.zoneTwoPercent(0));
    }
}



