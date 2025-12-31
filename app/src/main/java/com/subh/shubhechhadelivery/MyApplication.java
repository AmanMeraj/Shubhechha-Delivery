package com.subh.shubhechhadelivery;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static final String NOTIFICATION_CHANNEL_ID = "order_notifications_v2";

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Create notification channel on app startup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (notificationManager == null) {
            Log.e(TAG, "NotificationManager is null");
            return;
        }

        // Delete old channel if exists (to update settings)
        NotificationChannel existingChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
        if (existingChannel != null) {
            notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
            Log.d(TAG, "Deleted existing notification channel");
        }

        // Get custom sound URI
        Uri soundUri = getNotificationSoundUri();

        // Create audio attributes
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build();

        // Create the notification channel
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.setDescription("Notifications for new orders and updates");
        channel.setSound(soundUri, audioAttributes);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 500, 200, 500});
        channel.enableLights(true);
        channel.setShowBadge(true);

        notificationManager.createNotificationChannel(channel);
        Log.d(TAG, "Notification channel created successfully with sound: " + soundUri);
    }

    private Uri getNotificationSoundUri() {
        try {
            // Try custom sound
            Uri customSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);

            // Verify the resource exists
            getContentResolver().openInputStream(customSound).close();
            Log.d(TAG, "Using custom notification sound");
            return customSound;
        } catch (Exception e) {
            Log.e(TAG, "Custom sound not found, using default", e);
            // Fallback to default sound
            return android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION);
        }
    }
}
