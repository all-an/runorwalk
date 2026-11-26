package com.runorwalk.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import com.runorwalk.R;
import com.runorwalk.calc.PaceFormat;
import com.runorwalk.calc.WorkoutSession;
import com.runorwalk.track.SessionHolder;
import com.runorwalk.track.TrackingService;

/** Single screen: distance, time, pace and Zone 2 amount. No map. */
public final class MainActivity extends Activity {
    private static final int REQUEST_PERMISSIONS = 1;
    private static final long REFRESH_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable refresh = this::refresh;

    private TextView distanceView;
    private TextView timeView;
    private TextView paceView;
    private TextView avgPaceView;
    private TextView zoneView;
    private TextView statusView;
    private Button toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distanceView = findViewById(R.id.distance);
        timeView = findViewById(R.id.time);
        paceView = findViewById(R.id.pace);
        avgPaceView = findViewById(R.id.avgPace);
        zoneView = findViewById(R.id.zoneTwo);
        statusView = findViewById(R.id.status);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(v -> onToggle());
        findViewById(R.id.settingsButton)
                .setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(refresh);
        super.onPause();
    }

    private void onToggle() {
        if (SessionHolder.isRunning()) {
            startService(serviceIntent(TrackingService.ACTION_STOP));
            refresh();
        } else if (hasLocationPermission()) {
            startTracking();
        } else {
            requestPermissions(permissionsNeeded(), REQUEST_PERMISSIONS);
        }
    }

    private void startTracking() {
        startForegroundService(serviceIntent(TrackingService.ACTION_START));
        handler.postDelayed(this::refresh, 200);
    }

    private Intent serviceIntent(String action) {
        return new Intent(this, TrackingService.class).setAction(action);
    }

    private boolean hasLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private String[] permissionsNeeded() {
        return Build.VERSION.SDK_INT >= 33
                ? new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS}
                : new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && hasLocationPermission()) {
            startTracking();
        } else {
            statusView.setText(R.string.status_permission);
        }
    }

    private void refresh() {
        handler.removeCallbacks(refresh);
        WorkoutSession session = SessionHolder.session();
        boolean running = SessionHolder.isRunning();
        toggleButton.setText(running ? R.string.stop : R.string.start);
        if (session != null) {
            render(session, running);
        }
        if (running) {
            handler.postDelayed(refresh, REFRESH_MS);
        }
    }

    private String statusText(WorkoutSession session, boolean running) {
        if (!running) {
            return getString(R.string.status_finished);
        }
        if (session.hasFix()) {
            return getString(R.string.status_tracking);
        }
        float accuracy = session.lastAccuracyM();
        return Float.isNaN(accuracy)
                ? getString(R.string.status_waiting_gps)
                : getString(R.string.status_weak_gps, Math.round(accuracy));
    }

    private void render(WorkoutSession session, boolean running) {
        long now = System.currentTimeMillis();
        distanceView.setText(PaceFormat.formatKm(session.distanceMeters()));
        timeView.setText(PaceFormat.formatDuration(session.elapsedMs(now)));
        paceView.setText(PaceFormat.formatPace(session.currentPaceSecPerKm()));
        avgPaceView.setText(PaceFormat.formatPace(session.averagePaceSecPerKm(now)));
        zoneView.setText(getString(R.string.zone_two_value,
                PaceFormat.formatDuration(session.zoneTwoMs()),
                PaceFormat.formatKm(session.zoneTwoMeters()),
                session.zoneTwoPercent(now)));
        statusView.setText(statusText(session, running));
    }
}

