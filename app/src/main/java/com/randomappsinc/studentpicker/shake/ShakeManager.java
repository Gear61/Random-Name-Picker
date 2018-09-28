package com.randomappsinc.studentpicker.shake;

import android.support.annotation.Nullable;

// Utility singleton class to propagate shake detection signals
public class ShakeManager {

    public interface Listener {
        void onShakeDetected();
    }

    private static ShakeManager instance;

    public static ShakeManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized ShakeManager getSync() {
        if (instance == null) {
            instance = new ShakeManager();
        }
        return instance;
    }

    @Nullable private Listener listener;

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public void unregisterListener() {
        listener = null;
    }

    public void onShakeDetected() {
        if (listener != null) {
            listener.onShakeDetected();
        }
    }
}
