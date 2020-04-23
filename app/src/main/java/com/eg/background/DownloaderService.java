package com.eg.background;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class DownloaderService extends Service {

    ServiceThread thread;
    ServiceHandler handler;

    String storageUrl;


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


        handler = new ServiceHandler();

        String[] param = intent.getStringArrayExtra("path");
        thread = new ServiceThread(handler,  getApplicationContext(), param[0], param[1]);
        thread.start();

        return START_NOT_STICKY;
    }

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
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
