package com.eg.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class DownloaderService extends Service {

    NotificationManager notificationManager;
    Notification notification;
    ServiceThread thread;

    public DownloaderService() {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("UNITYCALL", "onStartCommand Enter");

        ServiceHandler handler = new ServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();

        return START_STICKY;
    }

    class ServiceHandler extends Handler {

        private final String NOTIFICATION_CHANNEL_ID = DownloaderService.class.getSimpleName();

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Log.e("UNITYCALL", "handleMessage Enter?");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.stopForever();
        thread = null;
        Log.d("test", "onDestroy Called");
    }
}
