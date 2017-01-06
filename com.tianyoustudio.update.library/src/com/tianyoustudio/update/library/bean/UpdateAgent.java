package com.tianyoustudio.update.library.bean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

import com.tianyoustudio.update.library.bean.UpdateApkInfo;
import com.tianyoustudio.update.library.download.DownloadManager;
import com.tianyoustudio.update.library.download.SqliteManager;
import com.tianyoustudio.update.library.listener.UpdateListener;
import com.tianyoustudio.update.library.network.IUpdateExecutor;
import com.tianyoustudio.update.library.network.UpdateExecutor;
import com.tianyoustudio.update.library.network.UpdateWorker;
import com.tianyoustudio.update.library.server.DownloadingService;
import com.tianyoustudio.update.library.util.InstallUtil;
import com.tianyoustudio.update.library.util.NetWorkUtil;
import com.tianyoustudio.update.library.util.UpdateConstants;
import com.tianyoustudio.update.library.view.UpdateDialog;
import com.tianyoustudio.update.type.UpdateType;

public class UpdateAgent {
	private static UpdateAgent updater;
	private IUpdateExecutor executor;

	private UpdateAgent() {
		executor = UpdateExecutor.getInstance();
	}

	public static UpdateAgent getInstance() {
		if (updater == null) {
			updater = new UpdateAgent();
		}
		return updater;
	}

	/**
	 * check out whether or not there is a new version on internet
	 *
	 * @param activity
	 *            The activity who need to show update dialog
	 */
	public void onlineCheck(Activity activity) {

	}

	/**
	 * check out whether or not there is a new version on internet
	 *
	 * @param activity
	 *            The activity who need to show update dialog
	 */
	public void checkUpdate(final Activity activity) {
		UpdateWorker checkWorker = new UpdateWorker(activity);
		checkWorker.setRequestMethod(UpdateHelper.getInstance().getRequestMethod());
		checkWorker.setUrl(UpdateHelper.getInstance().getCheckUrl());
		if (UpdateHelper.getInstance().getRequestMethod() == UpdateHelper.RequestType.post) {
			checkWorker.setParams(UpdateHelper.getInstance().getCheckParams());
		}

		checkWorker.setParser(UpdateHelper.getInstance().getCheckJsonParser());

		final UpdateListener mUpdate = UpdateHelper.getInstance().getUpdateListener();
		checkWorker.setUpdateListener(new UpdateListener() {
			@Override
			public void hasUpdate(UpdateApkInfo update) {
				if (mUpdate != null) {
					mUpdate.hasUpdate(update);
				}
				UpdateType type = UpdateHelper.getInstance().getUpdateType();
				boolean finshDown = SqliteManager.getInstance(activity).isContains(update.getDownload_Url());
				String url = update.getDownload_Url();
				String mPath = DownloadManager.getInstance(activity).getDownPath() + File.separator
						+ url.substring(url.lastIndexOf("/") + 1);
				if (!mPath.substring(mPath.length() - 4).endsWith(".apk")) {
					mPath = mPath + ".apk";
				}
				switch (type) {
				case autodown:
					if (finshDown && new File(mPath).length() == update.getApkSize()) {
						File file = new File(mPath);
						InstallUtil.installApkFromActivity(activity, file);
					} else {
						if (NetWorkUtil.isNetWorkConnected(activity)) {
							Toast.makeText(activity, "3g/4g网络下自动下载", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(activity, DownloadingService.class);
							intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
							intent.putExtra(UpdateConstants.DATA_UPDATE, update);
							activity.startService(intent);
						} else {
							Toast.makeText(activity, "3g/4g网络下自动下载，但是当前没有联网", Toast.LENGTH_SHORT).show();

						}
					}

					break;
				case wifiautodown:
					if (finshDown && new File(mPath).length() == update.getApkSize()) {
						File file = new File(mPath);
						InstallUtil.installApkFromActivity(activity, file);
					} else {
						if (NetWorkUtil.isWifiConnected(activity)) {
							Toast.makeText(activity, "Wifi情况下自动下载", Toast.LENGTH_SHORT).show();

							Intent intent = new Intent(activity, DownloadingService.class);
							intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
							intent.putExtra(UpdateConstants.DATA_UPDATE, update);
							activity.startService(intent);
						} else {
							Toast.makeText(activity, "Wifi情况下自动下载，但是当前没有连接Wifi", Toast.LENGTH_SHORT).show();
						}
					}

					break;
				default:
					Bundle bundle = new Bundle();
					bundle.putSerializable(UpdateConstants.DATA_UPDATE, update);
					bundle.putInt(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_TIE);
					final UpdateDialog dialog = new UpdateDialog(activity, bundle);
					dialog.setTitle("发现新版本");
					dialog.setCancelable(false);
					dialog.show();
					break;
				}

				// Intent intent = new Intent(activity,
				// UpdateDialogActivity.class);
				// intent.putExtra(UpdateConstants.DATA_UPDATE, update);
				// intent.putExtra(UpdateConstants.DATA_ACTION,
				// UpdateConstants.UPDATE_TIE);
				// activity.startActivity(intent);

			}

			@Override
			public void noUpdate(String msg) {
				if (mUpdate != null) {
					mUpdate.noUpdate(msg);
				}
			}

			@Override
			public void onCheckError(int code, String errorMsg) {
				if (mUpdate != null) {
					mUpdate.onCheckError(code, errorMsg);
				}
			}

			@Override
			public void onUserCancel() {
				if (mUpdate != null) {
					mUpdate.onUserCancel();
				}
			}
		});
		executor.check(checkWorker);
	}

}
