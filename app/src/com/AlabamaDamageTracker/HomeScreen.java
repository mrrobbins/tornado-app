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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
		setContentView(R.layout.home_screen);

		final Button newDamage = (Button) findViewById(R.id.home_new_location);
		final Button viewDamage = (Button) findViewById(R.id.home_reported_damage);

		newDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(self, TakePictureScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
			}
		});

		viewDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(self, ReportListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
			}
		});
	}
}