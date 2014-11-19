package com.example.weather;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class GetWeatherInfo extends AsyncTask<String, Void, String[]> {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected String[] doInBackground(String... params) {

		URL fullUrl;
		InputStream is;
		try {
			fullUrl = new URL(params[0]);
			is = fullUrl.openStream();
			if (Debug.on == true) {
				Log.v(TAG, "verbose"+is.toString() );
			}
			
			JSONTokener tok = new JSONTokener(is.toString());
			JSONObject result = new JSONObject(tok);
			is.close();
		} catch (Exception err) {
			// TODO Auto-generated catch block
			Log.e(TAG, "error: " + err.toString());
		}
		
		String[] weatherInfo = null;

		return weatherInfo;
	}

	@Override
	protected void onPostExecute(String[] result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

}