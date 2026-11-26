package com.runorwalk.track;

import com.runorwalk.calc.WorkoutSession;

/** In-process holder of the active workout, shared by the service (writer) and the UI (reader). */
public final class SessionHolder {
    private static volatile WorkoutSession session;
    private static volatile boolean running;

    private SessionHolder() {
    }

    public static WorkoutSession session() {
        return session;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void start(WorkoutSession newSession) {
        session = newSession;
        running = true;
    }

    public static void stop() {
        running = false;
    }

    public static void clear() {
        session = null;
        running = false;
    }
}
