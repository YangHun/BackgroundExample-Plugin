package com.eg.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.unity3d.player.UnityPlayer;

import java.io.File;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;
    double progress = 0.0f;

    Context context;

    FirebaseStorage storage;

    public ServiceThread(Handler handler, Context context, String storageUrl, String path) {
        this.handler = handler;
        this.context = context;

        Log.e("UNITYCALL", "ServiceThread Created! url = " + storageUrl + " / path = " + path);

        storage = FirebaseStorage.getInstance(storageUrl);
        GetImageFromPath(path);
    }

    public void stopForever()
    {
        isRun = false;
    }

    public void run() {
        while(isRun) {
            Message msg = Message.obtain();
            msg.obj = progress;
            handler.sendMessage(msg);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                getStackTrace();
            }
        }
    }

    private void GetImageFromPath(String name) {
        StorageReference root = storage.getReference();
        final StorageReference target = root.child(name);

        Log.d("UNITYCALL", "start download [file] " + name);

        try {

            //final File local = new File(context.getFilesDir(), name);

            final File local = new File(context.getExternalFilesDir(null), name);

            Log.d("UNITYCALL", "filepath : " + local.getAbsolutePath());

            target.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // 다운로드 성공 후 할 일
                    isRun = false;
                    Log.d("UNITYCALL", "file download success!");

                    UnityPlayer.UnitySendMessage("DownloadManager", "OnSuccessDownloadFile", local.getAbsolutePath());
                    Bitmap bitmap = BitmapFactory.decodeFile(local.getAbsolutePath());


                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // 진행 상태 표시
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress > 100.0) progress = 100.0f;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 실패했을 경우
                    isRun = false;
                    Log.e("UNITYCALL", "file download failure : [file]" + target.getPath());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
