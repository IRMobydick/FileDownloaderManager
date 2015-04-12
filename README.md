FileDownloaderManager
=====================

FileDownloader library for android



### filedownload-samples: 

1 Network addresses need to download the file<br>

2 After downloading the file save path<br>

3 Download a few threads<br>



### filedownload:

Download the file to run in the service, and encapsulated in the internal components

1 Download progress can be displayed in the notification bar<br>

2 When the program is minimized to download files, download the need continues<br>

3 Download it for a while, on the notification bar shows the progress of the download information




==================

### Invocation API component

```java
public class FileDownloader{
  // Constructor
  // context Context        * Instance, usually an activity instance
  // download_url           * URL to download
  // file_save_dir          * After downloading the file save location
  // thread_num             * Start several threads to download
  public FileDownloader(Context context, String download_url, File file_save_dir, int thread_num);
}
```

以上几个参数设置后，就可以给组件设置下载的监视钩子，并开始下载
```java
public class FileDownloader{
  // Start Download
  // ProgressUpdateListener   * Monitor change in the number of downloads，listener   * You need to run in the UI thread
  // If you do not monitor can be set to null
  public void download(ProgressUpdateListener listener);

  interface ProgressUpdateListener {
    public void on_update(int downloaded_size);
  }
}
```

Name and size after the download begins, you need to be able to get to the downloaded file
```java
public class FileDownloader{
  // Get To download the file size (in bytes)
  public int get_file_size();

  // Get the name of the file to be downloaded
  public String get_file_name();

}
```

Download it for a while, on the notification bar shows the progress of the download information, the information needed to register a notification bar click event, following the component interface allows users to set Click to open activity, bring intent_extras parameters when opening activity
```java
public void set_notification(Class activity_class, Bundle intent_extras);
```

Then download tasks can be suspended
```java
public void pause_download();
```

Tasks can be deleted
```java
public void stop_download();
```


Activity onPause cancel broadcast
```java
public void unregister_download_receiver();
```


Activity onResume activate broadcasting
```java
public void register_download_receiver(ProgressUpdateListener listener);
```

```java
public class FileDownloader{
  // Constructor
  // context Context Instance, usually an activity instance
  // download_url     * URL to download
  // file_save_dir    * After downloading the file save location
  // thread_num       * Start several threads to download
  public FileDownloader(Context context, String download_url, File file_save_dir, int thread_num);

  // Get To download the file size (in bytes)
  public int get_file_size();

  // Get the name of the file to be downloaded
  public String get_file_name();

  // Start Download
  // ProgressUpdateListener   * Monitor change in the number of downloads，listener   * You need to run in the UI thread
  // If you do not monitor can be set to null
  public void download(ProgressUpdateListener listener);

  interface ProgressUpdateListener {
    public void on_update(int downloaded_size);
  }

  // Setting the notification bar information click event behavior
  public void set_notification(Class activity_class, Bundle intent_extras);

  public void pause_download();
  public void stop_download();
}
```

### Activity in the overall use of the example in the following

```java
public class MainActivity extends Activity{
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    run_download_1();
    run_download_2();
  }

  public void run_download_1(){
    Context context = this;
    String url = "http://www.baidu.com/img/bdlogo.gif";
    File save_dir  = new File("/sd/files");
    FileDownloader fd = new FileDownloader(context, url, save_dir, 2);

    Bundle b = new Bundle();
    b.putString("param_name1", "param_value1");
    fd.set_notification(MainActivity.class, b);

    fd.download(new ProgressUpdateListener(){
      public void on_update(int downloaded_size){
        // This approach needs to be run in the UI thread
        // For example, increasing the logic here: download progress bar is displayed in the main interface
        // downloaded_size Bytes, indicating the number of bytes has been downloaded

        // Get To download the file size (in bytes)
        fd.get_file_size();

        // Get the name of the file to be downloaded
        fd.get_file_name();
      }
    });
  }


  public void run_download_2(){
    Context context = this;
    String url = "http://www.google.com/img/logo.gif";
    File save_dir  = new File("/sd/files");
    FileDownloader fd1 = new FileDownloader(context, url, save_dir, 2);

    Bundle b = new Bundle();
    b.putString("param_name1", "param_value1");
    fd1.set_notification(MainActivity.class, b);

    fd1.download(new ProgressUpdateListener(){
      public void on_update(int downloaded_size){
        // This approach needs to be run in the UI thread
        // For example, increasing the logic here: download progress bar is displayed in the main interface
        // downloaded_size Bytes, indicating the number of bytes has been downloaded

        // Get To download the file size (in bytes)
        fd1.get_file_size();

        // Get the name of the file to be downloaded
        fd1.get_file_name();
      }
    });
  }
}
```




AndroidManiFest Setup
```java
<service android:name="com.mindpin.android.filedownloader.DownloadService" />

<receiver
    android:name=".DownloadProgressNotificationWidget"
    android:label="DownloadProgressNotificationWidget" >
    <intent-filter>
        <action android:name="app.action.download_progress_notification_widget" />
    </intent-filter>

</receiver>


<receiver
    android:name=".DownloadDoneNotification"
    android:label="DownloadDoneNotification" >
    <intent-filter>
        <action android:name="app.action.download_done_notification" />
    </intent-filter>

</receiver>

<receiver
    android:name=".DownloadPauseReceiver"
    android:label="DownloadPauseReceiver" >
    <intent-filter>
        <action android:name="app.action.download_pause_receiver" />
    </intent-filter>

</receiver>

<receiver
    android:name=".DownloadStopReceiver"
    android:label="DownloadStopReceiver" >
    <intent-filter>
        <action android:name="app.action.download_stop_receiver" />
    </intent-filter>

</receiver>

<receiver
    android:name=".DownloadListenerReceiver"
    android:label="DownloadListenerReceiver" >
    <intent-filter>
        <action android:name="app.action.download_listener_receiver" />
    </intent-filter>

</receiver>
```

Examples of Use

```java
Context context = // Examples of an activity
String url = "http://www.baidu.com/img/bdlogo.gif";
File save_dir  = new File("/sd/files");
FileDownloader fd = new FileDownloader(context, url, save_dir, 2);

fd.download(new ProgressUpdateListener(){
  public void on_update(int downloaded_size){
    // This approach needs to be run in the UI thread
    // For example, increasing the logic here: download progress bar is displayed in the main interface
    // downloaded_size Bytes, indicating the number of bytes has been downloaded

  }
});

```

==========




### DownloadManager API instructions

1, Start the download and display the notification bar progress bar information, multiple tasks simultaneously displayed on the notification bar

```java
// Download URL path initialization
Uri uri = Uri.parse("http://esharedev.oss-cn-hangzhou.aliyuncs.com/file/%E5%9B%BE%E7%89%87%E6%94%BE%E5%A4%A7%E7%BC%A9%E5%B0%8F%E6%97%8B%E8%BD%AC.mp4");

DownloadManager.Request request = new Request(uri);

// Set the download directory, file name
request.setDestinationInExternalPublicDir("mindpin", "less_5mb.mp4");

// Set only allowed under the WIFI network to download
request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

// Join the download queue to begin downloading
int download_id = downloadmanager.enqueue(request);
```

2, Custom notification bar progress bar information click event

```java
// Activity onCreate method to start broadcasting in the activation notice
registerReceiver(on_notification_click,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


// Custom Click notification bar to do logic
BroadcastReceiver on_notification_click = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Click prompt notification bar", Toast.LENGTH_LONG).show();
        }
    };
```



3, Custom download is complete information about the event notification bar

```java
// Activity onCreate method to start broadcasting in the activation notice
registerReceiver(on_complete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


// Custom logic after the download is complete
BroadcastReceiver on_complete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "已经下载完成提示", Toast.LENGTH_LONG).show();
        }
    };
```


4, Delete download task

```java
// Initialization downloadmanager
DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

// download_id above to start the download method for downloadmanager.enqueue (request); return value here
downloadmanager.remove(download_id);
```


5, Custom progress bar interface provides hooks to monitor progress of the download method changes, customize the interface progress bar in the hook method
```java
// 需要使用到系统 ContentObserver
class DownloadChangeObserver extends ContentObserver {

    public DownloadChangeObserver() {
        // Here's handler for a Handler class instance define yourself, to handle the relevant data to update the progress bar operations
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        update_progress();
    }

}

// button onClick Events in activation
getContentResolver().registerContentObserver(CONTENT_URI, true, download_observer);

// Data needed to update the progress bar
public void update_progress() {
    int[] bytes_and_status = new int[] {-1, -1, 0};
    DownloadManager.Query query = new DownloadManager.Query().setFilterById(download_id);
    Cursor c = null;
    try {
        c = downloadmanager.query(query);
        if (c != null && c.moveToFirst()) {
            bytes_and_status[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            Log.i("Now download size", Integer.toString(bytes_and_status[0]));
            bytes_and_status[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            Log.i("The total size", Integer.toString(bytes_and_status[0]));
            bytes_and_status[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            Log.i("Download Status", Integer.toString(bytes_and_status[0]));
        }
    } finally {
        if (c != null) {
            c.close();
        }
    }

    // Need to use Hanlder class to handle the data information is displayed on the progress bar
    handler.sendMessage(handler.obtainMessage(0, bytes_and_status[0], bytes_and_status[1], bytes_and_status[2]));
}
```







