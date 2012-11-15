package com.AlabamaDamageTracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


public class EditImageScreen extends Activity {
	
	public static final String KEY_REPORT_ID = "report id";
	public static final String KEY_IMAGE_PATH = "image path";
	public static final String KEY_IMAGE_TIME = "image time";
	public static final String KEY_IMAGE_LATITUDE = "image lat";
	public static final String KEY_IMAGE_LONGITUDE = "image long";
	
	private final EditImageScreen self = this;
	
	private String imagePath;
	
	private Long dbId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_notes_screen);
		
		Intent intent = getIntent();
		if (intent.hasExtra(KEY_REPORT_ID)) {
			 // The report is in the database
			dbId = intent.getLongExtra(KEY_REPORT_ID, -1);
			if (dbId == -1l) dbId = null;
			
			loadFromDatabase(dbId);
		} else {
			 // The report is not in the database
			setImage(intent.getStringExtra(KEY_IMAGE_PATH));
			setLatitude(intent.getDoubleExtra(KEY_IMAGE_LATITUDE, -1));
			setLongitude(intent.getDoubleExtra(KEY_IMAGE_LONGITUDE, -1));
			setTime(intent.getIntExtra(KEY_IMAGE_TIME, -1));
		}
	}
	
	public void onDestroy() {
		ImageView i = (ImageView) findViewById(R.id.add_damage_image);
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
		Report r = new Report();
		r.picturePath = imagePath;
		r.address = getAddress();
		r.notes = getNotes();
		DatabaseHelper dbh = DatabaseHelper.openReadWrite(self);
		try {
			if (dbId != null) dbh.deleteReport(dbId);
			dbId = dbh.insertReport(r);
		} finally {
			dbh.close();
		}
		finish();
	}
	
	private void loadFromDatabase(long reportId) {
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
		try {
			Report source = dbh.getReport(reportId);
			setImage(source.picturePath);
			setAddress(source.address);
			setNotes(source.notes);
			setDamageIndicator(source.damageIndicator);
			setDamageDegree(source.degreeOfDamage);
		} finally {
			dbh.close();
		}
	}
	
	private void setTime(long time) {
		
	}
	
	private void setLatitude(double newLatitude) {
		((EditText) findViewById(R.id.edit_report_latitude)).setText(String.valueOf(newLatitude));
	}
	private void setLongitude(double newLongitude) {
		((EditText) findViewById(R.id.edit_report_longitude)).setText(String.valueOf(newLongitude));
	}
	
	private String getAddress() {
		return ((EditText) findViewById(R.id.edit_report_address_edittext)).getText().toString();
	}
	private void setAddress(String newAddress) {
		((EditText) findViewById(R.id.edit_report_address_edittext)).setText(newAddress);
	}
	
	private String getNotes() {
		return ((EditText) findViewById(R.id.edit_report_note_edittext)).getText().toString();
	}
	private void setNotes(String newNotes) {
		((EditText) findViewById(R.id.edit_report_note_edittext)).setText(newNotes);
	}
	
	private int getDamageIndicator() {
		Spinner indicators = (Spinner) findViewById(R.id.edit_report_damage_indicator);
		return (int) indicators.getSelectedItemId();
	}
	private void setDamageIndicator(int indicator) {
		Spinner indicators = (Spinner) findViewById(R.id.edit_report_damage_indicator);
	}
	
	private int getDamageDegree() {
		Spinner indicators = (Spinner) findViewById(R.id.edit_report_damage_degree);
		return (int) indicators.getSelectedItemId();
	}
	private void setDamageDegree(int degree) {
		Spinner indicators = (Spinner) findViewById(R.id.edit_report_damage_degree);
	}
	
	private void setImage(String path) {
		imagePath = path;
		ImageView i = (ImageView) findViewById(R.id.add_damage_image);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(path);
		i.setImageBitmap(bitmap);
	}
}