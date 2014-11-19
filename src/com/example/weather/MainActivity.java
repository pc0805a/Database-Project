package com.example.weather;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final int ACTIVITY_REPORT = 1000;
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
		setListeners();

	}

	private Button button_search;
	


	private void initViews() {
		button_search = (Button) findViewById(R.id.button_search);
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
//				bundle.putDouble("KEY_HEIGHT",
//						Double.parseDouble(num_height.getText().toString()));
//				bundle.putDouble("KEY_WEIGHT",
//						Double.parseDouble(num_weight.getText().toString()));
//				intent.putExtras(bundle);

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


