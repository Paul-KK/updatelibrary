package com.tianyoustudio.update.demo;

import com.tianyoustudio.update.library.bean.UpdateHelper;
import com.tianyoustudio.update.library.listener.UpdateListener;
import com.tianyoustudio.update.library.util.CurrentApkInfo;
import com.tianyoustudio.update.library.util.NetWorkUtil;
import com.tianyoustudio.update.type.UpdateType;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private Button update, goto2;
	private Context mContext;
	RadioGroup mRadioGroup;
	RadioButton mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4;
	SharedPreferences sp;
	UpdateType type = null;
	TextView currentVersionName, currentVersionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		currentVersionName = (TextView) findViewById(R.id.currentVersionName);
		currentVersionCode = (TextView) findViewById(R.id.currentVersionCode);

		try {
			currentVersionName.setText("当前版本名称： " + CurrentApkInfo.getApkVersionName(mContext));
			currentVersionCode.setText("当前版本号：" + CurrentApkInfo.getApkVersionCode(mContext));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		sp = getSharedPreferences("RadioGroup", Context.MODE_PRIVATE);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mRadioButton1 = (RadioButton) findViewById(R.id.radio_autocheck);
		mRadioButton2 = (RadioButton) findViewById(R.id.radio_wifiautocheck);
		mRadioButton3 = (RadioButton) findViewById(R.id.radio_autodown);
		mRadioButton4 = (RadioButton) findViewById(R.id.radio_wifiautodown);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				sp.edit().putInt("id", checkedId).commit();
				switch (checkedId) {
				case R.id.radio_autocheck:
					Toast.makeText(mContext, "autocheck", Toast.LENGTH_SHORT).show();
					UpdateHelper.getInstance().setUpdateType(UpdateType.autocheck);
					break;
				case R.id.radio_wifiautocheck:
					Toast.makeText(mContext, "wifiautocheck", Toast.LENGTH_SHORT).show();
					UpdateHelper.getInstance().setUpdateType(UpdateType.wifiautocheck);
					break;
				case R.id.radio_autodown:
					Toast.makeText(mContext, "autodown", Toast.LENGTH_SHORT).show();
					UpdateHelper.getInstance().setUpdateType(UpdateType.autodown);
					break;
				case R.id.radio_wifiautodown:
					Toast.makeText(mContext, "wifiautodown", Toast.LENGTH_SHORT).show();
					UpdateHelper.getInstance().setUpdateType(UpdateType.wifiautodown);
					break;

				}
			}
		});

		switch (sp.getInt("id", R.id.radio_autocheck)) {
		case R.id.radio_autocheck:
			mRadioButton1.setChecked(true);
			type = UpdateType.autocheck;
			break;
		case R.id.radio_wifiautocheck:
			mRadioButton2.setChecked(true);
			type = UpdateType.wifiautocheck;
			break;
		case R.id.radio_autodown:
			mRadioButton3.setChecked(true);
			type = UpdateType.autodown;
			break;
		case R.id.radio_wifiautodown:
			mRadioButton4.setChecked(true);
			type = UpdateType.wifiautodown;
			break;
		}

		update = (Button) findViewById(R.id.update);
		update.setOnClickListener(this);
		goto2 = (Button) findViewById(R.id.goto2);
		goto2.setOnClickListener(this);
		switch (type) {
		case autocheck:
			if (NetWorkUtil.isNetWorkConnected(mContext)) {
				Toast.makeText(mContext, "3g/4g网络自动检测更新", Toast.LENGTH_SHORT).show();
				UpdateHelper.getInstance().setUpdateType(UpdateType.autocheck).setUpdateListener(updateListener)
						.check(MainActivity.this);
			} else {
				Toast.makeText(mContext, "3g/4g网络自动检测更新，却没有联网", Toast.LENGTH_SHORT).show();
			}
			break;
		case wifiautocheck:
			if (NetWorkUtil.isWifiConnected(mContext)) {
				Toast.makeText(mContext, "Wifi网络自动检测更新", Toast.LENGTH_SHORT).show();
				UpdateHelper.getInstance().setUpdateType(UpdateType.wifiautocheck).setUpdateListener(updateListener)
						.check(MainActivity.this);
			} else {
				Toast.makeText(mContext, "Wifi网络自动检测更新，却没有联Wifi", Toast.LENGTH_SHORT).show();
			}

			break;
		case autodown:

			if (!NetWorkUtil.isNetWorkConnected(mContext)) {
				Toast.makeText(mContext, "3g/4g网络自动下载，却没有联网", Toast.LENGTH_SHORT).show();
			} else {
				UpdateHelper.getInstance().setUpdateType(UpdateType.autodown).setUpdateListener(updateListener)
						.check(MainActivity.this);
			}

			break;
		case wifiautodown:

			if (!NetWorkUtil.isNetWorkConnected(mContext)) {
				Toast.makeText(mContext, "Wifi网络自动下载，却没有联Wifi", Toast.LENGTH_SHORT).show();
			} else {
				UpdateHelper.getInstance().setUpdateType(UpdateType.wifiautodown).setUpdateListener(updateListener)
						.check(MainActivity.this);
			}

			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.update:
			UpdateHelper.getInstance().setUpdateType(UpdateType.forcecheck)
					.setUpdateListener(updateListener).check(MainActivity.this);
			break;

		case R.id.goto2:
			startActivity(new Intent(this, MainActivity2.class));
			break;

		}

	}

	UpdateListener updateListener = new UpdateListener() {

		@Override
		public void onCheckError(int code, String errorMsg) {
			Toast.makeText(mContext, "检测更新失败：" + errorMsg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void noUpdate(String msg) {

			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
	};

}
