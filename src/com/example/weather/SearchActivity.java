package com.example.weather;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends Activity {
	// http://maps.googleapis.com/maps/api/geocode/json?address=
	private static final String TAG = SearchActivity.class.getSimpleName();
	String[] YQLresult;
	String geoName;
	String[] geoResult;
	String lng;
	String lat;
	int weatherCode = -1;
	Handler refreshHandler = new Handler();
	int refreshDelayTime = 1 * 60 * 60 * 1000;// 1hr
	boolean firstTime = true;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bundle bundle = this.getIntent().getExtras();
		lng = bundle.getString("KEY_LNG");
		lat = bundle.getString("KEY_LAT");

		currentLocation_txt.setText(bundle.getString("KEY_GEO_NAME"));
		longitude_txt.setText("經度: " + lng);
		latitude_txt.setText("緯度: " + lat);
		currentCondition_txt.setText(bundle.getString("KEY_CONDI"));
		humidity_txt.setText(bundle.getString("KEY_HUMID") + "%");

		DecimalFormat temp = new DecimalFormat("#.0");

		currentTemperature_txt.setText(temp.format((Double.parseDouble(bundle
				.getString("KEY_TEMP")) - 32) * 5 / 9));

		weatherCode = bundle.getInt("KEY_CODE");
		
		reliability_txt.setText(bundle.getString("KEY_REL")+"%");
		lastUpdate_txt.setText(bundle.getString("KEY_LAST"));
		woeid_txt.setText("WOEID: "+bundle.getString("KEY_WOEID"));
		setBackground();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initViews();
		setListeners();

	}

	private void setListeners() {
		// TODO Auto-generated method stub

	}

	private TextView lastUpdate_txt;
	private TextView currentCondition_txt;
	private TextView humidity_txt;
	private TextView currentTemperature_txt;
	private TextView currentLocation_txt;
	private TextView reliability_txt;
	private TextView longitude_txt;
	private TextView latitude_txt;
	private TextView woeid_txt;

	private void initViews() {
		lastUpdate_txt = (TextView) findViewById(R.id.last_update);
		currentCondition_txt = (TextView) findViewById(R.id.current_condition);
		humidity_txt = (TextView) findViewById(R.id.humidity);
		currentTemperature_txt = (TextView) findViewById(R.id.current_temperature);
		currentLocation_txt = (TextView) findViewById(R.id.current_location);
		reliability_txt = (TextView) findViewById(R.id.reliability_txt);
		longitude_txt = (TextView) findViewById(R.id.lng);
		latitude_txt = (TextView) findViewById(R.id.lat);
		woeid_txt = (TextView) findViewById(R.id.woeid);

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
