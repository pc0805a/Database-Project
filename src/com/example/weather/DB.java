package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public static final String DATABASE_NAME = "history.db";
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_TABLE = "history";
		public static final String DATABASE_CREATE = "CREATE table history("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "_woeid TEXT NOT NULL, "
				+ "_name TEXT, " + "_condition TEXT, " + "_humidity TEXT, "
				+ "_temperature TEXT, " + "_reliability DOUBLE64, "
				+ "_last_update TEXT" + "); ";

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);

		}
	}

	private static final String TAG = MainActivity.class.getSimpleName();

	private Context mContext = null;
	private DatabaseHelper dbHelper;
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
		dbHelper = new DatabaseHelper(mContext);
		db = dbHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getAll() {
		String[] strCol = new String[] { KEY_ROWID, KEY_WOEID, KEY_NAME,
				KEY_CONDI, KEY_HUMI, KEY_TEMP, KEY_RELI, KEY_LASTUP };
		return db.query(DatabaseHelper.DATABASE_TABLE, strCol, null, null,
				null, null, KEY_ROWID + " DESC");
	}

	public Long insert(String[] data) {
		ContentValues args = new ContentValues();
		args.put(KEY_ROWID, 1);
		args.put(KEY_WOEID, data[0]);
		args.put(KEY_NAME, data[1]);
		args.put(KEY_CONDI, data[2]);
		args.put(KEY_HUMI, data[3]);
		args.put(KEY_TEMP, data[4]);
		args.put(KEY_RELI, Double.parseDouble(data[5]));
		args.put(KEY_LASTUP, data[6]);

		Log.v(TAG, "Insert Done!!");

		return db.insert(DatabaseHelper.DATABASE_TABLE, null, args);

	}
	

	public boolean delete(long rowId) {

		Log.v(TAG, "Delete Done!!");

		return db.delete(DatabaseHelper.DATABASE_TABLE,
				KEY_ROWID + "=" + rowId, null) > 0;

	}
}
