package com.tianyoustudio.update.library;

import com.tianyoustudio.update.library.Api.Api;
import com.tianyoustudio.update.library.view.UpdateDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UpdateDialogBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (action.equals(Api.Update_Intent)) {
			Activity mCurrentActivity = CurrentActivityManager.getInstance().getCurrentActivity();
			Toast.makeText(context, "Application收到了广播，并告知" + mCurrentActivity.toString() + "弹出对话框!", Toast.LENGTH_LONG)
					.show();
			final UpdateDialog dialog = new UpdateDialog(mCurrentActivity, intent.getBundleExtra("bundle"));
			dialog.setTitle("发现新版本");
			dialog.setCancelable(false);
			dialog.show();
		}
	}

}