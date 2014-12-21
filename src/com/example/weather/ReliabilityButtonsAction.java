package com.example.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.jsoup.Jsoup;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ReliabilityButtonsAction extends AsyncTask<Void, Void, Void> {

	private static final String TAG = GetDbInfo.class.getSimpleName();
	
	private String baseURL = "http://pc0805a.lionfree.net/weather/reliability.php?";
	
	String woeid;
	String reliableCount;
	String unreliableCount;
	String totalReliableCount;
	String totalUnreliableCount;
	String lastUpdate;

	ReliabilityButtonsAction(String woeid, String reliableCount,
			String unreliableCount, String totalReliableCount, String totalUnreliableCount,
			String lastUpdate) {
		this.woeid = woeid;
		this.reliableCount = reliableCount;
		this.unreliableCount = unreliableCount;
		this.totalReliableCount = totalReliableCount;
		this.totalUnreliableCount = totalUnreliableCount;
		this.lastUpdate = lastUpdate;

	}

	@Override
	protected Void doInBackground(Void... params) {
		
		String link = "http://pc0805a.lionfree.net/weather/reliability.php?"+
				"woeid=" + woeid +
				"&reliableCount=" + reliableCount +
				"&unreliableCount=" + unreliableCount+
				"&totalReliableCount=" + totalReliableCount+
				"&totalUnreliableCount=" + totalUnreliableCount;
		if (Debug.on) {
			Log.v(TAG, "Total Link:" + link);
		}
		
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
		
		
		
		return null;

	}

}
