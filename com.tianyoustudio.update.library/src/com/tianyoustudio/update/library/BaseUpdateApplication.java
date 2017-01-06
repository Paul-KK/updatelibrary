package com.tianyoustudio.update.library;

import com.tianyoustudio.update.library.Api.Api;
import com.tianyoustudio.update.library.util.ProcessUtil;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.os.Bundle;

public abstract class BaseUpdateApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		RegisterReceiver();

		this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

			@Override
			public void onActivityStopped(Activity activity) {
			}

			@Override
			public void onActivityStarted(Activity activity) {
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}

			@Override
			public void onActivityResumed(Activity activity) {
				CurrentActivityManager.getInstance().setCurrentActivity(activity);
			}

			@Override
			public void onActivityPaused(Activity activity) {
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
			}

			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			}
		});
	}

	/**
	 * 如果存在多个进程（process），就会多次调用此Application的onCreate方法，就会注册多个相同Action的广播接收者，所以需要通过进程名字来作判断，保证广播发送者和接收者处在相同进程，否则可能因为不同进程的原因会出现各种错误，比如安卓4.2.1就会报错
	 */
	private void RegisterReceiver() {

		if (ProcessUtil.getCurrentProcessName(getApplicationContext()).equals(UpdateProcessName())) {
			UpdateDialogBroadcastReceiver receiver = new UpdateDialogBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Api.Update_Intent);
			registerReceiver(receiver, filter);
		}

	}

	/**
	 * 返回弹出（更新对话框）所处的进程的名字，一定要手动写，不要使用Context获取，否则失效
	 */
	public abstract String UpdateProcessName();

}
