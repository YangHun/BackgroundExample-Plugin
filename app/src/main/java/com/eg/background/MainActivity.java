package com.eg.background;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("UNITYCALL", "onCreate() of MainActivity is called");

        Intent intent = new Intent(getApplication(), DownloaderService.class);
        startService(intent);
    }

    public void startUnityActivity()
    {
        Log.e("UNITYCALL", "startUnityActivity() is called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("UNITYCALL", "Unity Player Activity is destroyed!");

        Intent intent = new Intent(getApplicationContext(), DownloaderService.class);
        stopService(intent);
    }
}