package com.tianyoustudio.update.library.download;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.tianyoustudio.update.library.bean.UpdateApkInfo;
import com.tianyoustudio.update.library.util.CurrentApkInfo;

/**
 * 描 述：下载异步任务
 */
public class DownloadTask extends AsyncTask<Void, Integer, Integer> {
	private UpdateApkInfo updateApkInfo;
	/**
	 * 二级打印tag
	 */
	private String secondTag = DownloadTask.class.getCanonicalName() + "|<-->|>>";
	/**
	 * 一次缓存的大小
	 */
	final static int BUFFER_SIZE = 1024 * 8;
	/**
	 * 上下文
	 */
	Context context = null;
	/**
	 * 下载链接
	 */
	String url = "";
	/**
	 * 下载保存的文件对象
	 */
	File file = null;
	/**
	 * 下载状态监听
	 */
	DownloadTaskListener listener = null;
	/**
	 * 当前下载量
	 */
	long downloadSize = 0;
	/**
	 * 之前断点续传总完成下载量
	 */
	long previousFileSize = 0;
	/**
	 * 文件大小
	 */
	static long totalSize = 0;
	/**
	 * 下载进度
	 */
	static int downloadPercent = 0;
	/**
	 * 下载速度
	 */
	int networkSpeed = 0;
	/**
	 * 开始时间
	 */
	long previousTime = 0;
	/**
	 * 下载使用总时间
	 */
	long totalTime = 0;
	/**
	 * 下载请求对象
	 */
	HttpsURLConnection urlConn = null;
	/**
	 * 是否因为失败而中断
	 */
	boolean interrupt = false;
	/**
	 * 是否取消
	 */
	boolean isCancel = false;

	/**
	 * 写文件监听写的进度
	 */
	private class ProgressReportingRandomAccessFile extends RandomAccessFile {

		/**
		 * 当前写文件的进度
		 */
		private int process = 0;

		/**
		 * 写文件的对象和模式
		 */
		public ProgressReportingRandomAccessFile(File file, String mode) throws FileNotFoundException {
			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
			super.write(buffer, byteOffset, byteCount);
			process += byteCount;
			publishProgress(process);
		}
	}

	;

	/**
	 * @param url
	 *            下载链接如（http://baidu.com/picture/201510060001.jpg）
	 * @param path
	 *            保存文件的路径如（/mnt/sdcard/包名/*****）
	 * @param listener
	 *            下载状态监听
	 */
	public DownloadTask(Context context, String url, String path, DownloadTaskListener listener,
			UpdateApkInfo updateApkInfo) throws IOException {
		super();
		this.updateApkInfo = updateApkInfo;
		this.context = context;
		this.url = url;
		this.listener = listener;
		// String fileName = url.substring(url.lastIndexOf("/") + 1,
		// url.length());
		String fileName = context.getPackageName() + "_" + updateApkInfo.getMd5() + "_V"
				+ updateApkInfo.getVersionName() + ".apk";
		// if (!fileName.endsWith(".apk")) {
		// fileName = fileName +"_V"+updateApkInfo.getVersionName()+ ".apk";
		// }else {
		// fileName=fileName.substring(0,fileName.length() -
		// 4)+"_V"+updateApkInfo.getVersionName()+ ".apk";
		// }

		File filePtah = new File(path);
		if (!filePtah.exists()) {
			filePtah.mkdirs();
		}
		this.file = new File(filePtah, fileName);
		if (!this.file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * 下载链接
	 *
	 * @return
	 */
	public String getDownloadUrl() {
		return url;
	}

	/**
	 * 已完成进度
	 *
	 * @return
	 */
	public static long getDownloadPercent() {
		return downloadPercent;
	}

	/**
	 * 返回下载文件总大小
	 *
	 * @return
	 */
	public static long getTotalSize() {
		return totalSize;
	}

	/**
	 * 返回下载速度
	 *
	 * @return
	 */
	public long getDownloadSpeed() {
		return networkSpeed;
	}

	/**
	 * 返回总下载时间
	 *
	 * @return
	 */
	public long getTotalTime() {
		return totalTime;
	}

	@Override
	protected Integer doInBackground(Void... voids) {
		return download();
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);
		if (isCancel) {
			return;
		}
		if (listener != null) {
			if (integer != ParamsManager.ERROR_NONE) {
				listener.errorDownload(this, integer);
			} else {
				listener.finishDownload(this);
			}
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// 设置开始下载时间
		previousTime = System.currentTimeMillis();
		if (listener != null) {
			listener.preDownload(this);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// 开始下载
		if (values.length > 1) {
		} else {
			totalTime = System.currentTimeMillis() - previousTime;
			downloadSize = values[0];
			downloadPercent = (int) ((downloadSize + previousFileSize) * 100 / totalSize);
			networkSpeed = (int) (downloadSize / totalTime);
			if (listener != null && !isCancel) {
				listener.updateProcess(this);
			}
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		isCancel = true;
		interrupt = true;
		if (listener != null) {
			listener.cancelDownload(this);
		}
	}

	/**
	 * 开始下载文件
	 */
	@SuppressWarnings("resource")
	private int download() {
		RandomAccessFile outputStream = null;
		InputStream input = null;
		try {
			URL httpUrl = new URL(url);
			SslUtils.ignoreSsl();
			urlConn = (HttpsURLConnection) httpUrl.openConnection();
			urlConn.setRequestMethod("GET");
			urlConn.setConnectTimeout(10000);
			urlConn.connect();
			int responseCode = urlConn.getResponseCode();

			if (responseCode < 200 || responseCode >= 300) {
				System.err.println("responseCode < 200 || responseCode >= 300");
				return ParamsManager.ERROR_UNKONW;
			}

			totalSize = ((URLConnection) urlConn).getContentLength();
			SqliteManager.getInstance(context).updateDownloadData(CurrentApkInfo.getPackageName(context),url, ParamsManager.State_DOWNLOAD, "" + totalSize,updateApkInfo.getMd5());
			/** 文件已经下载完成 */
			if (file.length() > 0 && totalSize > 0 && totalSize == file.length()) {
				Log.e(ParamsManager.tag, secondTag + "文件已经下载完成");
				((HttpURLConnection) urlConn).disconnect();
				urlConn = null;
				return ParamsManager.ERROR_NONE;
			}
			/** 文件没有完成下载 */
			else if (totalSize > 0 && totalSize > file.length()) {
				previousFileSize = file.length();
				urlConn.disconnect();
				urlConn = null;
				SslUtils.ignoreSsl();
				urlConn = (HttpsURLConnection) httpUrl.openConnection();
				urlConn.setRequestMethod("GET");
				urlConn.setReadTimeout(10000);
				if (previousFileSize > 0) {
					urlConn.setRequestProperty("Range", "bytes=" + previousFileSize + "-" + (totalSize - 1));
				}
				urlConn.connect();
				responseCode = urlConn.getResponseCode();
				System.err.println("responseCode=" + responseCode);
				if (responseCode < 200 || responseCode >= 300) {
					System.err.println("responseCode < 200 || responseCode >= 300");
					return ParamsManager.ERROR_UNKONW;
				}
				if (CommonUtils.getAvailableStorage() < totalSize - file.length()) {
					interrupt = true;
					urlConn.disconnect();
					urlConn = null;
					return ParamsManager.ERROR_SD_NO_MEMORY;
				} else {
					outputStream = new ProgressReportingRandomAccessFile(file, "rw");
					/** 准备开始下载 */
					publishProgress(0, (int) totalSize);
					input = urlConn.getInputStream();
					copy(input, outputStream);
					if (interrupt) {
						return ParamsManager.ERROR_BLOCK_INTERNET;
					}
					return ParamsManager.ERROR_NONE;
				}
			} else {
				/** 其他异常 */
				return ParamsManager.ERROR_UNKONW;
			}

		} catch (Exception e) {
			Log.e("catch 到了 Ssl错误", secondTag, e);
			if (urlConn != null) {
				urlConn.disconnect();
				urlConn = null;
			}
			download();
			// Activity currentActivity =
			// TestActivityManager.getInstance().getCurrentActivity();
			// Toast.makeText(currentActivity, "下载出错，请检查网络",
			// Toast.LENGTH_SHORT).show();
			return ParamsManager.ERROR_UNKONW;
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
				urlConn = null;
			}
		}
	}

	/**
	 * 写文件
	 */
	private int copy(InputStream input, RandomAccessFile out) {
		/** 本次下载总量 */
		int count = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		try {
			out.seek(out.length());
			int n = 0;
			while (!interrupt) {
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;
			}

		} catch (IOException e) {
			Log.e(ParamsManager.tag, secondTag, e);
			/** 下载过程中出现异常 */
			interrupt = true;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				Log.e(ParamsManager.tag, secondTag, e);
			}
			try {
				in.close();
			} catch (IOException e) {
				Log.e(ParamsManager.tag, secondTag, e);
			}
			try {
				input.close();
			} catch (IOException e) {
				Log.e(ParamsManager.tag, secondTag, e);
			}
		}
		return count;
	}

	/**
	 * 取消下载
	 */
	public void cancel() {
		onCancelled();
	}
}
