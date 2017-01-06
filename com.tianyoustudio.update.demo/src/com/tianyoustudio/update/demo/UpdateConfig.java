package com.tianyoustudio.update.demo;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import org.json.JSONObject;

import com.tianyoustudio.update.library.bean.ParseData;
import com.tianyoustudio.update.library.bean.UpdateApkInfo;
import com.tianyoustudio.update.library.bean.UpdateHelper;
import com.tianyoustudio.update.library.util.CurrentApkInfo;
import com.tianyoustudio.update.library.util.UpdateSP;

public class UpdateConfig {
	static int CURRENT_VERSIONCODE = 0;
	static final String APP_KEY = "357d421c6c62d1b9bfc84296acab53c2";
	private static String checkUrl = "https://weixin.cqjtu.edu.cn/android/checkupdate?app_key=" + APP_KEY
			+ "&versionCode=" + CURRENT_VERSIONCODE;

	public static void init(Context context) {
		try {
			CURRENT_VERSIONCODE = CurrentApkInfo.getApkVersionCode(context);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		UpdateHelper.init(context);
		UpdateHelper.getInstance()
				// 可填：请求方式
				.setMethod(UpdateHelper.RequestType.get)
				// 必填：数据更新接口
				.setCheckUrl(checkUrl)
				// 必填：用于从数据更新接口获取的数据response中。解析出Update实例。以便框架内部处理
				.setCheckJsonParser(new ParseData() {
					@SuppressWarnings("unchecked")
					@Override
					public UpdateApkInfo parse(String httpResponse) {
						// 此处添加并初始化一个UpdateApkInfo对象
						UpdateApkInfo mUpdateApkInfo = new UpdateApkInfo();
						initUpdateApkInfo(httpResponse, mUpdateApkInfo);
						// 是否为强制更新
						UpdateSP.setForced(false);
						return mUpdateApkInfo;

					}
				});

	}

	private static void initUpdateApkInfo(String response, UpdateApkInfo mUpdateApkInfo) {
		try {
			JSONObject jobj = new JSONObject(response);

			if (!jobj.isNull("data")) {
				JSONObject job = jobj.optJSONObject("data");
				if (!job.isNull("versionCode")) {
					mUpdateApkInfo.setVersionCode(Integer.valueOf(job.optString("versionCode")));
				}
				if (!job.isNull("size")) {
					mUpdateApkInfo.setApkSize(job.optLong("size"));
				}
				if (!job.isNull("versionName")) {
					mUpdateApkInfo.setVersionName(job.optString("versionName"));
				}
				if (!job.isNull("describe")) {
					mUpdateApkInfo.setUpdateContent(job.optString("describe"));
				}
				if (!job.isNull("download_url")) {
					mUpdateApkInfo.setDownload_Url(job.optString("download_url"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
