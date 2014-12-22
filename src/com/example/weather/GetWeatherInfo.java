package com.example.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetWeatherInfo extends AsyncTask<Void, Void, String[]> {

	private static final String TAG = MainActivity.class.getSimpleName();

	double lng;
	double lat;

	String YQLresult;
	String woeidResult;

	String[] result = new String[5];

	GetWeatherInfo(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;

	}

	@Override
	protected String[] doInBackground(Void... params) {

		String woeidQuery = "select woeid from geo.placefinder where text=\""
				+ lng + "," + lat + "\" and gflags=\"R\"";
		if (Debug.on) {
			Log.v(TAG, "WOEID Query: " + woeidQuery);
		}
		getWOEID(woeidQuery);

		String YQLquery = "select * from weather.forecast where woeid in ("
				+ woeidQuery + " )";
		if (Debug.on) {
			Log.v(TAG, "YQL Query: " + YQLquery);
		}
		getWeather(YQLquery);

		try {
			JSONObject jObject = new JSONObject(woeidResult);
			JSONObject jQuery = jObject.getJSONObject("query");
			JSONObject jResults = jQuery.getJSONObject("results");

			String woeid = jResults.getJSONObject("Result").getString("woeid");

			if (Debug.on) {
				Log.v(TAG, "WOEID: " + woeid);
			}

			result[3] = woeid;

			jObject = new JSONObject(YQLresult);
			jQuery = jObject.getJSONObject("query");
			jResults = jQuery.getJSONObject("results");

			JSONObject jChannel;
			JSONObject jItem;
			JSONObject jCondition;

			jChannel = jResults.getJSONObject("channel");
			jItem = jChannel.getJSONObject("item");
			jCondition = jItem.getJSONObject("condition");

			String condition = jCondition.getString("text");
			weatherCode = Integer.parseInt(jCondition.getString("code"));
			String temperature = jCondition.getString("temp");

			JSONObject jAtom = jChannel.getJSONObject("atmosphere");
			String humidity = jAtom.getString("humidity");
			if (Debug.on) {
				Log.v(TAG, "condition: " + condition);
				Log.v(TAG, "humidity: " + humidity);
				Log.v(TAG, "temperature: " + temperature);
			}
			result[0] = condition;
			weatherNameSet(result[0]);
			result[1] = humidity;// %
			result[2] = temperature;// f
			result[4] = jCondition.getString("code");

		} catch (JSONException err) {
			Log.e(TAG, "error: " + err.toString());
			result[0] = "Unknown";
			result[1] = "Unknown";
			result[2] = "Unknown";
		} catch (NullPointerException err) {
			Log.e(TAG, "error: " + err.toString());
			result[0] = "Unknown";
			result[1] = "Unknown";
			result[2] = "Unknown";
			
		}

		return result;
	}

	private void getWeather(String query) {

		InputStream inputStream = null;

		try {
			String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";

			String totalUrl = baseUrl + URLEncoder.encode(query, "UTF-8")
					+ "&format=json";

			if (Debug.on) {
				Log.v(TAG, "Total URL:" + totalUrl);
			}

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
			YQLresult = sb.toString();
			if (Debug.on) {
				Log.v(TAG, "YQL result: " + YQLresult);
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
	}

	private void getWOEID(String query) {
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";

		try {
			String totalUrl = baseUrl + URLEncoder.encode(query, "UTF-8")
					+ "&format=json";

			if (Debug.on) {
				Log.v(TAG, "Total URL:" + totalUrl);
			}

			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpPost httppost = new HttpPost(totalUrl);

			if (Debug.on) {
				Log.v(TAG, "URI:" + httppost.getURI());
			}

			httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;

			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				// json is UTF-8 by default
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				woeidResult = sb.toString();
				if (Debug.on) {
					Log.v(TAG, "WOEID result: " + woeidResult);
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

		} catch (UnsupportedEncodingException err) {
			Log.e(TAG, "error: " + err.toString());
		}

	}

	int weatherCode;

	private void weatherNameSet(String condition) {
		switch (weatherCode) {
		case 0:
			result[0] = "龍捲風";
			break;
		case 1:
			result[0] = "熱帶風暴";
			break;
		case 2:
			result[0] = "颱風/颶風";
			break;
		case 3:
			result[0] = "強烈暴風雨";
			break;
		case 4:
			result[0] = "暴風雨";
			break;
		case 5:
			result[0] = "混合雨雪";
			break;
		case 6:
			result[0] = "混合雨和雨夾雪";
			break;
		case 7:
			result[0] = "混合雪和雨夾雪";
			break;
		case 8:
			result[0] = "冰凍細雨";
			break;
		case 9:
			result[0] = "細雨";
			break;
		case 10:
			result[0] = "凍雨";
			break;
		case 11:
			result[0] = "小雨";
			break;
		case 12:
			result[0] = "大雨";
			break;
		case 13:
			result[0] = "飄雪";
			break;
		case 14:
			result[0] = "小雪";
			break;
		case 15:
			result[0] = "下雪";
			break;
		case 16:
			result[0] = "下雪";
			break;
		case 17:
			result[0] = "冰雹";
			break;
		case 18:
			result[0] = "霰";
			break;
		case 19:
			result[0] = "沙塵";
			break;
		case 20:
			result[0] = "霧";
			break;
		case 21:
			result[0] = "霾";
			break;
		case 22:
			result[0] = "烟";
			break;
		case 23:
			result[0] = "大風";
			break;
		case 24:
			result[0] = "有風";
			break;
		case 25:
			result[0] = "寒冷";
			break;
		case 26:
			result[0] = "多雲";
			break;
		case 27:
			result[0] = "晴時多雲（夜間）";
			break;
		case 28:
			result[0] = "晴時多雲（日）";
			break;
		case 29:
			result[0] = "晴時偶有雲（夜間）";
			break;
		case 30:
			result[0] = "晴時偶有雲（日）";
			break;
		case 31:
			result[0] = "晴（日）";
			break;
		case 32:
			result[0] = "陽光明媚";
			break;
		case 33:
			result[0] = "好天氣（夜間）";
			break;
		case 34:
			result[0] = "好天氣（日）";
			break;
		case 35:
			result[0] = "混合雨和冰雹";
			break;
		case 36:
			result[0] = "炎熱";
			break;
		case 37:
			result[0] = "地區性雷暴";
			break;
		case 38:
			result[0] = "零星雷雨";
			break;
		case 39:
			result[0] = "零星雷雨";
			break;
		case 40:
			result[0] = "零星陣雨";
			break;
		case 41:
			result[0] = "大雪";
			break;
		case 42:
			result[0] = "零星陣雪";
			break;
		case 43:
			result[0] = "大雪";
			break;
		case 44:
			result[0] = "偶時多雲";
			break;
		case 45:
			result[0] = "雷陣雨";
			break;
		case 46:
			result[0] = "陣雪";
			break;
		case 47:
			result[0] = "地區性雷陣雨";
			break;
		case 3200:
			result[0] = "無法使用";
			break;
		default:
			Log.e(TAG, "error: Don't have this weather condition!?");
		}
	}

}