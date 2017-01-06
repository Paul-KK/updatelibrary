package com.tianyoustudio.update.library.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

public class InstallUtil {

	/**
	 * install apk
	 * 
	 * @param context
	 *            the context is used to send install apk broadcast;
	 * @param filename
	 *            the file name to be installed;
	 */
	public static void installApk(Context context, String filename) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		File pluginfile = new File(filename);
		intent.setDataAndType(Uri.fromFile(pluginfile), type);
		context.startActivity(intent);
	}

	/**
	 * 安装apk
	 *
	 * @param context
	 *            上下文
	 * @param file
	 *            APK文件
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void installApkFromActivity(final Context context, File file) {
		if (file == null || file != null && file.length() == 0) {
			HandlerUtil.getMainHandler().post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, "安装文件有问题,文件不完整或者代码中路径不对，请检测SD卡中文件完整性", Toast.LENGTH_SHORT).show();
				}
			});
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

}
