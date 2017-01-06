package com.tianyoustudio.update.library.bean;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.tianyoustudio.update.library.download.DownloadManager;
import com.tianyoustudio.update.library.download.DownloadModel;
import com.tianyoustudio.update.library.download.SqliteManager;
import com.tianyoustudio.update.library.listener.OnlineCheckListener;
import com.tianyoustudio.update.library.listener.UpdateListener;
import com.tianyoustudio.update.type.UpdateType;

import java.util.List;
import java.util.TreeMap;

public class UpdateHelper {

	private SqliteManager manager;
	private Context mContext;
	private String checkUrl;
	private TreeMap<String, Object> checkParams;
	private String onlineUrl;
	private ParseData parserCheckJson;
	private ParseData parserOnlineJson;
	private UpdateListener mUpdateListener;
	private OnlineCheckListener mOnlineCheckListener;
	private static UpdateHelper instance;
	// 双重嵌套一级是否强制更新
	@SuppressWarnings("unused")
	private boolean updateForce = false;

	// 二级（1.手动更新2.自动更新（有网更新，只有WiFi更新，只有WiFi下载））
	public enum RequestType {
		get, post
	}

	// 联网请求方式
	private RequestType mRequestType = RequestType.get;

	//

	// 默认3g/4g网络也自动检测更新
	private UpdateType mUpdateType = UpdateType.autocheck;

	public UpdateHelper(Context context) {
		this.mContext = context;
		// 添加下载状态列表
		manager = SqliteManager.getInstance(mContext);
		List<DownloadModel> models = manager.getAllDownloadInfo();
		DownloadManager.getInstance(mContext).addStateMap(models);

	}

	public static void init(Context context) {
		instance = new UpdateHelper(context);
	}

	public static UpdateHelper getInstance() {
		if (instance == null) {
			throw new RuntimeException("UpdateHelper not initialized!");
		} else {
			return instance;
		}
	}

	public UpdateHelper setMethod(RequestType requestType) {
		this.mRequestType = requestType;
		return this;
	}

	public UpdateHelper setCheckUrl(String url) {
		this.checkUrl = url;
		return this;
	}

	public UpdateHelper setCheckUrl(String url, TreeMap<String, Object> params) {
		this.checkUrl = url;
		this.checkParams = params;
		return this;
	}

	public UpdateHelper setOnlineUrl(String url) {
		this.onlineUrl = url;
		return this;
	}

	public UpdateHelper setUpdateListener(UpdateListener listener) {
		this.mUpdateListener = listener;
		return this;
	}

	public UpdateHelper setOnlineCheckListener(OnlineCheckListener listener) {
		this.mOnlineCheckListener = listener;
		return this;
	}

	public UpdateHelper setCheckJsonParser(ParseData jsonParser) {
		this.parserCheckJson = jsonParser;
		return this;
	}

	public UpdateHelper setOnlineJsonParser(ParseData jsonParser) {
		this.parserOnlineJson = jsonParser;
		return this;
	}

	public UpdateHelper setUpdateType(UpdateType updateType) {
		this.mUpdateType = updateType;
		return this;
	}

	public Context getContext() {
		if (mContext == null) {
			throw new RuntimeException("should call UpdateConfig.install first");
		}
		return mContext;
	}

	public UpdateType getUpdateType() {
		return mUpdateType;
	}

	public String getCheckUrl() {
		if (TextUtils.isEmpty(checkUrl)) {
			throw new IllegalArgumentException("checkUrl is null");
		}
		return checkUrl;
	}

	public TreeMap<String, Object> getCheckParams() {
		if (checkParams == null) {
			throw new IllegalArgumentException("checkParams is null");
		}
		return checkParams;
	}

	public String getOnlineUrl() {
		return onlineUrl;
	}

	public RequestType getRequestMethod() {
		return mRequestType;
	}

	public ParseData getCheckJsonParser() {
		if (parserCheckJson == null) {
			throw new IllegalStateException("update parser is null");
		}
		return parserCheckJson;
	}

	public ParseData getOnlineJsonParser() {
		return parserOnlineJson;
	}

	public void check(Activity activity) {
		UpdateAgent.getInstance().checkUpdate(activity);
	}

	public UpdateListener getUpdateListener() {
		return mUpdateListener;
	}

	public OnlineCheckListener getOnlineCheckListener() {
		return mOnlineCheckListener;
	}
}
