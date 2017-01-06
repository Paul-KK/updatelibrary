/**
 * 
 *CopyRight © 2015-2017 TianYouStudio.All Rights Reserved.
 *
 */
package com.tianyoustudio.update.library.view;

import java.io.File;
import java.text.DecimalFormat;

import com.tianyoustudio.update.library.R;
import com.tianyoustudio.update.library.bean.UpdateApkInfo;
import com.tianyoustudio.update.library.bean.UpdateHelper;
import com.tianyoustudio.update.library.download.DownloadManager;
import com.tianyoustudio.update.library.server.DownloadingService;
import com.tianyoustudio.update.library.util.InstallUtil;
import com.tianyoustudio.update.library.util.NetWorkUtil;
import com.tianyoustudio.update.library.util.UpdateConstants;
import com.tianyoustudio.update.library.util.UpdateSP;
import com.tianyoustudio.update.type.UpdateType;
import com.zhy.changeskin.SkinManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者:Fsk E-mail:1473985844@qq.com
 * @createdtime 创建时间：2017年1月1日下午6:57:25
 * 
 *              该类的说明 :
 *
 */
public class UpdateDialog extends Dialog {
	View mContentView;
	TextView title_tv;
	private String text;
	private CheckBox update_id_check;
	String updateContent = null;
	TextView message_tv;
	private Button update_id_ok;
	ImageView update_wifi_indicator;
	public final static String YES = "yes";
	public final static String CANCEL = "cancel";
	public final static String IGNORE = "ignore";
	Bundle mBundle;
	Context mContext;
	String mPath;
	UpdateApkInfo mUpdate;
	// 是否已经下载完成
	private boolean finshDown;

	public UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.mContext = context;
		mContentView = LayoutInflater.from(getContext()).inflate(R.layout.update_dialog, null);
	}

	public UpdateDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.mContext = context;
		mContentView = LayoutInflater.from(getContext()).inflate(R.layout.update_dialog, null);
	}

	public UpdateDialog(Context context) {
		this(context, getdailogTheme(context));
		this.mContext = context;
		mContentView = LayoutInflater.from(getContext()).inflate(R.layout.update_dialog, null);
	}

	public UpdateDialog(Context context, Bundle bundle) {
		this(context, getdailogTheme(context));
		this.mContext = context;
		this.mBundle = bundle;
		mContentView = LayoutInflater.from(getContext()).inflate(R.layout.update_dialog, null);
	}

	@Override
	public void setTitle(CharSequence title) {
		title_tv = (TextView) mContentView.findViewById(R.id.title);
		title_tv.setText(title);
	}

	public void setMessage(CharSequence message) {
		message_tv = (TextView) mContentView.findViewById(R.id.message);
		message_tv.setText(message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
		setContentView(mContentView);
	}

	public View getmContentView() {
		return mContentView;
	}

	@Override
	public void show() {
		init();
		this.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		super.show();
	}

	private void init() {

		mUpdate = (UpdateApkInfo) mBundle.getSerializable(UpdateConstants.DATA_UPDATE);
		// mPath = mBundle.getString(UpdateConstants.SAVE_PATH);
		// if (TextUtils.isEmpty(mPath)) {
		// String url = mUpdate.getDownload_Url();
		// mPath = DownloadManager.getInstance(mContext).getDownPath() +
		// File.separator
		// + url.substring(url.lastIndexOf("/") + 1);

		mPath = DownloadManager.getInstance(mContext).getDownPath() + File.separator + mContext.getPackageName() + "_"
				+ mUpdate.getMd5() + "_V" + mUpdate.getVersionName() + ".apk";
		// }
		// if (!mPath.substring(mPath.length() - 4).endsWith(".apk")) {
		// mPath = mPath + ".apk";
		// }
		// finshDown =
		// SqliteManager.getInstance(mContext).isContains(mUpdate.getDownload_Url());

		// Toast.makeText(mContext, finshDown + "", Toast.LENGTH_SHORT).show();

		AddButton(YES, "立即更新", new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (new File(mPath).length() == mUpdate.getApkSize()) {
					InstallUtil.installApk(mContext, mPath);
				} else {
					Toast.makeText(mContext, "正在下载", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(mContext, DownloadingService.class);
					intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
					intent.putExtra(UpdateConstants.DATA_UPDATE, mUpdate);
					mContext.startService(intent);
				}
				dismiss();
			}

		});
		AddButton(CANCEL, "以后再说", new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UpdateSP.isForced()) {
					dismiss();
				} else {
					dismiss();
				}
			}
		});

		update_id_ok = getButton(YES);
		update_wifi_indicator = (ImageView) mContentView.findViewById(R.id.WIFI_tips);
		update_id_check = (CheckBox) mContentView.findViewById(R.id.update_id_check);
		update_id_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				UpdateSP.setIgnore(isChecked ? mUpdate.getVersionCode() + "" : "");
			}
		});

		if (NetWorkUtil.isWifiConnected(mContext)) {
			// WiFi环境
			update_wifi_indicator.setVisibility(View.INVISIBLE);
		} else {
			update_wifi_indicator.setVisibility(View.VISIBLE);
		}
		if (UpdateHelper.getInstance().getUpdateType() == UpdateType.forcecheck) {
			// 手动更新
			update_id_check.setVisibility(View.GONE);
		} else {
			update_id_check.setVisibility(View.VISIBLE);
		}
		if (new File(mPath).length() == mUpdate.getApkSize()) {
			// 已经下载
			text = (String) mContext.getText(R.string.update_dialog_installapk);
			//
			finshDown = true;
			updateContent = mContext.getText(R.string.update_newversion) + mUpdate.getVersionName() + "\n" + text
					+ "\n\n" + mContext.getText(R.string.update_updatecontent) + "\n" + mUpdate.getUpdateContent()
					+ "\n";
			update_id_ok.setText(R.string.update_installnow);
			setMessage(updateContent);
			// update_content.setText(updateContent);

		} else {
			text = mContext.getText(R.string.update_targetsize) + getFormatSize(mUpdate.getApkSize());
			//
			updateContent = mContext.getText(R.string.update_newversion) + mUpdate.getVersionName() + "\n" + text
					+ "\n\n" + mContext.getText(R.string.update_updatecontent) + "\n" + mUpdate.getUpdateContent()
					+ "\n";
			update_id_ok.setText(R.string.update_updatenow);
			// update_content.setText(updateContent);
			setMessage(updateContent);

		}

	}

	/**
	 * @param Button_Type
	 * @param text
	 * 
	 * @param onClickListener
	 *            是android.view.View.OnClickListener
	 */
	public void AddButton(String Button_Type, CharSequence text,
			final android.view.View.OnClickListener onClickListener) {
		Button button = null;
		switch (Button_Type) {
		case YES:
			button = (Button) mContentView.findViewById(R.id.btn_yes);
			break;
		case CANCEL:
			button = (Button) mContentView.findViewById(R.id.btn_cancel);
			break;
		case IGNORE:
			button = (Button) mContentView.findViewById(R.id.btn_ignore);
			break;
		}
		button.setVisibility(View.VISIBLE);
		button.setText(text);

		button.setOnClickListener(onClickListener);
	}

	public static String getFormatSize(double size) {
		double fileSize = size;
		DecimalFormat df = new DecimalFormat("0.00");
		String showSize = "";
		if (fileSize >= 0f && fileSize < 1024f) {
			showSize = fileSize + "B";
		} else if (fileSize >= 1024f && fileSize < (1024f * 1024f)) {
			showSize = df.format(fileSize / 1024f) + "KB";
		} else if (fileSize >= (1024f * 1024f) && fileSize < (1024f * 1024f * 1024f)) {
			showSize = df.format((fileSize / (1024f * 1024f))) + "MB";
		} else if (fileSize >= (1024f * 1024f * 1024f)) {
			showSize = df.format(fileSize / (1024f * 1024f * 1024f)) + "GB";
		}
		return showSize;
	}

	public Button getButton(String Button_Type) {
		Button button = null;
		switch (Button_Type) {
		case YES:
			button = (Button) mContentView.findViewById(R.id.btn_yes);
			break;
		case CANCEL:
			button = (Button) mContentView.findViewById(R.id.btn_cancel);
			break;
		case IGNORE:
			button = (Button) mContentView.findViewById(R.id.btn_ignore);
			break;
		}
		return button;
	};

	public static int getdailogTheme(Context context) {
		final TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.dialogTheme, outValue, true);
		return outValue.resourceId;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			for (int id : new int[] { R.id.btn_yes, R.id.btn_cancel, R.id.btn_ignore }) {
				Button button = (Button) findViewById(id);
				if (button.getVisibility() == View.VISIBLE) {
					//button.setTextColor(SkinManager.getInstance().getColorPrimaryDark());
				}
			}

		}

	}
}
