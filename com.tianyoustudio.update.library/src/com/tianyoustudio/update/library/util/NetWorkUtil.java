package com.tianyoustudio.update.library.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

	/**
	 * Whether or not to connect to the Internet
	 */
	public static boolean isNetWorkConnected(Context context) {
		NetworkInfo info = getNetworkInfos(context);
		return info != null && info.isConnected();
	}

	/**
	 * Determine whether to use wifi
	 */
	public static boolean isWifiConnected(Context context) {
		NetworkInfo info = getNetworkInfos(context);
		return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
	}

	static NetworkInfo getNetworkInfos(Context context) {
		// Context context = UpdateHelper.getInstance().getContext();
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connManager.getActiveNetworkInfo();
	}
}
