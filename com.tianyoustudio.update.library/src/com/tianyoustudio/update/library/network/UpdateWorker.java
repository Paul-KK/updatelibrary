package com.tianyoustudio.update.library.network;

import com.tianyoustudio.update.library.bean.ParseData;
import com.tianyoustudio.update.library.bean.UpdateApkInfo;
import com.tianyoustudio.update.library.bean.UpdateHelper;
import com.tianyoustudio.update.library.download.SslUtils;
import com.tianyoustudio.update.library.listener.UpdateListener;
import com.tianyoustudio.update.library.util.CurrentApkInfo;
import com.tianyoustudio.update.library.util.HandlerUtil;
import com.tianyoustudio.update.library.util.NetWorkUtil;
import com.tianyoustudio.update.library.util.UpdateSP;
import com.tianyoustudio.update.type.UpdateType;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

public class UpdateWorker implements Runnable {

	protected String url;
	protected TreeMap<String, Object> checkParams;
	protected UpdateListener checkCB;
	protected ParseData parser;
	private UpdateHelper.RequestType requestType;
	private Context context;

	public UpdateWorker(Context context) {
		this.context = context;
	}

	public void setRequestMethod(UpdateHelper.RequestType requestType) {
		this.requestType = requestType;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setParams(TreeMap<String, Object> checkParams) {
		this.checkParams = checkParams;
	}

	public void setUpdateListener(UpdateListener checkCB) {
		this.checkCB = checkCB;
	}

	public void setParser(ParseData parser) {
		this.parser = parser;
	}

	@Override
	public void run() {
		try {
			String response = null;
			if (checkParams != null) {
				response = check(requestType, url, checkParams);
			} else {
				response = check(requestType, url);
			}
			UpdateApkInfo parse = parser.parse(response);
			if (parse == null) {
				throw new IllegalArgumentException(
						"parse response to update failed by " + parser.getClass().getCanonicalName());
			}
			int currentApkVersionCode = CurrentApkInfo.getApkVersionCode(UpdateHelper.getInstance().getContext());

			if (UpdateHelper.getInstance().getUpdateType() == UpdateType.autocheck) {
				if (NetWorkUtil.isNetWorkConnected(context)) {
					ToastMessage("3g/4g网络自动检测更新");
				} else {
					ToastMessage("3g/4g网络自动检测更新，却没有联网");
					return;
				}

			} else if (UpdateHelper.getInstance().getUpdateType() == UpdateType.wifiautocheck) {
				if (NetWorkUtil.isWifiConnected(context)) {
					ToastMessage("Wifi网络自动检测更新");
				} else {
					ToastMessage("Wifi网络自动检测更新，却没有联Wifi");
					return;
				}

			} else if (UpdateHelper.getInstance().getUpdateType() == UpdateType.forcecheck) {
				if (NetWorkUtil.isNetWorkConnected(context)) {
					ToastMessage("手动检测更新");
				} else {
					ToastMessage("手动检测更新，却没有联网");
					return;
				}
			}

			if (parse.getVersionCode() > currentApkVersionCode && (!UpdateSP.isIgnore(parse.getVersionCode() + "")
					|| UpdateHelper.getInstance().getUpdateType() == UpdateType.forcecheck)) {
				
				 /**有新版本*/
				sendHasUpdate(parse);
			} else {
				if (parse.getVersionCode() > currentApkVersionCode && UpdateSP.isIgnore(parse.getVersionCode() + "")) {
					sendNoUpdate("有更新但是忽略了，建议在设置里手动更新");
				} else {
					sendNoUpdate("已经是最新版本了");
				}
			}

		} catch (HttpException he) {
			System.err.println("-----------------------------------网络连接问题，请检测网络");
			sendOnErrorMsg(he.getCode(), he.getErrorMsg());
		} catch (Exception e) {
			sendOnErrorMsg(-1, e.getMessage());
			System.err.println("-----------------------------------其他问题");
		}
	}

	private void ToastMessage(final String msg) {
		HandlerUtil.getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();;
			}
		});
		
	}

	protected String check(UpdateHelper.RequestType requestType, String urlStr) throws Exception {
		URL getUrl = new URL(urlStr);
		StringBuilder sb = new StringBuilder();
		BufferedReader bis;
		String lines;
		//
		if (urlStr.toLowerCase().startsWith("https")) {
			SslUtils.ignoreSsl();
			HttpsURLConnection httpsConn = (HttpsURLConnection) getUrl.openConnection();

			httpsConn.setUseCaches(false);
			httpsConn.setConnectTimeout(10000);
			if (requestType == UpdateHelper.RequestType.get) {
				httpsConn.setRequestMethod("GET");
			} else {
				httpsConn.setRequestMethod("POST");
				httpsConn.setDoInput(true);
				httpsConn.setDoOutput(true);
			}
			httpsConn.connect();
			int responseCode = httpsConn.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				throw new HttpException(responseCode, httpsConn.getResponseMessage());
			}
			bis = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "UTF-8"));
		} else {
			HttpURLConnection httpConn = (HttpURLConnection) getUrl.openConnection();
			httpConn.setUseCaches(false);
			httpConn.setConnectTimeout(10000);
			if (requestType == UpdateHelper.RequestType.get) {
				httpConn.setRequestMethod("GET");
			} else {
				httpConn.setRequestMethod("POST");
				httpConn.setDoInput(true);
				httpConn.setDoOutput(true);
			}
			httpConn.connect();
			int responseCode = httpConn.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				throw new HttpException(responseCode, httpConn.getResponseMessage());
			}

			bis = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
		}
		//
		while ((lines = bis.readLine()) != null) {
			sb.append(lines);
		}
		return sb.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected String check(UpdateHelper.RequestType requestType, String url, Map<String, Object> requestParams) {

		if (requestParams == null) {
			requestParams = new TreeMap();
		}
		String result = "";
		BufferedReader in = null;
		String paramStr = "";
		Iterator realUrl = requestParams.keySet().iterator();

		String paj;
		while (realUrl.hasNext()) {
			paj = (String) realUrl.next();
			if (!paramStr.isEmpty()) {
				paramStr = paramStr + '&';
			}
			try {
				paramStr = paramStr + paj + '=' + URLEncoder.encode(requestParams.get(paj).toString(), "UTF-8");
			} catch (UnsupportedEncodingException var28) {
				result = "{\"code\":-2100,\"location\":\"Request:120\",\"message\":\"api sdk throw exception! "
						+ var28.toString() + "\"}";
			}
		}

		try {
			if (requestType == UpdateHelper.RequestType.get) {
				if (url.indexOf(63) > 0) {
					url = url + '&' + paramStr;
				} else {
					url = url + '?' + paramStr;
				}
			}

			paj = "---------------------------";
			URL realUrl1 = new URL(url);
			Object connection = null;
			if (url.toLowerCase().startsWith("https")) {
				SslUtils.ignoreSsl();
				connection = (HttpsURLConnection) realUrl1.openConnection();
			} else {
				connection = realUrl1.openConnection();
			}

			((URLConnection) connection).setRequestProperty("accept", "*/*");
			((URLConnection) connection).setRequestProperty("connection", "Keep-Alive");
			((URLConnection) connection).setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			((URLConnection) connection).setConnectTimeout(10000);
			if (requestType == UpdateHelper.RequestType.post) {
				((HttpURLConnection) connection).setRequestMethod("POST");
				((URLConnection) connection).setDoOutput(true);
				((URLConnection) connection).setDoInput(true);
				((URLConnection) connection).setRequestProperty("Content-Type", "multipart/form-data; boundary=" + paj);
				DataOutputStream line1 = new DataOutputStream(((URLConnection) connection).getOutputStream());
				StringBuffer strBuf = new StringBuffer();
				Iterator filename = requestParams.keySet().iterator();

				while (filename.hasNext()) {
					String endData = (String) filename.next();
					if (!endData.equals("Debug")) {
						strBuf.append("\r\n").append("--").append(paj).append("\r\n");
						strBuf.append("Content-Disposition: form-data; name=\"" + endData + "\"\r\n\r\n");
						strBuf.append(requestParams.get(endData));
					}
				}
				System.out.println("URL----POST:" + url);
				System.out.println("URL----PARAMS:" + strBuf.toString());

				line1.write(strBuf.toString().getBytes());

				byte[] endData2 = ("\r\n--" + paj + "--\r\n").getBytes();
				line1.write(endData2);
				line1.flush();
				line1.close();
			}

			((URLConnection) connection).connect();

			String line2;
			for (in = new BufferedReader(new InputStreamReader(
					((URLConnection) connection).getInputStream())); (line2 = in.readLine()) != null; result = result
							+ line2) {
				;
			}
		} catch (Exception var29) {
			result = "{\"code\":-2200,\"location\":\"Request:220\",\"message\":\"api sdk throw exception! "
					+ var29.toString() + "\"}";
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception var27) {
				result = "{\"code\":-2300,\"location\":\"Request:219\",\"message\":\"api sdk throw exception! "
						+ var27.toString() + "\"}";
			}

		}
		return result;
	}

	private void sendHasUpdate(final UpdateApkInfo update) {
		if (checkCB == null)
			return;
		HandlerUtil.getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				checkCB.hasUpdate(update);
			}
		});
	}

	private void sendNoUpdate(final String msg) {
		if (checkCB == null)
			return;
		HandlerUtil.getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				checkCB.noUpdate(msg);
			}
		});
	}

	private void sendOnErrorMsg(final int code, final String errorMsg) {
		if (checkCB == null)
			return;
		HandlerUtil.getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				checkCB.onCheckError(code, errorMsg);
			}
		});
	}
}
