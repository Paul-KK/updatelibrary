package com.tianyoustudio.update.library.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class SqliteManager {

	private static SqliteManager manager = null;
	private static SqliteHelper helper = null;



	public static synchronized SqliteManager getInstance(Context context) {
		if (manager == null) {
			helper = new SqliteHelper(context);
			manager = new SqliteManager();
		}
		return manager;
	}

	/**
	 * 更新表状态
	 * 
	 * @param url
	 *            下载的链接
	 * @param state
	 *            下载的状态
	 * @param totalSize
	 *            下载的总大小
	 */
	public void updateDownloadData(String packageName, String url, int state, String totalSize, String Md5) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cs = db.query(SqliteHelper.TABLENAME, null, SqliteHelper.DOWNLOAD_NAME + "=?", new String[] { packageName },
				null, null, null);
		cs.moveToFirst();
		int count = cs.getCount();
		cs.close();
		if (count > 0) {
			ContentValues cv = new ContentValues();
			cv.put(SqliteHelper.DOWNLOAD_NAME, packageName);
			cv.put(SqliteHelper.DOWNLOAD_URL, url);
			cv.put(SqliteHelper.DOWNLOAD_STATE, state);
			cv.put(SqliteHelper.DOWNLOAD_TOTALSIZE, totalSize);
			cv.put(SqliteHelper.DOWNLOAD_MD5, Md5);
			db.update(SqliteHelper.TABLENAME, cv, SqliteHelper.DOWNLOAD_URL + "=?", new String[] { url });
		} else {
			ContentValues cv = new ContentValues();
			cv.put(SqliteHelper.DOWNLOAD_NAME, packageName);
			cv.put(SqliteHelper.DOWNLOAD_URL, url);
			cv.put(SqliteHelper.DOWNLOAD_STATE, state);
			cv.put(SqliteHelper.DOWNLOAD_TOTALSIZE, totalSize);
			cv.put(SqliteHelper.DOWNLOAD_MD5, Md5);
			db.insert(SqliteHelper.TABLENAME, null, cv);
			
			
			System.out.println("-------db.insert(SqliteHelper.TABLENAME, null, cv);");
		}
		db.close();
	}

	/**
	 * 获取所有下载信息
	 * 
	 * @return
	 */
	public ArrayList<DownloadModel> getAllDownloadInfo() {
		ArrayList<DownloadModel> models = new ArrayList<DownloadModel>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cs = db.query(SqliteHelper.TABLENAME, null, null, null, null, null, null);
		cs.moveToFirst();
		int count = cs.getCount();
		for (int i = 0; i < count; i++) {
			cs.moveToPosition(i);
			DownloadModel model = new DownloadModel();
			model.setDOWNLOAD_NAME(cs.getString(cs.getColumnIndex(SqliteHelper.DOWNLOAD_NAME)));
			model.setDOWNLOAD_NAME(cs.getString(cs.getColumnIndex(SqliteHelper.DOWNLOAD_URL)));
			model.setDOWNLOAD_STATE(cs.getInt(cs.getColumnIndex(SqliteHelper.DOWNLOAD_STATE)));
			model.setDOWNLOAD_TOTALSIZE(cs.getString(cs.getColumnIndex(SqliteHelper.DOWNLOAD_TOTALSIZE)));
			model.setDOWNLOAD_MD5(cs.getString(cs.getColumnIndex(SqliteHelper.DOWNLOAD_MD5)));
			models.add(model);
		}
		cs.close();
		db.close();
		return models;
	}

	/** 删除指定的下载 */
	public int deleteByUrl(String url) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int delete = db.delete(SqliteHelper.TABLENAME, SqliteHelper.DOWNLOAD_URL + "=?", new String[] { url });
		db.close();
		return delete;
	}

	/** 删除所有下载信息 */
	public int deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		int delete = db.delete(SqliteHelper.TABLENAME, null, null);
		db.close();
		return delete;
	}

	public boolean isContains(String url) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cs = db.query(SqliteHelper.TABLENAME, null, SqliteHelper.DOWNLOAD_URL + "=?", new String[] { url }, null,
				null, null);
		cs.moveToFirst();
		for (int i = 0; i < cs.getCount(); i++) {
			int a = cs.getInt(cs.getColumnIndex(SqliteHelper.DOWNLOAD_STATE));
			if (a == ParamsManager.State_FINISH) {
				cs.close();
				db.close();
				return true;
			}
			cs.moveToNext();
		}
		cs.close();
		db.close();
		return false;
	}

	public String getMd5(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cs = db.query(SqliteHelper.TABLENAME, null, SqliteHelper.DOWNLOAD_NAME + "=?",
				new String[] { packageName }, null, null, null);
		cs.moveToFirst();
		for (int i = 0; i < cs.getCount(); i++) {
			String md5 = cs.getString(cs.getColumnIndex(SqliteHelper.DOWNLOAD_MD5));
			if (md5 != null) {
				return md5;
			}
			cs.moveToNext();
		}
		cs.close();
		db.close();
		return "";
	}
}
