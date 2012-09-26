package com.AlabamaDamageTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeScreen extends Activity {

	private static final String TAG = "HomeScreen";
	
	private final HomeScreen self = this;
	private LocationManager locationManager;
	private LocationListener locationListener;
	protected final HomeScreen context = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}

	public void onResume() {
		super.onResume();
		setContentView(R.layout.home_screen);

		// Get access to the system location services. 
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  

		// Set up a location listener for the device.
		locationListener = new MyLocationListener();

		// Receive GPS location updates for the device.
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);



		final Button newDamage = (Button) findViewById(R.id.home_new_location);
		final Button reportedDamage = (Button) findViewById(R.id.home_reported_damage);

		newDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(self, TakePictureScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
			}
		});

		reportedDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(self, ReportListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
			}
		});


	}

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
		}


		public void onProviderDisabled(String provider) {

			Toast.makeText( getApplicationContext(),

					"Gps Disabled",

					Toast.LENGTH_SHORT ).show();

		}


		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(),
					"Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}


		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	}


}