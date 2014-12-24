package com.example.weather;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class SearchActivity extends Activity {
//	http://maps.googleapis.com/maps/api/geocode/json?address=
	private static final String TAG = SearchActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initViews();
		setListeners();

	}
	private TextView lastUpdate_txt;
	private TextView currentCondition_txt;
	private TextView humidity_txt;
	private TextView currentTemperature_txt;
	private TextView currentLocation_txt;
	private TextView reliability_txt;
	private void initViews() {
		lastUpdate_txt = (TextView) findViewById(R.id.last_update);
		currentCondition_txt = (TextView) findViewById(R.id.current_condition);
		humidity_txt = (TextView) findViewById(R.id.humidity);
		currentTemperature_txt = (TextView) findViewById(R.id.current_temperature);
		currentLocation_txt = (TextView) findViewById(R.id.current_location);
		reliability_txt = (TextView) findViewById(R.id.reliability_txt);

	}


	private void setListeners() {

	}


}

