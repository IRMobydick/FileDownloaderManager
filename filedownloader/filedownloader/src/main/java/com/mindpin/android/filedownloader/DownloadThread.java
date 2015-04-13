package com.mindpin.android.filedownloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class DownloadThread extends Thread {
    private static final String TAG = "DownloadThread";
    private File save_file;
    private URL download_url;
    private int block;
    
    private int thread_id = -1;
    private int downloaded_length;
    private boolean finish = false;
    private FileDownloader downloader;

    public DownloadThread(FileDownloader downloader,
                          URL download_url,
                          File save_file,
                          int block,
                          int downloaded_length,
                          int thread_id) {

        this.download_url = download_url;
        this.save_file = save_file;
        this.block = block;
        this.downloader = downloader;
        this.thread_id = thread_id;
        this.downloaded_length = downloaded_length;
    }

    @Override
    public void run() {
        //No download is complete
        if(downloaded_length < block){
            try {
                HttpURLConnection http = (HttpURLConnection) download_url.openConnection();
                http.setConnectTimeout(5 * 1000);
                http.setRequestMethod("GET");
                http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                http.setRequestProperty("Accept-Language", "zh-CN");
                http.setRequestProperty("Referer", download_url.toString());
                http.setRequestProperty("Charset", "UTF-8");

                int start_pos = block * (thread_id - 1) + downloaded_length;
                int end_pos = block * thread_id - 1;

                if (downloader.file_size <= end_pos) {
                    end_pos = downloader.file_size - 1;
                }

                //Setting range of data acquisition entity
                Log.i("Thread starting point", Integer.toString(start_pos));
                Log.i("Thread starting point", Integer.toString(end_pos));
                http.setRequestProperty("Range", "bytes=" + start_pos + "-"+ end_pos);
                http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                http.setRequestProperty("Connection", "Keep-Alive");

                InputStream inStream = http.getInputStream();
                byte[] buffer = new byte[1024];
                int offset;
                print("Thread" + this.thread_id + " start download from position "+ start_pos);
                RandomAccessFile threadfile = new RandomAccessFile(this.save_file, "rwd");
                threadfile.seek(start_pos);
                while ((offset = inStream.read(buffer, 0, 1024)) != -1) {
                    if (downloader.should_destroy) {
                        Log.i("You can stop the running thread", "true");
                        this.interrupt();
                        return;
                    }

                    if (downloader.should_pause) {
                        Log.i("You can stop the running thread", "true");
                        this.interrupt();
                        return;
                    }


                    threadfile.write(buffer, 0, offset);
                    downloaded_length += offset;
                    downloader.update(this.thread_id, downloaded_length);
                    downloader.append(offset);


                    Log.i("Thread " + this.thread_id + " offset ", Integer.toString(offset));
                    Log.i("Thread " + this.thread_id + " loop size ", Integer.toString(downloaded_length));
                }
                threadfile.close();
                inStream.close();
                print("Thread " + this.thread_id + " download finish");
                this.finish = true;
            } catch (Exception e) {
                this.downloaded_length = -1;
                print("Threading errors "+ this.thread_id + ":"+ e.getMessage());
            }
        }
    }
    private static void print(String msg){
        Log.i(TAG, msg);
    }



    public boolean is_finish() {
        return finish;
    }


    /**
     * Already downloaded content size
     * @return If the return value is -1, representing the download fails
     */

    public long get_downloaded_length() {
        return downloaded_length;
    }
}
