package com.example.weather;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.os.AsyncTask;
import android.provider.DocumentsContract.Document;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class GetDbInfo extends AsyncTask<Void, Void, String[]> {

	private static final String TAG = GetDbInfo.class.getSimpleName();

	private String baseURL = "http://pc0805a.lionfree.net/weather/getDbInfo.php?";
	int woeid;
	private String[] result = new String[7];

	GetDbInfo(String woeid) {
		this.woeid = Integer.parseInt(woeid);
	}

	@Override
	protected String[] doInBackground(Void... params) {

		String link = "http://pc0805a.lionfree.net/weather/getDbInfo.php?woeid="
				+ woeid;

		DefaultHttpClient httpclient = new DefaultHttpClient(
				new BasicHttpParams());
		HttpPost httppost = new HttpPost(link);

		InputStream inputStream = null;

		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			if (Debug.on) {
				Log.v(TAG, "Original Page Info: " + sb.toString());
			}

			org.jsoup.nodes.Document doc = Jsoup.parse(sb.toString());

			String plainText = doc.body().text();
			if (Debug.on) {
				Log.v(TAG, "Plain Text: " + plainText);
			}
				
				result = plainText.split(" ");

		} catch (Exception err) {
			Log.e(TAG, "error: " + err.toString());
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception err) {
				if (Debug.on) {
					Log.e(TAG, "error: " + err.toString());
				}
			}
		}
		
		
		

		return result;

	}
}
