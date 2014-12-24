package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



public class DB {
	
	private Context mContext = null;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	public DB(Context context)
	{
		this.mContext = context;
	}
	
	public DB open()
	{
		dbHelper = new DBHelper(mContext);
		db = dbHelper.getWritableDatabase();
		
		return this;		
	}

}
