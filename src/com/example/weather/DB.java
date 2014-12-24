package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DB {
	private static final String TAG = MainActivity.class.getSimpleName();

	private Context mContext = null;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private String woeid;

	public static final String KEY_ROWID = "_id";
	public static final String KEY_WOEID = "_woeid";
	public static final String KEY_NAME = "_name";
	public static final String KEY_CONDI = "_condition";
	public static final String KEY_HUMI = "_humidity";
	public static final String KEY_TEMP = "_temperature";
	public static final String KEY_RELI = "_reliability";
	public static final String KEY_LASTUP = "_last_update";

	public DB(Context context) {
		this.mContext = context;
	}

	public DB open() throws SQLException {
		dbHelper = new DBHelper(mContext);
		db = dbHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getAll() {
		String[] strCol = new String[] { KEY_ROWID, KEY_WOEID, KEY_NAME,
				KEY_CONDI, KEY_HUMI, KEY_TEMP, KEY_RELI, KEY_LASTUP };
		return db.query(DBHelper.DATABASE_TABLE, strCol, null, null, null, null,
				KEY_ROWID + " DESC");
	}
	
	public Long Insert (String[] data)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_WOEID, data[0]);
		args.put(KEY_NAME, data[1]);
		args.put(KEY_CONDI, data[2]);
		args.put(KEY_HUMI, data[3]);
		args.put(KEY_TEMP, data[4]);
		args.put(KEY_RELI, Double.parseDouble(data[5]));
		args.put(KEY_LASTUP, data[6]);
		
		Log.v(TAG,"Insert Done!!");
		
		return db.insert(DBHelper.DATABASE_TABLE, null, args);

		
		
	}
}
