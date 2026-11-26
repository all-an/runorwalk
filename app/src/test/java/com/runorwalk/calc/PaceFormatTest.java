package com.runorwalk.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PaceFormatTest {
    @Test
    public void parsesMinutesAndSeconds() {
        assertEquals(390, PaceFormat.parsePace("6:30"));
        assertEquals(390, PaceFormat.parsePace(" 06:30 "));
    }

    @Test
    public void rejectsBadPaces() {
        assertThrows(IllegalArgumentException.class, () -> PaceFormat.parsePace(null));
        assertThrows(IllegalArgumentException.class, () -> PaceFormat.parsePace("630"));
        assertThrows(IllegalArgumentException.class, () -> PaceFormat.parsePace("6:60"));
        assertThrows(IllegalArgumentException.class, () -> PaceFormat.parsePace("a:bc"));
        assertThrows(IllegalArgumentException.class, () -> PaceFormat.parsePace("0:00"));
    }

    @Test
    public void formatsPace() {
        assertEquals("6:30", PaceFormat.formatPace(390));
        assertEquals("5:05", PaceFormat.formatPace(304.6));
        assertEquals(PaceFormat.NO_PACE, PaceFormat.formatPace(Double.NaN));
        assertEquals(PaceFormat.NO_PACE, PaceFormat.formatPace(0));
        assertEquals(PaceFormat.NO_PACE, PaceFormat.formatPace(Double.POSITIVE_INFINITY));
    }

    @Test
    public void formatsDistanceAndDuration() {
        assertEquals("1.50", PaceFormat.formatKm(1500));
        assertEquals("00:00", PaceFormat.formatDuration(-5));
        assertEquals("01:05", PaceFormat.formatDuration(65_000));
        assertEquals("1:01:01", PaceFormat.formatDuration(3_661_000));
    }

    @Test
    public void zoneTwoValidatesAndContains() {
        ZoneTwo zone = ZoneTwo.parse("6:00", "7:00");
        assertTrue(zone.contains(360));
        assertTrue(zone.contains(420));
        assertFalse(zone.contains(359));
        assertFalse(zone.contains(421));
        assertFalse(zone.contains(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> ZoneTwo.parse("7:00", "6:00"));
        assertThrows(IllegalArgumentException.class, () -> ZoneTwo.parse("6:00", "6:00"));
    }
}
