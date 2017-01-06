
package com.tianyoustudio.update.library.server;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.tianyoustudio.update.library.R;
import com.tianyoustudio.update.library.Api.Api;
import com.tianyoustudio.update.library.bean.UpdateApkInfo;
import com.tianyoustudio.update.library.bean.UpdateHelper;
import com.tianyoustudio.update.library.download.DownloadManager;
import com.tianyoustudio.update.library.download.DownloadTask;
import com.tianyoustudio.update.library.download.ParamsManager;
import com.tianyoustudio.update.library.util.InstallUtil;
import com.tianyoustudio.update.library.util.ResourceUtils;
import com.tianyoustudio.update.library.util.UpdateConstants;
import com.tianyoustudio.update.type.UpdateType;

import java.io.File;

/**
 * ========================================
 * <p/>
 * 描 述：原理 纵线 首先是点击更新时，弹出进度对话框（进度，取消和运行在后台）， 如果是在前台完成下载，弹出安装对话框，
 * 如果是在后台完成下载，通知栏提示下载完成， 横线 如果进入后台后，还在继续下载点击时重新回到原界面 如果强制更新无进入后台功能 如果是静默更新，安静的安装
 * <p/>
 * ========================================
 */
public class DownloadingService extends Service {

	private UpdateApkInfo mUpdateApkInfo;
	private NotificationCompat.Builder mBuilder;
	private String url;
	public NotificationManager mNotificationManager;

	private Context mContext;
	private DownloadManager manage;
	long total;
	long percent;
	MyBroadcastReceiver receiver;
	public Handler handler = new Handler() {
		long oldtime = System.currentTimeMillis();

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String strUrl = (String) msg.obj;
			total = DownloadTask.getTotalSize();
			percent = DownloadTask.getDownloadPercent();
			System.out.println("mypercent=" + percent);
			if (url != null && url.equals(strUrl)) {
				switch (msg.what) {
				case ParamsManager.State_NORMAL:
					break;
				case ParamsManager.State_DOWNLOAD:
					if (total <= 0 || percent > total) {
						return;
					}
					notifyNotification(percent);
					long newtime = System.currentTimeMillis();
					System.out.println("time_=" + (newtime - oldtime));
					oldtime = newtime;
					break;
				case ParamsManager.State_FINISH:
					onNotificationfinish();
					String mPath = DownloadManager.getInstance(mContext).getDownPath() + File.separator
							+ url.substring(url.lastIndexOf("/") + 1);
					if (!url.substring(url.length() - 4).endsWith(".apk")) {
						mPath = mPath + ".apk";
					}
					File file = new File(mPath);
					showInstallNotificationUI(file);
					if (UpdateHelper.getInstance().getUpdateType() == UpdateType.wifiautodown
							|| UpdateHelper.getInstance().getUpdateType() == UpdateType.autodown
									&& file.length() == mUpdateApkInfo.getApkSize()) {
						InstallUtil.installApk(mContext, file);
					} else {
						// Intent intent = new Intent(mContext,
						// UpdateDialogActivity.class);
						// intent.putExtra(UpdateConstants.DATA_UPDATE,
						// mUpdateApkInfo);
						// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.putExtra(UpdateConstants.DATA_ACTION,
						// UpdateConstants.UPDATE_INSTALL);
						// intent.putExtra(UpdateConstants.SAVE_PATH,
						// file.getAbsolutePath());
						// startActivity(intent);
						Bundle bundle = new Bundle();

						bundle.putSerializable(UpdateConstants.DATA_UPDATE, mUpdateApkInfo);
						bundle.putInt(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_INSTALL);
						bundle.putString(UpdateConstants.SAVE_PATH, file.getAbsolutePath());
						Intent intent = new Intent(Api.Update_Intent);
						intent.putExtra("bundle", bundle);
						sendBroadcast(intent);
						//
						stopService(new Intent(DownloadingService.this, DownloadingService.class));
					}
					break;
				case ParamsManager.State_PAUSE:
					break;
				case ParamsManager.State_PRE:
					createNotification();
					break;
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		// this.mContext = this;
		this.mContext = getApplicationContext();
		/* 动态方式注册广播接收者 */
		if (receiver == null) {
			receiver = new MyBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Api.Action_Click);
			filter.addAction(Api.Action_Cancel);
			registerReceiver(receiver, filter);
		}
		manage = DownloadManager.getInstance(mContext);
		manage.setHandler(handler);
		percent = DownloadTask.getDownloadPercent();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int action = intent.getIntExtra(UpdateConstants.DATA_ACTION, 0);

			if (action == UpdateConstants.START_DOWN) {
				mUpdateApkInfo = (UpdateApkInfo) intent.getSerializableExtra(UpdateConstants.DATA_UPDATE);
				url = mUpdateApkInfo.getDownload_Url();
				if (mUpdateApkInfo != null && !TextUtils.isEmpty(url)) {
					manage.startDownload(url,mUpdateApkInfo);
				}
			} else if (action == UpdateConstants.PAUSE_DOWN) {
				if (manage.isDownloading(url)) {
					System.out.println("action=暂停");
					manage.pauseDownload(url);
				} else {
					manage.startDownload(url,mUpdateApkInfo);
					System.out.println("action=继续");
				}
			} else if (action == UpdateConstants.CANCEL_DOWN) {
				manage.deleteDownload(url);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void createNotification() {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);

		mBuilder.setContentTitle(getText(ResourceUtils.getResourceIdByName(mContext, "string", "app_name")) + ".apk");
		mBuilder.setProgress(100, (int) percent, false);
		mBuilder.setContentText("已经下载：" + percent + "%");
		Intent intentClick = new Intent(Api.Action_Click);
		intentClick.setAction(Api.Action_Click);
		intentClick.putExtra(MyBroadcastReceiver.TYPE, UpdateConstants.NOTIFICATION_ID);
		PendingIntent pendingIntent_Click = PendingIntent.getBroadcast(DownloadingService.this, 0, intentClick,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Intent intentCancel = new Intent(Api.Action_Cancel);
		intentCancel.setAction(Api.Action_Cancel);
		intentCancel.putExtra(MyBroadcastReceiver.TYPE, UpdateConstants.NOTIFICATION_ID);
		PendingIntent pendingIntent_Cancel = PendingIntent.getBroadcast(this, 0, intentCancel,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.addAction(android.R.drawable.ic_media_pause, "暂停", pendingIntent_Click);
		mBuilder.addAction(R.drawable.ic_cancel, "取消", pendingIntent_Cancel);
		mNotificationManager.notify(UpdateConstants.NOTIFICATION_ID, mBuilder.build());

	}

	private void onNotificationfinish() {
		mBuilder.setContentText("已经下载：100%");
		mBuilder.setProgress(100, 100, false);
		mNotificationManager.notify(UpdateConstants.NOTIFICATION_ID, mBuilder.build());
	}

	/**
	 * 刷新下载进度
	 */
	private void notifyNotification(long percent) {
		mBuilder.setContentText("已经下载：" + percent + "%");
		mBuilder.setProgress(100, (int) percent, false);
		mNotificationManager.notify(UpdateConstants.NOTIFICATION_ID, mBuilder.build());
	}

	/**
	 * 显示安装
	 */
	private void showInstallNotificationUI(File file) {
		int counts = mBuilder.mActions.size();
		for (int i = 0; i < counts; i++) {
			mBuilder.mActions.remove(0);
		}
		mBuilder.setProgress(0, 0, false);
		if (mBuilder == null) {
			mBuilder = new NotificationCompat.Builder(this);
		}
		mBuilder.setSmallIcon(getApplicationInfo().icon)
				.setContentTitle(getText(ResourceUtils.getResourceIdByName(mContext, "string", "app_name")) + ".apk")
				.setContentText("下载完成，点击安装");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(UpdateConstants.NOTIFICATION_ID, mBuilder.build());
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public static final String TYPE = "type";

		@Override
		public void onReceive(Context context, Intent intent) {

			int id = intent.getIntExtra(TYPE, -1);
			String action = intent.getAction();

			/** 暂停和开始 */
			if (action.equals(Api.Action_Click)) {
				Intent downIntent = new Intent(context, DownloadingService.class);
				downIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				downIntent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.PAUSE_DOWN);
				downIntent.putExtra("mUpdateApkInfo", mUpdateApkInfo);
				startService(downIntent);

				String title = (String) mBuilder.mActions.get(0).title;
				if (title.equals("继续") ) {
					Toast.makeText(context, "pause", Toast.LENGTH_SHORT).show();
					mBuilder.mActions.get(0).icon = android.R.drawable.ic_media_pause;
					mBuilder.mActions.get(0).title = "暂停";
					mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
				} else {
					Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();
					mBuilder.mActions.get(0).icon = android.R.drawable.ic_media_play;
					mBuilder.mActions.get(0).title = "继续";
					mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
				}
				mNotificationManager.notify(id, mBuilder.build());
			}
			if (action.equals(Api.Action_Cancel)) {
				Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
				mNotificationManager.cancel(id);
				/** 取消 */
				Intent cancelIntent = new Intent(context, DownloadingService.class);
				cancelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				cancelIntent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.CANCEL_DOWN);
				cancelIntent.putExtra("mUpdateApkInfo", mUpdateApkInfo);
				startService(cancelIntent);
			}

		}

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
}