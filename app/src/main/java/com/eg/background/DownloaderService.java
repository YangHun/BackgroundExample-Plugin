package com.eg.background;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.unity3d.player.UnityPlayer;

public class DownloaderService extends Service {

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

        String[] param = intent.getStringArrayExtra("path");
        ServiceHandler handler = new ServiceHandler();

        thread = new ServiceThread(handler, getApplicationContext(), param[0], param[1]);
        thread.start();

        return START_NOT_STICKY;
    }

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Intent intent = new Intent (DownloaderService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(DownloaderService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            LocalNotification notification = new LocalNotification(getApplicationContext());
            notification.Send(pendingIntent, "컨텐츠 다운로드가 완료되었어요.", "앱으로 이동해 컨텐츠 미리보기");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UnityPlayer.UnitySendMessage("DownloadManager", "OnDestroyService", "Service Destroyed");
        thread.stopForever();
        thread = null;
        Log.d("test", "onDestroy Called");
    }
}
