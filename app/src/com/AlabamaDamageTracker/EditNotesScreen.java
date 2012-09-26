package com.AlabamaDamageTracker;

import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;


public class EditNotesScreen extends Activity {
	
	public static final String KEY_REPORT_ID = "report id";
	public static final String KEY_IMAGE_PATH = "image path";
	
	private final EditNotesScreen self = this;
	
	private String imagePath;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_notes_screen);
		
		Intent intent = getIntent();
		if (intent.hasExtra(KEY_REPORT_ID)) {
			 // The report is in the database
			long reportId = intent.getLongExtra(KEY_REPORT_ID, -1);
			loadFromDatabase(intent.getLongExtra(KEY_REPORT_ID, -1));
		} else {
			 // The report is not in the database
			if (intent.hasExtra(KEY_IMAGE_PATH))
				setImage(intent.getStringExtra(KEY_IMAGE_PATH));
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

		Bitmap bitmap = BitmapFactory.decodeFile(new File(URI.create(path)).getAbsolutePath(), options);
		i.setImageBitmap(bitmap);
	}
	

}