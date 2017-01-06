package com.tianyoustudio.update.library;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class CurrentActivityManager {

	private static CurrentActivityManager mManager;
	private WeakReference<Activity> mCurrentActivityWeakRef = null;

	public static CurrentActivityManager getInstance() {
		if (mManager == null) {
			mManager = new CurrentActivityManager();
		}
		return mManager;
	}

	public Activity getCurrentActivity() {

		if (mCurrentActivityWeakRef == null) {
			System.out.println("------------------CurrentActivity not set!");
			// throw new RuntimeException("CurrentActivity not set!");
			return null;
		} else {
			return mCurrentActivityWeakRef.get();
		}
	}

	public void setCurrentActivity(Activity activity) {
		this.mCurrentActivityWeakRef = new WeakReference<Activity>(activity);
	}

	@SuppressWarnings("deprecation")
	public String getRunningAvtivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		hashCode();
		return activityManager.getRunningTasks(1).get(0).topActivity.getClassName() + '@'
				+ Integer.toHexString(hashCode());
	};
}
