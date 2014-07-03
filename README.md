FileDownloaderManager
=====================

FileDownloader library for android



### filedownload-samples: 

1 需要下载的文件的网络地址<br>

2 下载后文件的保存路径<br>

3 用几个线程进行下载<br>



### filedownload:

下载文件在 service 中运行，并且封装在组件内部

1 下载进度可以显示在通知栏 <br>

2 下载文件的程序最小化时，下载需要仍然继续 <br>

3 下载进行时，通知栏上显示了下载进度等信息




==================

### 组件的调用API

```java
public class FileDownloader{
  // 构造函数
  // context Context 实例，一般为一个 activity 实例
  // download_url 要下载的 URL
  // file_save_dir 下载后的文件保存的位置
  // thread_num  启动几个线程进行下载
  public FileDownloader(Context context, String download_url, File file_save_dir, int thread_num);

  // 获取要下载的文件的大小(单位为字节)
  public int get_file_size();

  // 获取要下载的文件的名字
  public String get_file_name();

  // 开始下载
  // ProgressUpdateListener 监听下载数量的变化，listener 需要运行在UI线程
  // 如果不需要监听可以设置为 null
  public void download(ProgressUpdateListener listener);

  interface ProgressUpdateListener {
    public void on_update(int downloaded_size);
  }

  // 设置通知栏信息的点击事件行为
  public void set_notification(Class activity_class, Bundle intent_extras);
}
```



AndroidManiFest 设置
```java
<service android:name="com.mindpin.android.filedownloader.DownloadService" />

<receiver
    android:name="com.mindpin.android.filedownloader.DownloadProgressNotificationWidget"
    android:label="TargetWidget" >
    <intent-filter>
        <action android:name="app.action.download_progress_notification_widget" />
    </intent-filter>

</receiver>
```

使用示例

```java
Context context = // 某个 activity 实例
String url = "http://www.baidu.com/img/bdlogo.gif";
File save_dir  = new File("/sd/files");
FileDownloader fd = new FileDownloader(context, url, save_dir, 2);

fd.download(new ProgressUpdateListener(){
  public void on_update(int downloaded_size){
    // 这个方法需要运行在UI线程
    // 比如这里增加逻辑:在主界面显示下载进度条
    // downloaded_size 单位是字节，表示已经下载了的字节数

  }
});

```

==========




### DownloadManager API 相关说明

1, 启动下载，并显示通知栏进度条信息, 多个任务也同时显示在通知栏上

```java
// 初始化下载 URL 路径
Uri uri = Uri.parse("http://esharedev.oss-cn-hangzhou.aliyuncs.com/file/%E5%9B%BE%E7%89%87%E6%94%BE%E5%A4%A7%E7%BC%A9%E5%B0%8F%E6%97%8B%E8%BD%AC.mp4");

DownloadManager.Request request = new Request(uri);

// 设置下载目录，文件名
request.setDestinationInExternalPublicDir("mindpin", "less_5mb.mp4");

// 设置只允许在WIFI的网络下下载
request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

// 加入下载队列, 开始下载
int download_id = downloadmanager.enqueue(request);
```

1, 自定义界面进度条（提供监视下载进度变化的钩子方法，在钩子方法中自定义界面进度条）


