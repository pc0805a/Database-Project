package com.example.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener

{

	@Override
	public void onLocationChanged(Location location) {
		location.getLatitude();

		location.getLongitude();

		String Text = "My current location is: " + "Latitud ="
				+ location.getLatitude() + "Longitud ="
				+ location.getLongitude();
		Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
				.show();

	}

	private Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Enabled",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Disabled",
				Toast.LENGTH_SHORT).show();

	}

}