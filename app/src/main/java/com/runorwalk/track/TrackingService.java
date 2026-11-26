package com.runorwalk.track;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import com.runorwalk.R;
import com.runorwalk.calc.TrackPoint;
import com.runorwalk.calc.WorkoutSession;
import com.runorwalk.ui.MainActivity;
import com.runorwalk.ui.ZoneSettings;

/** Foreground service that keeps GPS running while the screen is off. */
public final class TrackingService extends Service {
    public static final String ACTION_START = "com.runorwalk.START";
    public static final String ACTION_STOP = "com.runorwalk.STOP";
    private static final String CHANNEL_ID = "tracking";
    private static final int NOTIFICATION_ID = 1;

    private LocationManager locationManager;
    private final LocationListener listener = this::onLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopTracking();
            return START_NOT_STICKY;
        }
        startTracking();
        return START_NOT_STICKY;
    }

    @SuppressWarnings("MissingPermission")
    private void startTracking() {
        if (SessionHolder.isRunning()) {
            return;
        }
        startForeground(NOTIFICATION_ID, buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        SessionHolder.start(new WorkoutSession(ZoneSettings.load(this), System.currentTimeMillis()));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, listener);
        } catch (SecurityException e) {
            stopTracking();
        }
    }

    private void onLocation(Location location) {
        WorkoutSession session = SessionHolder.session();
        if (session != null && SessionHolder.isRunning()) {
            session.onPoint(new TrackPoint(System.currentTimeMillis(),
                    location.getLatitude(), location.getLongitude(), location.getAccuracy()));
        }
    }

    private void stopTracking() {
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
        SessionHolder.stop();
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private Notification buildNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(new NotificationChannel(
                CHANNEL_ID, getString(R.string.notification_channel), NotificationManager.IMPORTANCE_LOW));
        PendingIntent open = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(open)
                .setOngoing(true)
                .build();
    }

    @Override
    public void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
