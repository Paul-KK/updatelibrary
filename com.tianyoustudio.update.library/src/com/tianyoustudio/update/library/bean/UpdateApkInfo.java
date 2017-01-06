package com.tianyoustudio.update.library.bean;

import java.io.Serializable;

public class UpdateApkInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String updateContent;
	private String download_Url;
	private String versionName;
	private String md5;
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private int versionCode;
	private long apkSize;

	public String getUpdateContent() {
		return updateContent;
	}

	public void setUpdateContent(String updateContent) {

		this.updateContent = updateContent.replaceAll("<br>", "\n");
	}

	public String getDownload_Url() {
		return download_Url;
	}

	public void setDownload_Url(String download_Url) {
		this.download_Url = download_Url;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public long getApkSize() {
		return apkSize;
	}

	public void setApkSize(long apkSize) {
		this.apkSize = apkSize;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
