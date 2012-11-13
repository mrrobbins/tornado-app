package com.AlabamaDamageTracker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class TakePictureScreen extends Activity {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 777;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private static final String TAG = "TakePictureScreen";
	
	private Uri imageUri = null;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, EditImageScreen.class);
				intent.putExtra(EditImageScreen.KEY_IMAGE_PATH, imageUri.getPath());
				startActivityForResult(intent, 0);
			}
			else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
			}
		}
		finish();
	}
	

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {

		File mediaStorageDir = new File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			"Damage Tracker"
		);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			
			mediaFile = new File(
				mediaStorageDir.getPath() +
				File.separator +
				"IMG_" +
				timeStamp +
				".jpg"
			);
			
		} else if (type == MEDIA_TYPE_VIDEO) {
			
			mediaFile = new File(
				mediaStorageDir.getPath() +
				File.separator + "VID_" +
				timeStamp + ".mp4"
			);
			
		} else {
			return null;
		}

		return mediaFile;
	}

}