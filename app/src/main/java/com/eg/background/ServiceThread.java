package com.eg.background;

import android.os.Handler;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler) {
        this.handler = handler;
    }

    public void stopForever()
    {
        isRun = false;
    }

    public void run() {
        while(isRun) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                getStackTrace();
            }
        }
    }
}
