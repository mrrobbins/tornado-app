package com.AlabamaDamageTracker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeScreen extends Activity {

	private static final String KEY_SERVER_ADDRESS = "server address";
	private static final String KEY_EMAIL = "email";
	private static final String TAG = "HomeScreen";
	
	private final HomeScreen self = this;
	
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getPreferences(MODE_PRIVATE);
	}
	
	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
		setContentView(R.layout.home_screen);
		final Button newDamage = (Button) findViewById(R.id.launch_capture);
		final Button viewDamage = (Button) findViewById(R.id.launch_reports);
		final Button uploadDamage = (Button) findViewById(R.id.launch_upload);
		final Button about = (Button) findViewById(R.id.launch_about_info);
		
		about.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder b = new AlertDialog.Builder(self);
				b.setTitle("Damage Tracker");
				b.setIcon(getResources().getDrawable(R.drawable.nsf));
				b.setMessage(
					"The Univerity of Alabama\n\n" +
					"Developers:\n" +
					"\tChris Hodapp\n" +
					"\tMatt Robbins\n" +
					"\tKyle Redding\n" +
					"\tLuke Taylor\n" +
					"\tJonathan Fikes\n\n" +
					"This project was supported in part by NSF grant #1047780"
				);
				b.show();
			}
		});

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
		
		uploadDamage.setOnClickListener (new View.OnClickListener() {
			
			public void onClick(View clicked) {
				
				DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
				
				List<Report> reports = new LinkedList<Report>();
				try {
					reports = dbh.getPendingReports();
				} finally {
					dbh.close();
				}
				
				final long[] ids = new long[reports.size()];
				
				{
					int idx = 0;
					Iterator<Report> iter = reports.iterator();
					while (iter.hasNext()) {
						Report r = iter.next();
						ids[idx] = r.id;
						idx++;
					}
				}
				

				AlertDialog.Builder b = new AlertDialog.Builder(self);
				b.setTitle("Enter Login Details");
				LayoutInflater inflater = getLayoutInflater();

				final View v = inflater.inflate(R.layout.user_login_screen, null);
				((EditText) v.findViewById(R.id.server_address)).setText(prefs.getString(KEY_SERVER_ADDRESS, ""));
				((EditText) v.findViewById(R.id.user_email)).setText(prefs.getString(KEY_EMAIL, ""));
				b.setView(v);

				b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { }
				});

				b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String server = ((EditText) v.findViewById(R.id.server_address)).getText().toString();
						String email = ((EditText) v.findViewById(R.id.user_email)).getText().toString();
						String password = ((EditText) v.findViewById(R.id.user_password)).getText().toString();
						
						Editor e = prefs.edit();
						try {
							e.putString(KEY_SERVER_ADDRESS, server);
							e.putString(KEY_EMAIL, email);
						} finally {
							e.commit();
						}
						
						
						Intent serviceIntent = new Intent(self, UploadService.class);
						serviceIntent.setAction(UploadService.ACTION_UPLOAD_IMAGES);
						serviceIntent.putExtra(UploadService.KEY_SERVER_ADDRESS, server);
						serviceIntent.putExtra(UploadService.KEY_EMAIL, email);
						serviceIntent.putExtra(UploadService.KEY_PASSWORD, password);
						serviceIntent.putExtra(UploadService.KEY_REPORT_IDS, ids);
						startService(serviceIntent);
					}
				});


				b.show();

			}
		});
	}
}