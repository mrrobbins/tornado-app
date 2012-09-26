package com.AlabamaDamageTracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class EditNotesScreen extends Activity {
	
	public static final String KEY_REPORT_ID = "report id";
	public static final String KEY_IMAGE_PATH = "image path";
	
	private final EditNotesScreen self = this;
	
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
			if (intent.hasExtra(KEY_IMAGE_PATH))
				setImage(intent.getStringExtra(KEY_IMAGE_PATH));
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
	
	public void onButtonPress(View v) {
		if (v == findViewById(R.id.edit_notes_save_button)) {
			saveToDatabase();
		} else if (v == findViewById(R.id.edit_notes_delete_button)) {
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
		DatabaseHelper dbh = DatabaseHelper.openReadWrite(self);
		try {
			dbh.insertReport(r);
		} finally {
			dbh.close();
		}
	}
	
	private void loadFromDatabase(long reportId) {
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
		try {
			Report source = dbh.getReport(reportId);
			setImage(source.picturePath);
		} finally {
			dbh.close();
		}
		
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