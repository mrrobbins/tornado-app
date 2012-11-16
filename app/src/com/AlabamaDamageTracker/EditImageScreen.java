package com.AlabamaDamageTracker;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class EditImageScreen extends Activity {
	
	public static final String KEY_REPORT_ID = "report id";
	public static final String KEY_IMAGE_PATH = "image path";
	public static final String KEY_IMAGE_TIME = "image time";
	public static final String KEY_IMAGE_LATITUDE = "image lat";
	public static final String KEY_IMAGE_LONGITUDE = "image long";
	
	private static final String TAG = "EditImageScreen";
	
	private final EditImageScreen self = this;
	
	private String imagePath;
	private long time;
	private double latitude;
	private double longitude;
	
	private Long dbId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_notes_screen);
		
		Intent intent = getIntent();
		if (intent.hasExtra(KEY_REPORT_ID)) {
			 // The report is in the database
			dbId = intent.getLongExtra(KEY_REPORT_ID, -1);
			loadFromDatabase(dbId);
		} else {
			// The report is not in the database
			((Button) findViewById(R.id.info_delete_btn)).setText("Cancel");
			loadFromBundle(intent);
		}
	}
	
	
	public void onDestroy() {
		ImageView i = (ImageView) findViewById(R.id.info_image);
		Drawable d = i.getDrawable();
		if (d instanceof BitmapDrawable) {
			BitmapDrawable bmd = (BitmapDrawable) d;
			Bitmap b = bmd.getBitmap();
			b.recycle();
		}
		super.onDestroy();
	}
	
	public void onSaveBtn(View v) {
		saveToDatabase();
	}
	public void onDeleteBtn(View v) {
		if (dbId != null) {
			DatabaseHelper dbh = DatabaseHelper.openReadWrite(self);
			try {
				dbh.deleteReport(dbId);
			} finally {
				dbh.close();
			}
		}
		finish();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

	}
	
	public void saveToDatabase() {
		DatabaseHelper dbh = null;
		try {
			dbh = DatabaseHelper.openReadWrite(self);
			Report r;
			if (dbId != null) {
				r = dbh.getReport(dbId);
				r.picturePath = imagePath;
				r.address = getAddress();
				r.notes = getNotes();
				dbh.updateReport(r);
			} else {
				r = new Report();
				r.picturePath = imagePath;
				r.address = getAddress();
				r.notes = getNotes();
				r.latitude = getLatitude();
				r.longitude = getLongitude();
				r.time = getTime();
				dbId = dbh.insertReport(r);
			}
		} finally {
			if (dbh != null) dbh.close();
		}
		finish();
	}
	
	private void loadFromDatabase(long reportId) {
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
		try {
			Report source = dbh.getReport(reportId);
			setImage(source.picturePath);
			setTime(source.time);
			setLatitude(source.latitude);
			setLongitude(source.longitude);
			
			setAddress(source.address);
			setNotes(source.notes);
			setDamageIndicator(source.damageIndicator);
			setDamageDegree(source.degreeOfDamage);
		} finally {
			dbh.close();
		}
	}
	
	public void loadFromBundle(Intent intent) {
		setImage(intent.getStringExtra(KEY_IMAGE_PATH));
		setTime(intent.getLongExtra(KEY_IMAGE_TIME, 0));
		setLatitude(intent.getDoubleExtra(KEY_IMAGE_LATITUDE, 0));
		setLongitude(intent.getDoubleExtra(KEY_IMAGE_LONGITUDE, 0));
	}
	
	private void setTime(long time) {
		this.time = time;
		String timeStamp;
		if (time == 0) timeStamp = "";
		else timeStamp = new SimpleDateFormat("E M/d/y H:m:s").format(new Date(time));
		
		((TextView) findViewById(R.id.info_time)).setText(timeStamp);
	}
	private long getTime() {
		return time;
	}
	
	private void setLatitude(double latitude) {
		((TextView) findViewById(R.id.info_latitude)).setText(String.format("Latitude: %f", latitude));
	}
	private double getLatitude() {
		return latitude;
	}
	
	private void setLongitude(double longitude) {
		((TextView) findViewById(R.id.info_longitude)).setText(String.format("Longitude: %f", longitude));
	}
	private double getLongitude() {
		return longitude;
	}
	
	private String getAddress() {
		return ((EditText) findViewById(R.id.info_address)).getText().toString();
	}
	private void setAddress(String newAddress) {
		((EditText) findViewById(R.id.info_address)).setText(newAddress);
	}
	
	private String getNotes() {
		return ((EditText) findViewById(R.id.info_notes)).getText().toString();
	}
	private void setNotes(String newNotes) {
		((EditText) findViewById(R.id.info_notes)).setText(newNotes);
	}
	
	private int getDamageIndicator() {
		Spinner indicators = (Spinner) findViewById(R.id.info_indicator);
		return (int) indicators.getSelectedItemId();
	}
	private void setDamageIndicator(int indicator) {
		Spinner indicators = (Spinner) findViewById(R.id.info_indicator);
	}
	
	private int getDamageDegree() {
		Spinner indicators = (Spinner) findViewById(R.id.info_degree);
		return (int) indicators.getSelectedItemId();
	}
	private void setDamageDegree(int degree) {
		Spinner indicators = (Spinner) findViewById(R.id.info_degree);
	}
	
	private void setImage(String path) {
		imagePath = path;
		ImageView i = (ImageView) findViewById(R.id.info_image);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (bitmap == null) Log.d(TAG, "bitmap is null");
		i.setImageBitmap(bitmap);
	}
}