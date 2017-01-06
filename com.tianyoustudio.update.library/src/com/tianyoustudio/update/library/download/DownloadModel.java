package com.tianyoustudio.update.library.download;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DownloadModel implements Serializable {

	private String DOWNLOAD_NAME = "";
	private int DOWNLOAD_STATE = ParamsManager.State_NORMAL;
	private String DOWNLOAD_TOTALSIZE = "";
	private String DOWNLOAD_MD5 = "";

	public String getDOWNLOAD_MD5() {
		return DOWNLOAD_MD5;
	}

	public void setDOWNLOAD_MD5(String dOWNLOAD_MD5) {
		DOWNLOAD_MD5 = dOWNLOAD_MD5;
	}

	public String getDOWNLOAD_NAME() {
		return DOWNLOAD_NAME;
	}

	public void setDOWNLOAD_NAME(String DOWNLOAD_NAME) {
		this.DOWNLOAD_NAME = DOWNLOAD_NAME;
	}

	public int getDOWNLOAD_STATE() {
		return DOWNLOAD_STATE;
	}

	public void setDOWNLOAD_STATE(int DOWNLOAD_STATE) {
		this.DOWNLOAD_STATE = DOWNLOAD_STATE;
	}

	public String getDOWNLOAD_TOTALSIZE() {
		return DOWNLOAD_TOTALSIZE;
	}

	public void setDOWNLOAD_TOTALSIZE(String DOWNLOAD_TOTALSIZE) {
		this.DOWNLOAD_TOTALSIZE = DOWNLOAD_TOTALSIZE;
	}

}
