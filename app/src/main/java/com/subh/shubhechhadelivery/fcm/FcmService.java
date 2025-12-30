package com.subh.shubhechhadelivery.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.subh.shubhechhadelivery.Activities.SplashActivity;
import com.subh.shubhechhadelivery.MyApplication;
import com.subh.shubhechhadelivery.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FcmService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        // Check if message contains notification payload (fallback)
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification payload");
            handleNotificationMessage(remoteMessage);
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String image = data.get("image");
        String channelId = data.get("channelId");
        String clickAction = data.get("clickAction");
        String pid = data.get("pid");

        Log.d(TAG, "Data - Title: " + title);
        Log.d(TAG, "Data - Body: " + body);
        Log.d(TAG, "Data - Image: " + image);
        Log.d(TAG, "Data - ChannelId: " + channelId);
        Log.d(TAG, "Data - PID: " + pid);

        sendNotification(title, body, image, channelId, clickAction, pid);
    }

    private void handleNotificationMessage(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String image = remoteMessage.getNotification().getImageUrl() != null ?
                remoteMessage.getNotification().getImageUrl().toString() : "";
        String channelId = remoteMessage.getNotification().getChannelId();
        String clickAction = remoteMessage.getNotification().getClickAction();
        String pid = remoteMessage.getData().get("pid");

        sendNotification(title, body, image, channelId, clickAction, pid);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String title, String messageBody, String img,
                                  String channelId, String clickAction, String pid) {

        // Validate required fields
        if (title == null || messageBody == null) {
            Log.e(TAG, "Title or body is null, cannot show notification");
            return;
        }

        Intent intent;
        if (clickAction != null && !clickAction.isEmpty() && pid != null) {
            intent = new Intent(clickAction);
            try {
                intent.putExtra("id", Integer.parseInt(pid));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid pid: " + pid, e);
                intent = new Intent(this, SplashActivity.class);
            }
        } else {
            intent = new Intent(this, SplashActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Get custom sound URI
        Uri soundUri = getNotificationSoundUri();
        Log.d(TAG, "Using sound URI: " + soundUri);

        // Use the channel from Application class or provided channelId
        String finalChannelId = (channelId == null || channelId.isEmpty()) ?
                MyApplication.NOTIFICATION_CHANNEL_ID : channelId;

        Log.d(TAG, "Using channel ID: " + finalChannelId);

        // Build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, finalChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        // Generate unique notification ID based on timestamp
        int notificationId = (int) System.currentTimeMillis();

        // Show notification immediately
        showNotification(notificationBuilder, notificationId);
        Log.d(TAG, "Notification displayed with ID: " + notificationId);

        // Load image asynchronously if available
        if (img != null && !img.isEmpty()) {
            final String finalTitle = title;
            final String finalMessageBody = messageBody;
            final String finalChannelIdForImage = finalChannelId;

            executorService.execute(() -> {
                Bitmap bitmap = getBitmapfromUrl(img);
                if (bitmap != null) {
                    NotificationCompat.Builder updatedBuilder = new NotificationCompat.Builder(this, finalChannelIdForImage)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(finalTitle)
                            .setContentText(finalMessageBody)
                            .setAutoCancel(true)
                            .setSound(soundUri)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(bitmap)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap)
                                    .bigLargeIcon((Bitmap) null)
                                    .setBigContentTitle(finalTitle)
                                    .setSummaryText(finalMessageBody));

                    showNotification(updatedBuilder, notificationId);
                    Log.d(TAG, "Notification updated with image");
                } else {
                    Log.e(TAG, "Failed to load image for notification");
                }
            });
        }
    }

    private void showNotification(NotificationCompat.Builder builder, int notificationId) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification posted successfully");
        } else {
            Log.e(TAG, "NotificationManager is null");
        }
    }

    private Uri getNotificationSoundUri() {
        try {
            Uri customSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
            // Try to open the sound file to verify it exists
            InputStream is = getContentResolver().openInputStream(customSound);
            if (is != null) {
                is.close();
                Log.d(TAG, "Custom sound file found and verified");
                return customSound;
            }
        } catch (Exception e) {
            Log.e(TAG, "Custom sound file not found or cannot be opened", e);
        }

        // Fallback to default notification sound
        Uri defaultSound = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION);
        Log.d(TAG, "Using default notification sound: " + defaultSound);
        return defaultSound;
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        HttpURLConnection connection = null;
        InputStream input = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                Log.d(TAG, "Image loaded successfully from: " + imageUrl);
                return bitmap;
            } else {
                Log.e(TAG, "Failed to load image. Response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image from URL: " + imageUrl, e);
        } finally {
            try {
                if (input != null) input.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error closing connection", e);
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}