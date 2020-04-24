package com.eg.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class LocalNotification {

    private Context context;
    private static final String NOTIFICATION_CHANNEL = "62001";

    public LocalNotification(Context context) {
        this.context = context;
    }

    public Notification Send(PendingIntent pendingIntent, String title, String text) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(res.getIdentifier("small_icon", "drawable", context.getPackageName()))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "API version for Oreo+";

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, "downloader", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }

        assert manager != null;
        Notification result = builder.build();
        manager.notify(62001, result);

        return result;
    }
}
