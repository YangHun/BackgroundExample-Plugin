package com.eg.background;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.unity3d.player.UnityPlayer;

public class DownloaderService extends Service {

    ServiceThread thread = null;

    public DownloaderService() {
    }

    public enum MessageType {
        SEND_THREAD_FORCE_STOP(1), SEND_THREAD_COMPLETE(2);

        private int value;
        MessageType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
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
        handleStart(intent);
        return START_NOT_STICKY;
    }

    private void handleStart(Intent intent) {
        assert intent != null;
        String[] param = intent.getStringArrayExtra("path");
        ServiceHandler handler = new ServiceHandler();
        assert param != null;
        thread = new ServiceThread(handler, getApplicationContext(), param[0], param[1]);
        thread.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalNotification notification = new LocalNotification(getApplicationContext());
            PendingIntent pendingIntent = PendingIntent.getActivity(DownloaderService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            startForeground(62001, notification.Send(pendingIntent, "컨텐츠 다운로드를 시작합니다", "앱으로 이동해 컨텐츠 미리보기"));
        }
    }

    private void handleFinish() {
        Intent intent = new Intent(DownloaderService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(DownloaderService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        LocalNotification notification = new LocalNotification(getApplicationContext());
        notification.Send(pendingIntent, "컨텐츠 다운로드가 완료되었어요.", "앱으로 이동해 컨텐츠 미리보기");
        stopSelf();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(Service.STOP_FOREGROUND_DETACH);
        }
    }


    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.what == MessageType.SEND_THREAD_FORCE_STOP.getValue()) {
                if (thread != null) {
                    thread.stopForever();
                    thread = null;
                }
            }
            else if (msg.what == MessageType.SEND_THREAD_COMPLETE.getValue()) {
                handleFinish();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (thread != null) {
            thread.stopForever();
            thread = null;
        }
        Log.d("test", "onDestroy Called");
        UnityPlayer.UnitySendMessage("DownloadManager", "OnDestroyService", "Service Destroyed");
        super.onDestroy();
    }
}
