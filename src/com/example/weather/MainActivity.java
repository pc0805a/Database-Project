package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

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

	@Override
	protected void onStart() {
		super.onStart();
		if (initLocationProvider()) {
			whereAmI();
		} else {
			Toast.makeText(this, "�ж}�ҩw��A��", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // �}�ҳ]�w����
		}

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

	// private void getInfo(String query) {
	// String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
	// String totalUrl = baseUrl + query;
	// try {
	// getJson(URLEncoder.encode(totalUrl, "UTF8"));
	// } catch (UnsupportedEncodingException err) {
	// // TODO Auto-generated catch block
	// Log.e(TAG, "error: " + err.toString());
	// }
	// }
	//
	// public String getJson(String url) {
	// String result = "";
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(url);
	// HttpResponse response;
	// try {
	// response = httpclient.execute(httppost);
	// HttpEntity entity = response.getEntity();
	// InputStream is = entity.getContent();
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// is, "utf8"), 9999999);
	// StringBuilder sb = new StringBuilder();
	// String line = null;
	//
	// while ((line = reader.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// is.close();
	// result = sb.toString();
	// } catch (ClientProtocolException err) {
	// Log.e(TAG, "error: " + err.toString());
	// } catch (IOException err) {
	// Log.e(TAG, "error: " + err.toString());
	// }
	//
	// return result;
	// }
	//
	private Button button_search;
	private TextView longitude_txt;
	private TextView latitude_txt;
	private double lng;
	private double lat;

	private void initViews() {
		button_search = (Button) findViewById(R.id.button_search);
		longitude_txt = (TextView) findViewById(R.id.lng);
		latitude_txt = (TextView) findViewById(R.id.lat);
	}

	private boolean initLocationProvider() {

		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		try {
			if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					|| locationMgr
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// 1.��̨ܳδ��Ѿ�
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setCostAllowed(true);
				criteria.setPowerRequirement(Criteria.POWER_LOW);

				provider = locationMgr.getBestProvider(criteria, true);
				// 2.��ܨϥ�GPS���Ѿ�
				// if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// provider = LocationManager.GPS_PROVIDER;
				// return true;
				// }

				// 3.��ܨϥκ������Ѿ�
				// if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				// {
				// provider = LocationManager.NETWORK_PROVIDER;
				// return true;
				// }
				

				Log.d(TAG, "My Provider:" + provider);

				return true;
			}

		} catch (Exception err) {
			Log.e(TAG, "error: " + err.toString());
		}

		return false;

	}

	private void setListeners() {
		button_search.setOnClickListener(search);
	}

	private void whereAmI() {
		// ���o�W���w������m
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
				// bundle.putDouble("KEY_WEIGHT",
				// Double.parseDouble(num_weight.getText().toString()));
				// intent.putExtras(bundle);

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
			// �g��
			lng = location.getLongitude();
			// �n��
			lat = location.getLatitude();
			// �t��
			float speed = location.getSpeed();
			// �ɶ�
			long time = location.getTime();
			String timeString = getTimeString(time);

			where = "�g��: " + lng + "\n�n��: " + lat + "\n�t��: " + speed + "\n�ɶ�: "
					+ timeString + "\nProvider: " + provider;

			// "��"
			longitude_txt.setText("�g��" + lng);
			latitude_txt.setText("�n��" + lat);

		} else {
			where = "�L�k���o�a�z��T"+
		"\n�Y�b�Ǥ��й��ըϥκ����w��";
		}

		Toast.makeText(this, where, Toast.LENGTH_LONG).show();

	}

	private String getTimeString(long timeInMilliseconds) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timeInMilliseconds);
	}
}
