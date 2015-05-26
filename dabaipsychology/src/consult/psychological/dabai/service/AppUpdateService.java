package consult.psychological.dabai.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import consult.psychological.dabai.Conf;
import consult.psychological.dabai.R;
import consult.psychological.dabai.activity.MainActivity;
import consult.psychological.dabai.commont.APIURL;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;


public class AppUpdateService extends Service {
	// 标题
	private int titleId = 0;

	// 文件存储
	private File updateDir = null;
	private File updateFile = null;
	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	/***
	 * 创建通知栏
	 */
	RemoteViews contentView;
	// 这样的下载代码很多，我就不做过多的说明
	int downloadCount = 0;
	int currentSize = 0;
	long totalSize = 0;
	int updateTotalSize = 0;

	// 在onStartCommand()方法中准备相关的下载工作：
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获取传值
		titleId = intent.getIntExtra("titleId", 0);
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(),
					APIURL.saveFileName);
			updateFile = new File(updateDir.getPath(), getResources()
					.getString(titleId) + ".apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);
		// 设置通知栏显示内容
		updateNotification.icon = R.drawable.ic_home;
		updateNotification.tickerText = "开始下载";
		updateNotification.setLatestEventInfo(this, "指读", "0%",
				updatePendingIntent);
		// 发出通知
		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");

				updatePendingIntent = PendingIntent.getActivity(
						AppUpdateService.this, 0, installIntent, 0);

				updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
				updateNotification.setLatestEventInfo(AppUpdateService.this,
						"指读", "下载完成,点击安装。", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);

				// 停止服务
				stopService(updateIntent);
			case DOWNLOAD_FAIL:
				// 下载失败
				updateNotification.setLatestEventInfo(AppUpdateService.this,
						"指读", "下载完成,点击安装。", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);
			default:
				stopService(updateIntent);
			}
		}
	};

	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;

					updateNotification.setLatestEventInfo(AppUpdateService.this,
							"正在下载", (int) totalSize * 100 / updateTotalSize
									+ "%", updatePendingIntent);

					
					/***
					 * 在这里我们用自定的view来显示Notification
					 */
					updateNotification.contentView = new RemoteViews(
							getPackageName(), R.layout.notification_item);
					updateNotification.contentView.setTextViewText(
							R.id.notificationTitle, "正在下载");
					updateNotification.contentView.setProgressBar(
							R.id.notificationProgress, 100, downloadCount, false);
					
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			
			
			try {
				// 增加权限<USES-PERMISSION
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				// 下载函数，以QQ为例子
				// 增加权限<USES-PERMISSION
				// android:name="android.permission.INTERNET">;
				long downloadSize = downloadUpdateFile(
						Conf.DOWNLOAD_APK,
						
						updateFile);
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				} 
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}
}