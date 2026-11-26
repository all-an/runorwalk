package com.runorwalk.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.runorwalk.calc.ZoneTwo;

/** Persists the Zone 2 pace range in SharedPreferences. */
public final class ZoneSettings {
    private static final String PREFS = "zone2";
    private static final String KEY_FAST = "fastest";
    private static final String KEY_SLOW = "slowest";

    private ZoneSettings() {
    }

    public static ZoneTwo load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return new ZoneTwo(
                prefs.getInt(KEY_FAST, ZoneTwo.DEFAULT.fastestSecPerKm()),
                prefs.getInt(KEY_SLOW, ZoneTwo.DEFAULT.slowestSecPerKm()));
    }

    public static void save(Context context, ZoneTwo zone) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putInt(KEY_FAST, zone.fastestSecPerKm())
                .putInt(KEY_SLOW, zone.slowestSecPerKm())
                .apply();
    }
}
