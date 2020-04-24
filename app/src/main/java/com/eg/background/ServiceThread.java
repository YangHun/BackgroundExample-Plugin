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
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceThread extends Thread {

    private boolean isRun;
    private Context context;
    private FirebaseStorage storage;
    private ArrayList<StorageReference> files = new ArrayList<>();
    private Handler handler;

    private ThreadState state;

    private int totalCount;
    private int completedCount;

    enum ThreadState {
        RETRIEVE_FILES, FINISH_RETRIEVE, DOWNLOAD_FILES, FINISH_DOWNLOAD, IDLE
    }

    ServiceThread(Handler handler, Context context, String storageUrl, String path) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance(storageUrl);
        this.isRun = true;
        this.handler = handler;
        this.state = ThreadState.IDLE;
        this.totalCount = 0;
        this.completedCount = 0;

        Log.e("UNITYCALL", "ServiceThread Created! url = " + storageUrl + " | path = " + path);

        RetrieveFileList(path, null);
    }

    void stopForever()
    {
        isRun = false;
        files.clear();
        state = ThreadState.IDLE;
    }

    public void run() {
        while(isRun) {
            try {
                if (state.equals(ThreadState.FINISH_RETRIEVE)) {
                    UnityPlayer.UnitySendMessage("DownloadManager", "OnSuccessRetrievePath", Integer.toString(files.size()));
                    state = ThreadState.DOWNLOAD_FILES;

                    totalCount = files.size();

                    for (StorageReference i : files) {
                        DownloadFileFromReference(i);
                    }
                    files.clear();
                }
                else if (state.equals(ThreadState.DOWNLOAD_FILES)) {
                    Log.e("UNITYCALL", "Waiting to async downloading... [completed/total] - [" + completedCount + "," + totalCount + "]");
                    if (completedCount >= totalCount) state = ThreadState.FINISH_DOWNLOAD;
                    else sleep(1000);
                }
                else if (state.equals(ThreadState.FINISH_DOWNLOAD)) {
                    // Complete download handle
                    handler.sendEmptyMessage(DownloaderService.MessageType.SEND_THREAD_COMPLETE.getValue());
                    state = ThreadState.IDLE;
                }
                else if (state.equals(ThreadState.IDLE)) {
                    break;
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(DownloaderService.MessageType.SEND_THREAD_FORCE_STOP.getValue());
                getStackTrace();
            }
        }
    }


    private void RetrieveFileList(String path, @Nullable String pageToken) {

        if (state.equals(ThreadState.IDLE)) state = ThreadState.RETRIEVE_FILES;
        if (!state.equals(ThreadState.RETRIEVE_FILES)) return;

        final String targetPath = path;
        Log.d("UNITYCALL", "Retrieve... = " + path);

        StorageReference reference = storage.getReference().child(targetPath);
        Task<ListResult> listPageTask = pageToken != null
                ? reference.list(10, pageToken)
                : reference.list(10);

        listPageTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d("UNITYCALL", "Retrieve Success! pageToken = " + listResult.getPageToken());
                List<StorageReference> items = listResult.getItems();

                files.addAll(items);

                // Recurse onto next page
                if (listResult.getPageToken() != null) {
                    RetrieveFileList(targetPath, listResult.getPageToken());
                }
                else {
                    state = ThreadState.FINISH_RETRIEVE;
                }
            }
        });
    }

    private void DownloadFileFromReference(StorageReference item)
    {
        Log.d("UNITYCALL", "DownloadFileFromReference() enter [ref path]: " + item.getPath());

        try {
            File directory = new File(context.getExternalFilesDir(null), item.getParent() == null
                    ? item.getRoot().getPath()
                    : item.getParent().getPath());
            if (!directory.exists()) directory.mkdirs();

            final File local = new File(directory, item.getName());

            Log.d("UNITYCALL", "local directory : " + directory.getAbsolutePath() + " || local file : " + local.getAbsolutePath());

            final String remotePath = item.getPath();
            item.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // 다운로드 성공 후 할 일
                    Log.d("UNITYCALL", "file download success!");
                    ++completedCount;
                    UnityPlayer.UnitySendMessage("DownloadManager", "OnSuccessDownloadFile", local.getAbsolutePath());
                    //Bitmap bitmap = BitmapFactory.decodeFile(local.getAbsolutePath());
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // 진행 상태 표시
                    //progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 실패했을 경우
                    handler.sendEmptyMessage(DownloaderService.MessageType.SEND_THREAD_FORCE_STOP.getValue());
                    Log.e("UNITYCALL", "file download failure [ref path]: " + remotePath);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
