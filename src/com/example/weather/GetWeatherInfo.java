package com.example.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetWeatherInfo extends AsyncTask<Void, Void, JSONObject> {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	String query;
	String result;
	JSONObject jArray = null;

	GetWeatherInfo(String query)
	{
		this.query = query;
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		
		getJson(query);
		
		try {
			jArray = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jArray;
	}
	
	private void getJson(String query) {
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
			
		try {
			String totalUrl = baseUrl + URLEncoder.encode(query, "UTF-8") + "&format=json";
			
			Log.v(TAG, "Total URL:" + totalUrl);
			
			
			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpPost httppost = new HttpPost(totalUrl);
			
			Log.v(TAG, "URI:" + httppost.getURI());
			
			
			httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			
			try {
			    HttpResponse response = httpclient.execute(httppost);           
			    HttpEntity entity = response.getEntity();

			    inputStream = entity.getContent();
			    // json is UTF-8 by default
			    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			    StringBuilder sb = new StringBuilder();

			    String line = null;
			    while ((line = reader.readLine()) != null)
			    {
			        sb.append(line + "\n");
			    }
			    result = sb.toString();
			    Log.v(TAG,  "JSON result: "+result);
			} catch (Exception err) { 
				Log.v(TAG, err.toString());
			}
			finally {
			    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
			}
			
		} catch (UnsupportedEncodingException err) {
			// TODO Auto-generated catch block
			Log.v(TAG, err.toString());
		}

		
	}
	


	

}