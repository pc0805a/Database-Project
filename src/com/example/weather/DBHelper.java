package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
		public static final String DATABASE_NAME = "history.db";
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_TABLE = "history";
		public static final String DATABASE_CREATE = "CREATE table history("
				+ "_id INTEGER PRIMARYKEY, "
				+ "_woeid TEXT NOT NULL, "
				+ "_name TEXT, "
				+ "_condition TEXT, "
				+ "_humidity TEXT, "
				+ "_temperature TEXT, "
				+ "_reliability DOUBLE64, " 
				+ "_last_update TEXT"
				+"); ";

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
			onCreate(db);

		}
}