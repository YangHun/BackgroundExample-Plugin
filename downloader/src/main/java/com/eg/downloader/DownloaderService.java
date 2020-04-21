package com.eg.downloader;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;

public class DownloaderService extends Service {

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UNITYCALL", "onCreate of Donwloader Service!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("UNITYCALL", "onStartCommand Enter");

        return START_REDELIVER_INTENT;
    }

    private void SendNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test", "onDestroy Called");
    }
}
