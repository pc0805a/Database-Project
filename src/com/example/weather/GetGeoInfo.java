package com.example.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetGeoInfo extends AsyncTask<Void, Void, String[]> {

	private static final String TAG = MainActivity.class.getSimpleName();

	double lng = -999999;
	double lat = -999999;
	int level = 1;

	String Gresult;
	String[] result = new String[3];
	boolean search = false;
	String searchLocation;

	GetGeoInfo(String searchLocation) {
		this.searchLocation = searchLocation;
		level = 0;
		search = true;

	}

	GetGeoInfo(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;

	}

	@Override
	protected String[] doInBackground(Void... params) {
		getGeoName();
		try {
			JSONObject jObject = new JSONObject(Gresult);
			JSONArray jResults = jObject.getJSONArray("results");
			JSONObject jResult = jResults.getJSONObject(level);
			String jFormatAddress = jResult.getString("formatted_address");
			result[0] = jFormatAddress;

			if (Debug.on) {
				Log.v(TAG, "jResults: " + jFormatAddress);
			}
			result[0] = jFormatAddress;
			JSONObject jGeometry = jResult.getJSONObject("geometry");
			JSONObject jLocation = jGeometry.getJSONObject("location");
			lng = jLocation.getDouble("lng");
			lat = jLocation.getDouble("lat");
			result[1] = Double.toString(lng);
			result[2] = Double.toString(lat);
		} catch (JSONException err) {
			Log.e(TAG, "error: " + err.toString());
		} catch (NullPointerException err) {
			Log.e(TAG, "error: " + err.toString());
		}
		return result;
	}

	private void getGeoName() {

		String baseUrl;
		String totalUrl;

		if (search) {
			baseUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=";
			totalUrl = baseUrl + searchLocation;
		} else {
			baseUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
			totalUrl = baseUrl + lat + "," + lng;
		}

//		if (Debug.on) {
			Log.v(TAG, "Total URL:" + totalUrl);
//		}

		InputStream inputStream = null;

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpPost httppost = new HttpPost(totalUrl);

			if (Debug.on) {
				Log.v(TAG, "URI:" + httppost.getURI());
			}

			httppost.setHeader("Content-type", "application/json");

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			Gresult = sb.toString();
			if (Debug.on) {
				Log.v(TAG, "Google result: " + Gresult);
			}
		} catch (Exception err) {
			Log.e(TAG, "error: " + "asd" + err.toString());

		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception err) {
				Log.e(TAG, "error: " + err.toString());
			}
		}

	}
}