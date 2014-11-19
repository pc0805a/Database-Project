package com.example.weather;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class SearchActivity extends Activity {

	private static final String TAG = SearchActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		WebView web = (WebView) findViewById(R.id.webview);
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new InsideWebViewClient());
		web.loadUrl("https://weather.yahoo.com/");
		
		initViews();
		showResults();
		setListeners();

	}

	private void initViews() {

	}

	protected void showResults() {
	}

	protected void showNotification(double BMI) {

	}

	private void setListeners() {

	}

	private Button.OnClickListener backtoMain = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}

	};

}

class InsideWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
