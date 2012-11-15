package com.AlabamaDamageTracker;

import java.io.File;
import java.io.IOException;
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

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

public class TakePictureScreen extends Activity {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 777;
	private static final String TAG = "TakePictureScreen";

	private File image = null;
	private long timeCaptured = 0L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Date now = new Date();
		// gets unix time in seconds
		timeCaptured = now.getTime() / 1000L;
		image = getOutputMediaFile(now);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, EditImageScreen.class);
				intent.putExtra(EditImageScreen.KEY_IMAGE_PATH, image.getPath());
				intent.putExtra(EditImageScreen.KEY_IMAGE_TIME, timeCaptured);
				double[] latLng = getLatLng(image);
				if (latLng != null) {
					intent.putExtra(EditImageScreen.KEY_IMAGE_LATITUDE, latLng[0]);
					intent.putExtra(EditImageScreen.KEY_IMAGE_LONGITUDE, latLng[1]);
				}
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

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(Date date) {

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
		
		// Create a image filename
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
		File mediaFile = new File(
				mediaStorageDir.getPath() +
				File.separator +
				"IMG_" +
				timeStamp +
				".jpg"
			);

		return mediaFile;
	}
	
	private double[] getLatLng(File file) {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(file);
			// obtain the Exif directory
			GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
			GeoLocation gpsInfo = gpsDirectory.getGeoLocation();
			double latitude = gpsInfo.getLatitude();
			double longitude = gpsInfo.getLongitude();
			return new double[] {latitude, longitude};
		} catch (NullPointerException npe) {
			return null;
		} catch (IOException ioe) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}