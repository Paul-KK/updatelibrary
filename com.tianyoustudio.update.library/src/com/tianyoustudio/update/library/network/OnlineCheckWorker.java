package com.tianyoustudio.update.library.network;

import com.tianyoustudio.update.library.bean.ParseData;
import com.tianyoustudio.update.library.download.SslUtils;
import com.tianyoustudio.update.library.listener.OnlineCheckListener;
import com.tianyoustudio.update.library.util.HandlerUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class OnlineCheckWorker implements Runnable {

	protected String url;
	protected OnlineCheckListener checkCB;
	protected ParseData parser;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUpdateListener(OnlineCheckListener checkCB) {
		this.checkCB = checkCB;
	}

	public void setParser(ParseData parser) {
		this.parser = parser;
	}

	@Override
	public void run() {

		try {
			String response = check(url);
			String parse = parser.parse(response);
			if (parse == null) {
				throw new IllegalArgumentException(
						"parse response to update failed by " + parser.getClass().getCanonicalName());
			}
			sendHasUpdate(parse);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected String check(String urlStr) throws Exception {
		URL Url = new URL(urlStr);
		StringBuilder sb = new StringBuilder();
		BufferedReader bis;
		String lines;
		//
		if (urlStr.toLowerCase().startsWith("https")) {
			SslUtils.ignoreSsl();
			HttpsURLConnection httpsConn = (HttpsURLConnection) Url.openConnection();
			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);
			httpsConn.setUseCaches(false);
			httpsConn.setConnectTimeout(10000);
			httpsConn.setRequestMethod("GET");
			httpsConn.connect();
			int responseCode = httpsConn.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				throw new HttpException(responseCode, httpsConn.getResponseMessage());
			}
			bis = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "utf-8"));

		} else {
			HttpURLConnection httpConn = (HttpURLConnection) Url.openConnection();
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setUseCaches(false);
			httpConn.setConnectTimeout(10000);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			int responseCode = httpConn.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				throw new HttpException(responseCode, httpConn.getResponseMessage());
			}
			bis = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));

		}
		//
	
		while ((lines = bis.readLine()) != null) {
			sb.append(lines);
		}
		return sb.toString();

	}

	private void sendHasUpdate(final String update) {
		if (checkCB == null)
			return;
		HandlerUtil.getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				checkCB.hasParams(update);
			}
		});
	}
}
