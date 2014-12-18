package com.example.weather;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final int ACTIVITY_REPORT = 1000;

	private static final String TAG = MainActivity.class.getSimpleName();

	private LocationManager locationMgr;
	private String provider;
	private double lng;
	private double lat;

	String[] YQLresult;
	String YQLquery;
	JSONObject Gresult;
	String Gquery;

	@Override
	protected void onStart() {
		super.onStart();
		if (initLocationProvider()) {
			whereAmI();
		} else {
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 開啟設定頁面
		}



		handleWeatherInfo();

	}

	@Override
	protected void onStop() {
		locationMgr.removeUpdates(locationListener);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		setListeners();

	}

	private void handleWeatherInfo() {
		try {
			YQLresult = new GetWeatherInfo(lng,lat).execute().get();
			currentCondition_txt.setText(YQLresult[0]);
			humidity_txt.setText(YQLresult[1]+"%");
			DecimalFormat temp = new DecimalFormat( "#.0");
			currentTemperature_txt.setText(temp.format((Double.parseDouble(YQLresult[2])-32)*5/9));
		} catch (InterruptedException err) {
			Log.e(TAG, "error: " + err.toString());
		} catch (ExecutionException err) {
			Log.e(TAG, "error: " + err.toString());
		}

		

	}
//	
//	private void handleGeoName() {
//		try {
//			Gresult = new GetWeatherInfo(Gquery).execute().get();
//		} catch (InterruptedException err) {
//			// TODO Auto-generated catch block
//			Log.e(TAG, "error: " + err.toString());
//		} catch (ExecutionException err) {
//			// TODO Auto-generated catch block
//			Log.e(TAG, "error: " + err.toString());
//		}
//		
//	}
	

	private Button button_search;
	private TextView longitude_txt;
	private TextView latitude_txt;
	private TextView lastUpdate_txt;
	private TextView currentCondition_txt;
	private TextView humidity_txt;
	private TextView currentTemperature_txt;

	private void initViews() {
		button_search = (Button) findViewById(R.id.button_search);
		longitude_txt = (TextView) findViewById(R.id.lng);
		latitude_txt = (TextView) findViewById(R.id.lat);
		lastUpdate_txt = (TextView) findViewById(R.id.last_update);
		currentCondition_txt = (TextView) findViewById(R.id.current_condition);
		humidity_txt = (TextView) findViewById(R.id.humidity);
		currentTemperature_txt = (TextView) findViewById(R.id.current_temperature);
	}

	private boolean initLocationProvider() {

		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		try {
			if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					|| locationMgr
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// 選擇最佳提供器
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setCostAllowed(true);
				criteria.setPowerRequirement(Criteria.POWER_LOW);

				provider = locationMgr.getBestProvider(criteria, true);
				if(Debug.on)
				Log.d(TAG, "My Provider:" + provider);

				return true;
			}

		} catch (Exception err) {
			if (Debug.on) {
				Log.e(TAG, "error: " + err.toString());
			}
		}

		return false;

	}

	private void setListeners() {
		button_search.setOnClickListener(search);
	}

	private void whereAmI() {
		// 取得上次已知的位置
		Location location = locationMgr.getLastKnownLocation(provider);
		updateWithNewLocation(location);

		// GPS Listener
		locationMgr.addGpsStatusListener(gpsListener);

		// Location Listener
		int minTime = 1000;// ms
		int minDist = 5;// meter
		locationMgr.requestLocationUpdates(provider, minTime, minDist,
				locationListener);
	}

	private OnClickListener search = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent();

			intent.setClass(MainActivity.this, SearchActivity.class);
			Bundle bundle = new Bundle();
			try {
				// bundle.putDouble("KEY_HEIGHT",
				// Double.parseDouble(num_height.getText().toString()));

				startActivityForResult(intent, ACTIVITY_REPORT);
			} catch (Exception err) {
				if (Debug.on) {
					Log.e(TAG, "error: " + err.toString());
				}
				Toast.makeText(MainActivity.this, R.string.input_error,
						Toast.LENGTH_SHORT).show();
			}

		}

	};

	GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				Log.d(TAG, "GPS_EVENT_STARTED");
				Toast.makeText(MainActivity.this, "GPS_EVENT_STARTED",
						Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.d(TAG, "GPS_EVENT_STOPPED");
				Toast.makeText(MainActivity.this, "GPS_EVENT_STOPPED",
						Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.d(TAG, "GPS_EVENT_FIRST_FIX");
				Toast.makeText(MainActivity.this, "GPS_EVENT_FIRST_FIX",
						Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
				break;
			}
		}
	};

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
			handleWeatherInfo();
//			handleGeoName();
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				Log.v(TAG, "Status Changed: Out of Service");
				Toast.makeText(MainActivity.this,
						"Status Changed: Out of Service", Toast.LENGTH_SHORT)
						.show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.v(TAG, "Status Changed: Temporarily Unavailable");
				Toast.makeText(MainActivity.this,
						"Status Changed: Temporarily Unavailable",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:
				Log.v(TAG, "Status Changed: Available");
				Toast.makeText(MainActivity.this, "Status Changed: Available",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_about:
			openOptionsDialog();
			break;
		case R.id.action_close:
			finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void get_current_local_info() {

	}

	public void openOptionsDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle(R.string.about_title);
		dialog.setMessage(R.string.about_msg);

		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {

					}
				});
		dialog.setNegativeButton(R.string.label_fb,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int i) {
						Uri uri = Uri.parse(getString(R.string.fb_uri));
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}
				});

		dialog.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void updateWithNewLocation(Location location) {
		String where = "";
		if (location != null) {
			// 經度
			lng = location.getLongitude();
			// 緯度
			lat = location.getLatitude();
			Gquery = lat+","+lng;
			if (Debug.on) {
				Log.v(TAG, "Latitude: " + lat+ "\nLongitude: "+ lng);
			}
			// 速度
			float speed = location.getSpeed();
			// 時間
			long time = location.getTime();
			String timeString = getTimeString(time);

			where = "經度: " + lng + "\n緯度: " + lat + "\n速度: " + speed + "\n時間: "
					+ timeString + "\nProvider: " + provider;

			// "我"
			longitude_txt.setText("經度: " + lng);
			latitude_txt.setText("緯度: " + lat);
			lastUpdate_txt.setText(timeString);

		} else {
			where = "無法取得地理資訊" + "\n若在室內請嘗試使用網路定位";
		}

		Toast.makeText(this, where, Toast.LENGTH_LONG).show();

	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeString(long timeInMilliseconds) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timeInMilliseconds);
	}
}