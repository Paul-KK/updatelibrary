package com.tianyoustudio.update.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class CurrentApkInfo {

	public static int getApkVersionCode(Context context) throws PackageManager.NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
		return packageInfo.versionCode;
	}

	public static String getApkVersionName(Context context) throws PackageManager.NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
		return packageInfo.versionName;
	}

	public static PackageInfo getApkVersionInfo(Context context) throws PackageManager.NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
		return packageInfo;
	}

	public static String getPackageName(Context context) {

		return context.getPackageName();
	}
}
