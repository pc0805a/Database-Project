package com.example.weather;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class GetWeatherInfo extends AsyncTask<String, Integer, String> {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	private String info;

	@Override
	protected String doInBackground(String... params) {
	    
		return null;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	

}