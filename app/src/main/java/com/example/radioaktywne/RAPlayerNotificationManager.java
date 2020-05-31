package com.example.radioaktywne;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.util.NotificationUtil;

public class RAPlayerNotificationManager extends PlayerNotificationManager {
    private GestureDetectorCompat mGesture;

    public RAPlayerNotificationManager(Context context, String channelId, int notificationId, MediaDescriptionAdapter mediaDescriptionAdapter, @Nullable NotificationListener notificationListener) {
        super(context, channelId, notificationId, mediaDescriptionAdapter, notificationListener);
        mGesture = new GestureDetectorCompat(context, new RAGestureListener());
    }

    public static RAPlayerNotificationManager createWithNotificationChannel(
            Context context,
            String channelId,
            @StringRes int channelName,
            @StringRes int channelDescription,
            int notificationId,
            MediaDescriptionAdapter mediaDescriptionAdapter,
            @Nullable NotificationListener notificationListener) {
        NotificationUtil.createNotificationChannel(
                context, channelId, channelName, channelDescription, NotificationUtil.IMPORTANCE_LOW);
        return new RAPlayerNotificationManager(
                context, channelId, notificationId, mediaDescriptionAdapter, notificationListener);
    }

    class RAGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            return true;
        }
    }
}
