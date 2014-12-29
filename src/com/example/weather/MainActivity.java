package com.example.weather;

import java.text.DecimalFormat;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

public class MainActivity extends Activity {

	protected static final int ACTIVITY_REPORT = 1000;

	private static final String TAG = MainActivity.class.getSimpleName();

	private LocationManager locationMgr;
	private String provider;
	private double lng = -99999;
	private double lat = -99999;

	String[] YQLresult;
	String YQLquery;
	String[] geoResult;
	String[] DBresult;

	String woeid = "-1";
	String reliableCount = "0";
	String unreliableCount = "0";
	String totalReliableCount = "0";
	String totalUnreliableCount = "0";
	String lastUpdate;
	int weatherCode;

	Handler networkConnectionHandler = new Handler();
	int checkNetworkDelayTime = 10000;
	Handler refreshHandler = new Handler();
	int refreshDelayTime = 1 * 60 * 60 * 1000;// 1hr

	@Override
	protected void onStart() {
		super.onStart();
		if (initLocationProvider()) {

			getHistory();

			networkConnectionHandler.postDelayed(checkNetworkConnection,
					checkNetworkDelayTime);
			whereAmI();
		} else {
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 開啟設定頁面
		}

	}

	@Override
	protected void onPause() {
		networkConnectionHandler.removeCallbacks(checkNetworkConnection);
		refreshHandler.removeCallbacks(refresh);
		super.onPause();
	}

	@Override
	protected void onStop() {
		locationMgr.removeUpdates(locationListener);
		networkConnectionHandler.removeCallbacks(checkNetworkConnection);
		refreshHandler.removeCallbacks(refresh);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		setListeners();
		setAdapter();

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	private DB mDBHelper;
	private Cursor mCursor;

	private String reliability = "0";

	private void setAdapter() {
		mDBHelper = new DB(this);
		mDBHelper.open();

	}

	private void handleWeatherInfo() {
		try {
			YQLresult = new GetWeatherInfo(lng, lat).execute().get();
			currentCondition_txt.setText(YQLresult[0]);
			humidity_txt.setText(YQLresult[1] + "%");
			DecimalFormat temp = new DecimalFormat("#.0");
			currentTemperature_txt.setText(temp.format((Double
					.parseDouble(YQLresult[2]) - 32) * 5 / 9));

			weatherCode = Integer.parseInt(YQLresult[4]);
			if (Debug.on) {
				Log.v(TAG, "Weather Code: " + weatherCode);
			}
		} catch (InterruptedException err) {
			Log.e(TAG, "error: " + err.toString());
		} catch (ExecutionException err) {
			Log.e(TAG, "error: " + err.toString());
		}

	}

	private void handleGeoName() {
		try {
			geoResult = new GetGeoInfo(lng, lat).execute().get();
			// if (Debug.on) {
			woeid_txt.setText("WOEID: " + YQLresult[3]);
			// }

			if (Debug.on) {
				Log.v(TAG, "Geo Name: " + geoResult[0]);
				Log.v(TAG, "Lng: " + geoResult[1]);
				Log.v(TAG, "Lat: " + geoResult[2]);
			}

			currentLocation_txt.setText(geoResult[0]);
		} catch (InterruptedException err) {
			Log.e(TAG, "error: " + err.toString());
		} catch (ExecutionException err) {
			Log.e(TAG, "error: " + err.toString());
		}

	}

	private void handleDbInfo() {
		try {
			DBresult = new GetDbInfo(YQLresult[3]).execute().get();

			woeid = DBresult[0];
			reliableCount = DBresult[1];
			unreliableCount = DBresult[2];
			totalReliableCount = DBresult[3];
			totalUnreliableCount = DBresult[4];
			lastUpdate = DBresult[5] + " " + DBresult[6];

			if (Debug.on) {
				Log.v(TAG, "WOEID: " + woeid);
				Log.v(TAG, "Reliable Count: " + reliableCount);
				Log.v(TAG, "Unreliable Count: " + unreliableCount);
				Log.v(TAG, "Total Reliable Count: " + totalReliableCount);
				Log.v(TAG, "Total Unreliable Count: " + totalUnreliableCount);
				Log.v(TAG, "Last Update: " + lastUpdate);
			}

			try {

				double tempTotalRe;
				if ((Double.parseDouble(totalReliableCount) + Double
						.parseDouble(totalUnreliableCount)) == 0) {
					tempTotalRe = 0;
				} else {
					DecimalFormat temp = new DecimalFormat("#0.0");
					tempTotalRe = Double.parseDouble(totalReliableCount)
							/ (Double.parseDouble(totalReliableCount) + Double
									.parseDouble(totalUnreliableCount)) * 100;
					temp.format(tempTotalRe);

				}

				if (Debug.on) {
					Log.v(TAG, "Reliability:" + tempTotalRe + "%");
				}
				DecimalFormat temp = new DecimalFormat("#0.0");

				reliability = temp.format(tempTotalRe);

				reliability_txt.setText(reliability + "%");
				lastUpdate_txt.setText(lastUpdate);
			} catch (Exception err) {
				Log.e(TAG, "error: " + err.toString());
			}

		} catch (InterruptedException err) {
			Log.e(TAG, "error: " + err.toString());
		} catch (ExecutionException err) {
			Log.e(TAG, "error: " + err.toString());
		}

	}

	private Button search_btn;
	private Button reliable_btn;
	private Button unreliable_btn;
	private TextView longitude_txt;
	private TextView latitude_txt;
	private TextView lastUpdate_txt;
	private TextView currentCondition_txt;
	private TextView humidity_txt;
	private TextView currentTemperature_txt;
	private TextView currentLocation_txt;
	private TextView woeid_txt;
	private TextView reliability_txt;
	private TextView searchLocation_txt;

	private void initViews() {
		search_btn = (Button) findViewById(R.id.search_btn);
		reliable_btn = (Button) findViewById(R.id.reliable_btn);
		unreliable_btn = (Button) findViewById(R.id.unreliable_btn);
		longitude_txt = (TextView) findViewById(R.id.lng);
		latitude_txt = (TextView) findViewById(R.id.lat);
		lastUpdate_txt = (TextView) findViewById(R.id.last_update);
		currentCondition_txt = (TextView) findViewById(R.id.current_condition);
		humidity_txt = (TextView) findViewById(R.id.humidity);
		currentTemperature_txt = (TextView) findViewById(R.id.current_temperature);
		currentLocation_txt = (TextView) findViewById(R.id.current_location);
		woeid_txt = (TextView) findViewById(R.id.woeid);
		reliability_txt = (TextView) findViewById(R.id.reliability_txt);
		searchLocation_txt = (TextView) findViewById(R.id.search_location_txt);
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
				if (Debug.on)
					Log.d(TAG, "My Provider:" + provider);

				return true;
			}

		} catch (Exception err) {

			Log.e(TAG, "error: " + err.toString());

		}

		return false;

	}

	private void setListeners() {
		search_btn.setOnClickListener(search);
		reliable_btn.setOnClickListener(reliable);
		unreliable_btn.setOnClickListener(unreliable);
	}

	Runnable checkNetworkConnection = new Runnable() {
		@Override
		public void run() {
			if (!isOnline()) {
				Toast.makeText(MainActivity.this, "請開啟網路以獲得最新天氣資訊",
						Toast.LENGTH_LONG).show();
			} else {
				insertHistory();
				refreshHandler.postDelayed(refresh, refreshDelayTime);
			}
			networkConnectionHandler.postDelayed(this, checkNetworkDelayTime);

		}
	};

	Runnable refresh = new Runnable() {
		@Override
		public void run() {
			if (isOnline()) {
				handleWeatherInfo();
				setBackground();
				handleGeoName();
				handleDbInfo();

				refreshHandler.postDelayed(this, refreshDelayTime);
			} else {
				refreshHandler.removeCallbacks(refresh);
			}
		}

	};

	private void insertHistory() {
		String[] insertData = new String[7];

		insertData[0] = woeid;
		insertData[1] = currentLocation_txt.getText().toString();
		insertData[2] = currentCondition_txt.getText().toString();
		insertData[3] = humidity_txt.getText().toString();
		insertData[4] = currentTemperature_txt.getText().toString();
		insertData[5] = reliability;
		insertData[6] = lastUpdate_txt.getText().toString();

		mDBHelper.delete(1);
		mDBHelper.insert(insertData);

	}

	private void getHistory() {
		Cursor cursor = mDBHelper.getAll();
		int row_num = cursor.getCount();
		if (row_num != 0) {
			cursor.moveToFirst();
			currentLocation_txt.setText(cursor.getString(2));
			currentCondition_txt.setText(cursor.getString(3));
			humidity_txt.setText(cursor.getString(4));
			currentTemperature_txt.setText(cursor.getString(5));
			reliability_txt.setText(cursor.getDouble(6) + "%");
			lastUpdate_txt.setText(cursor.getString(7));
		}
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

			if (isOnline()) {
				Intent intent = new Intent();

				intent.setClass(MainActivity.this, SearchActivity.class);
				Bundle bundle = new Bundle();
				try {

					String searchInput = searchLocation_txt.getText()
							.toString().replaceAll("\\s", "");

					String[] tempGeo = new GetGeoInfo(searchInput).execute()
							.get();

					bundle.putString("KEY_GEO_NAME", tempGeo[0]);
					bundle.putString("KEY_LNG", tempGeo[1]);
					bundle.putString("KEY_LAT", tempGeo[2]);

					String[] tempWea = new GetWeatherInfo(tempGeo[1],
							tempGeo[2]).execute().get();

					bundle.putString("KEY_CONDI", tempWea[0]);
					bundle.putString("KEY_HUMID", tempWea[1]);
					bundle.putString("KEY_TEMP", tempWea[2]);
					bundle.putString("KEY_WOEID", tempWea[3]);
					bundle.putInt("KEY_CODE", Integer.parseInt(tempWea[4]));
					if (Debug.on) {
						Log.v(TAG, "Input: " + searchInput);
					}
					intent.putExtras(bundle);
					startActivityForResult(intent, ACTIVITY_REPORT);
				} catch (Exception err) {
					Log.e(TAG, "error: " + err.toString());
				}

			} else {
				Toast.makeText(MainActivity.this, "請開啟網路以使用搜尋功能",
						Toast.LENGTH_SHORT).show();
			}

		}

	};

	private OnClickListener reliable = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (woeid != "-1" && DBresult != null) {
				reliable_btn.setEnabled(false);
				unreliable_btn.setEnabled(false);
				reliableCount = Integer.toString(Integer
						.parseInt(reliableCount) + 1);
				totalReliableCount = Integer.toString(Integer
						.parseInt(totalReliableCount) + 1);
				new ReliabilityButtonsAction(woeid, reliableCount,
						unreliableCount, totalReliableCount,
						totalUnreliableCount, lastUpdate).execute();
				handleDbInfo();
				Toast.makeText(MainActivity.this, "感謝您提供資訊", Toast.LENGTH_SHORT)
						.show();

			} else {
				Toast.makeText(MainActivity.this, "尚未取得地區資訊",
						Toast.LENGTH_SHORT).show();
			}

		}

	};

	private OnClickListener unreliable = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (woeid != "-1" && DBresult != null) {
				reliable_btn.setEnabled(false);
				unreliable_btn.setEnabled(false);
				unreliableCount = Integer.toString(Integer
						.parseInt(unreliableCount) + 1);
				totalUnreliableCount = Integer.toString(Integer
						.parseInt(totalUnreliableCount) + 1);
				new ReliabilityButtonsAction(woeid, reliableCount,
						unreliableCount, totalReliableCount,
						totalUnreliableCount, lastUpdate).execute();

				handleDbInfo();
				Toast.makeText(MainActivity.this, "感謝您提供資訊", Toast.LENGTH_SHORT)
						.show();

			} else {
				Toast.makeText(MainActivity.this, "尚未取得地區資訊",
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
				Toast.makeText(MainActivity.this, "若長時間無法取得地理資訊, 請嘗試使用網路定位",
						Toast.LENGTH_LONG).show();
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
			if (isOnline()) {
				try {
					handleWeatherInfo();
					setBackground();
					handleGeoName();
					handleDbInfo();

				} catch (Exception err) {
					Log.e(TAG, "error: " + err.toString());
					if (!isOnline()) {
						Toast.makeText(MainActivity.this, "請開啟網路以獲得最新天氣資訊",
								Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Toast.makeText(MainActivity.this, "請開啟網路以獲得最新天氣資訊",
						Toast.LENGTH_LONG).show();
			}
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		super.onResume();
	}

	private void updateWithNewLocation(Location location) {
		String where = "";
		if (location != null) {
			lng = location.getLongitude();
			lat = location.getLatitude();

			if (Debug.on) {
				Log.v(TAG, "Latitude: " + lat + "\nLongitude: " + lng);
			}

			float speed = location.getSpeed();
			long time = location.getTime();
			String timeString = getTimeString(time);

			where = "經度: " + lng + "\n緯度: " + lat + "\n速度: " + speed + "\n時間: "
					+ timeString + "\nProvider: " + provider;

			// "我"
			longitude_txt.setText("經度: " + lng);
			latitude_txt.setText("緯度: " + lat);

		} else {
			where = "無法取得地理資訊" + "\n若在室內請嘗試使用網路定位";
		}
		if (Debug.on) {
			Toast.makeText(this, where, Toast.LENGTH_LONG).show();
		}

	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeString(long timeInMilliseconds) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timeInMilliseconds);
	}

	private void setBackground() {

		LinearLayout MainLinearLayout = (LinearLayout) findViewById(R.id.MainLinearLayout);
		Drawable background = background = getResources().getDrawable(
				R.drawable.unknown);
		;
		switch (weatherCode) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 3:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 4:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			background = getResources().getDrawable(R.drawable.rain_d);
			break;
		case 10:
			break;
		case 11:
			background = getResources().getDrawable(R.drawable.rain_d);
			break;
		case 12:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 13:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 14:
			break;
		case 15:
			break;
		case 16:
			break;
		case 17:
			break;
		case 18:
			break;
		case 19:
			break;
		case 20:
			break;
		case 21:
			break;
		case 22:
			break;
		case 23:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 24:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 25:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 26:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 27:
			background = getResources().getDrawable(R.drawable.cloud_n);
			break;
		case 28:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 29:
			background = getResources().getDrawable(R.drawable.cloud_n);
			break;
		case 30:
			background = getResources().getDrawable(R.drawable.cloud_d);
			break;
		case 31:
			background = getResources().getDrawable(R.drawable.clear_d);
			break;
		case 32:
			background = getResources().getDrawable(R.drawable.clear_d);
			break;
		case 33:
			background = getResources().getDrawable(R.drawable.clear_n);
			break;
		case 34:
			background = getResources().getDrawable(R.drawable.clear_d);
			break;
		case 35:
			break;
		case 36:
			background = getResources().getDrawable(R.drawable.clear_d);
			break;
		case 37:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 38:
			break;
		case 39:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 40:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 41:
			break;
		case 42:
			background = getResources().getDrawable(R.drawable.rain_d);
			break;
		case 43:
			break;
		case 44:
			background = getResources().getDrawable(R.drawable.clear_d);
			break;
		case 45:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 46:
			background = getResources().getDrawable(R.drawable.rain_d);
			break;
		case 47:
			background = getResources().getDrawable(R.drawable.rain_n);
			break;
		case 3200:
			break;
		default:
			background = getResources().getDrawable(R.drawable.unknown);
			Log.e(TAG, "error: Don't have this weather condition!?");
		}

		MainLinearLayout.setBackground(background);
	}

}