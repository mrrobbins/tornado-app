package com.AlabamaDamageTracker;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class TakePictureScreen extends Activity {
	
	public static final String KEY_LOCATION_ID = "location id";

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
	private long locationId;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		locationId = getIntent().getLongExtra(KEY_LOCATION_ID, -1); 

		takePicture(locationId+"", locationId);
	}

	public void takePicture(String filename, long id){
		DatabaseHelper myDbHelper = new DatabaseHelper(this);
		myDbHelper.openDataBase();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, filename);
		values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		Uri imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);	   	
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		File picture = convertImageUriToFile(imageUri,this);
		String pic1 = picture.getAbsolutePath(); 
		long p = myDbHelper.insertPicture(pic1, id);
		myDbHelper.updatePic(p, id);
		myDbHelper.close();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {	
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					String provider = null;
					Intent intent = new Intent(this, AddNotesScreen.class);
					intent.putExtra(AddNotesScreen.KEY_LOCATION_ID, locationId);
					startActivityForResult(intent,0);
				}
				else if (resultCode == RESULT_CANCELED) {
					Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
				} else {
					Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
				}
			}
		} catch (Exception e) { }
	}

	public static File convertImageUriToFile(Uri imageUri, Activity activity)  {
		Cursor cursor = null;
		String [] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
		cursor = activity.managedQuery( imageUri,
				proj, // Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
		if (cursor.moveToFirst()) {
			@SuppressWarnings("unused")
			String orientation =  cursor.getString(orientation_ColumnIndex);
			return new File(cursor.getString(file_ColumnIndex));
		}				    
		return null;
	}

}