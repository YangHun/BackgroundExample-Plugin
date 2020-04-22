package com.eg.background;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.MessagingUnityPlayerActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unity3d.player.UnityPlayer;



public class MainActivity extends MessagingUnityPlayerActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("UNITYCALL", "onCreate() of MainActivity is called");

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) signInAnonymous();
    }


    private void signInAnonymous() {
        assert auth != null;
        auth.signInAnonymously().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("UNITYCALL", "signInAnonymous failure:", e);
            }
        });
    }

    public void startUnityActivity()
    {
        Log.e("UNITYCALL", "startUnityActivity() is called");
        Intent intent = new Intent(this, DownloaderService.class);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("UNITYCALL", "Unity Player Activity is destroyed!");

        Intent intent = new Intent(getApplicationContext(), DownloaderService.class);
        stopService(intent);
    }
}