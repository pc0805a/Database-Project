package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import android.location.Location;
import android.location.LocationManager;
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
	private boolean getService = false;
	private LocationManager lms;
	private String bestProvider = LocationManager.GPS_PROVIDER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LocationManager status = (LocationManager) (this
				.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// �p�GGPS�κ����w��}�ҡA�I�slocationServiceInitial()��s��m
			locationServiceInitial();
		} else {
			Toast.makeText(this, "�ж}�ҩw��A��", Toast.LENGTH_LONG).show();
			getService = true; // �T�{�}�ҩw��A��
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		initViews();

		setListeners();

	}

	private void locationServiceInitial() {
		lms = (LocationManager) getSystemService(LOCATION_SERVICE);	
		Criteria criteria = new Criteria();	
		bestProvider = lms.getBestProvider(criteria, true);	
		Location location = lms.getLastKnownLocation(bestProvider);
		getLocation(location);

		getLocation(location);
	}
	
	private void getLocation(Location location) { //�N�w���T��ܦb�e����
        if(location != null) {
             Double longitude = location.getLongitude();   //���o�g��
             Double latitude = location.getLatitude();     //���o�n��
             longitude_txt.setText(String.valueOf(longitude));
             latitude_txt.setText(String.valueOf(latitude));
        }
        else {
             Toast.makeText(this, "�L�k�w��y��", Toast.LENGTH_LONG).show();
        }
    }

	private void getInfo(String query) {
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
		String totalUrl = baseUrl + query;
		try {
			getJson(URLEncoder.encode(totalUrl, "UTF8"));
		} catch (UnsupportedEncodingException err) {
			// TODO Auto-generated catch block
			Log.e(TAG, "error: " + err.toString());
		}
	}

	public String getJson(String url) {
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf8"), 9999999);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (ClientProtocolException err) {
			Log.e(TAG, "error: " + err.toString());
		} catch (IOException err) {
			Log.e(TAG, "error: " + err.toString());
		}

		return result;
	}

	private Button button_search;
	private TextView longitude_txt;
	private TextView latitude_txt;

	private void initViews() {
		button_search = (Button) findViewById(R.id.button_search);
		longitude_txt = (TextView) findViewById(R.id.lon);
		latitude_txt = (TextView) findViewById(R.id.lat);
	}

	// listen for button click
	private void setListeners() {
		button_search.setOnClickListener(search);
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

}
