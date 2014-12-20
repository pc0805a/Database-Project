package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;

public class GetDbInfo extends AsyncTask<Void, Void, String[]> {
	
	private static final String TAG = GetDbInfo.class.getSimpleName();
	
	private String URI = "http://pc0805a.lionfree.net/weather/getDbInfo.php";
	int woeid;
	private String[] result;

	GetDbInfo(String woeid) {
		this.woeid = Integer.parseInt(woeid);
	}
//	http://j796160836.pixnet.net/blog/post/28994669-%5Bandroid%5D-%E4%BD%BF%E7%94%A8http%E7%9A%84post%E6%96%B9%E5%BC%8F%E5%92%8C%E7%B6%B2%E9%A0%81%E8%A1%A8%E5%96%AE%E6%BA%9D%E9%80%9A
	@Override
	protected String[] doInBackground(Void... params) {

		try {
			URL url = new URL(URI);
			String data = URLEncoder.encode("woeid", "UTF-8") + "="
					+ woeid;
			URLConnection conn = url.openConnection();

			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());

//			wr.write(data);

//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					conn.getInputStream()));
//			
//			StringBuilder sb = new StringBuilder();
//
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			String tempResult = sb.toString();
//			
//			Log.v(TAG, tempResult);
			

		} catch (MalformedURLException err) {
			Log.e(TAG, "error: "+err.toString());
		} catch (UnsupportedEncodingException err) {
			Log.e(TAG, "error: "+err.toString());
		} catch (IOException err) {
			Log.e(TAG, "error: "+err.toString());
		}

		return null;
	}

}
