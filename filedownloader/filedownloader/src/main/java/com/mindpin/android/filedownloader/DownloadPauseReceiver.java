package com.mindpin.android.filedownloader;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DownloadPauseReceiver extends BroadcastReceiver {
    public FileDownloader fd;
    public int file_size = 0;
    public int downloaded_size = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Debugging pause to receive the latest fd", "true");

        fd = intent.getParcelableExtra("download_manager");
        Log.i("Debugging pause to receive the latest fd file_size", Integer.toString(fd.file_size));
        file_size = fd.file_size;

        Log.i("Debugging pause to receive the latest fd downloaded_size", Integer.toString(fd.downloaded_size));
        downloaded_size = fd.downloaded_size;

    }
}
