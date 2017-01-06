package com.tianyoustudio.update.library.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

	final static String DBNAME = "download_db";
	final static int DBVERSION = 1;

	final static String TABLENAME = "download";
	final static String DOWNLOAD_NAME = "download_name";
	final static String DOWNLOAD_URL = "download_url";
	final static String DOWNLOAD_STATE = "download_state";
	final static String DOWNLOAD_TOTALSIZE = "download_totalsize";
	final static String DOWNLOAD_MD5 = "download_md5";

	public SqliteHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String sql = "create table if not exists " + TABLENAME + " (_id integer primary key autoincrement, "
				+ DOWNLOAD_NAME + " text," + DOWNLOAD_URL + " text," + DOWNLOAD_STATE + " integer, "
				+ DOWNLOAD_TOTALSIZE + " text, " + DOWNLOAD_MD5 + " text)";
		sqLiteDatabase.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

	}
}
