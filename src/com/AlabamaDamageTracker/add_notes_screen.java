package com.AlabamaDamageTracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class add_notes_screen extends Activity {

	protected final Context Context = this;

	private long LocationID;
	private String ef;
	private String degree;
	private String address;
	private String notes;


	private static final String TAG = "VoiceRecognition";

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private ListView mList;
	private Handler mHandler;
	private EditText mEdit;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_notes_screen);
		currentDamage info = ((currentDamage)getApplicationContext());
		LocationID = info.getLocationID(); 

		try {
			databaseHelper myDbHelper = new databaseHelper(this);
			myDbHelper.openDataBase();
			Cursor c = myDbHelper.getDamagePic(LocationID);
			ImageView i = (ImageView) findViewById(R.id.add_damage_image);
			String picture = c.getString(0);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;

			Bitmap bitmap = BitmapFactory.decodeFile(picture, options);
			i.setImageBitmap(bitmap);
			c.close();  

		}
		catch (Exception e){
			final ImageView i = (ImageView) findViewById(R.id.add_damage_image);
			i.setVisibility(View.GONE);		        
		} 


		Spinner s = (Spinner) findViewById(R.id.add_ef_rating);
		@SuppressWarnings("rawtypes")
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.EF_Rating,android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
				Object item = parent.getItemAtPosition(pos);
				ef = item.toString();
			}
			public void onNothingSelected(AdapterView<?> parent){}
		});
		Spinner spinnerDegOfDamag = (Spinner) findViewById(R.id.add_degree_of_damage);
		@SuppressWarnings("rawtypes")
		ArrayAdapter adaptDegOfDamg = ArrayAdapter.createFromResource(this,R.array.Damage_Rating,android.R.layout.simple_spinner_dropdown_item);
		spinnerDegOfDamag.setAdapter(adaptDegOfDamg);
		spinnerDegOfDamag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
				Object item = parent.getItemAtPosition(pos);
				degree = item.toString();
			}
			public void onNothingSelected(AdapterView<?> parent){}
		});


		try {
			databaseHelper myDbHelper = new databaseHelper(this);
			myDbHelper.openDataBase();
			Cursor c = myDbHelper.getDamageByID(LocationID);
			String GPS = c.getString(1)+", "+c.getString(2);
			String aAddress = c.getString(3);
			String notesText = c.getString(5);
			String Damage = c.getString(8);
			String EF = c.getString(9);
			TextView tv = (TextView) findViewById(R.id.add_gps_location);
			tv.setText("GPS: "+ GPS);
			tv.requestFocus();
			TextView tv3 = (TextView) findViewById(R.id.add_notes_about_damage);
			tv3.setText(notesText);
			tv3.requestFocus();
			TextView tv4 = (TextView) findViewById(R.id.add_street_address);
			tv4.setText(aAddress);
			tv4.requestFocus();
			int spinnerPosition = adapter.getPosition(c.getString(9));
			s.setAdapter(adapter);
			s.setSelection(spinnerPosition);
			int spinnerPosition1 = adaptDegOfDamg.getPosition(c.getString(8));
			spinnerDegOfDamag.setAdapter(adaptDegOfDamg);
			spinnerDegOfDamag.setSelection(spinnerPosition1);
			c.close();
		}     catch (Exception e){}




		final Button saveDamage = (Button) findViewById(R.id.Save);
		saveDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				databaseHelper myDbHelper = new databaseHelper(Context);    
				myDbHelper.openDataBase();

				MultiAutoCompleteTextView add = (MultiAutoCompleteTextView) findViewById(R.id.add_street_address);
				Editable addText = add.getText();
				address = addText.toString();

				MultiAutoCompleteTextView notesEditor = (MultiAutoCompleteTextView) findViewById(R.id.add_notes_about_damage);
				Editable notesEdit = notesEditor.getText();
				notes = notesEdit.toString();

				myDbHelper.updateDamage(LocationID, "", "", address, "", notes, "Kyle", degree, ef);
				Intent intent = new Intent(getBaseContext(),review_notes_screen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
				finish();
				myDbHelper.close();
			}
		});

		final Button deleteDamage = (Button) findViewById(R.id.Delete);
		deleteDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {

				databaseHelper myDbHelper = new databaseHelper(Context);    
				myDbHelper.openDataBase();
				myDbHelper.deleteRecord( LocationID);
				myDbHelper.close();
				((currentDamage) Context.getApplicationContext()).setLocationID(-1);
				Intent intent = new Intent(getBaseContext(),homeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
				finish();
			}
		});

		final Button audio = (Button) findViewById(R.id.button1);
		audio.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {

				mHandler = new Handler();
				mList = (ListView) findViewById(R.id.list);
				PackageManager pm = getPackageManager();
				List<ResolveInfo> activities = pm.queryIntentActivities(
						new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
				refreshVoiceSettings();

				startVoiceRecognitionActivity();
			}

		});




		final Button ReviewDamagePics = (Button) findViewById(R.id.add_all_pictures);
		ReviewDamagePics.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),damageGallery.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
			}
		});

		final Button addDamagePics = (Button) findViewById(R.id.add_pictures);
		addDamagePics.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				//((currentDamage) Context.getApplicationContext()).setLocationID(LocationID);
				Intent intent = new Intent(getBaseContext(),takePicture_screen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
				finish();
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putLong("ID", LocationID);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		LocationID = savedInstanceState.getLong("ID");

	}


	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");

		// Given an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	private class SupportedLanguageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {

			final Bundle extra = getResultExtras(false);

			if (getResultCode() != Activity.RESULT_OK) {
				mHandler.post(new Runnable() {
					//@Override
					public void run() {
						showToast("Error code:" + getResultCode());
					}
				});
			}

			if (extra == null) {
				mHandler.post(new Runnable() {
					//@Override
					public void run() {
						showToast("No extra");
					}
				});
			}


		}

		private void showToast(String text) {
			Toast.makeText(add_notes_screen.this, text, 1000).show();
		}	   		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			String notes = matches.get(0);
			TextView tv3 = (TextView) findViewById(R.id.add_notes_about_damage);
			tv3.setText(notes);
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	private void refreshVoiceSettings() {

		sendOrderedBroadcast(RecognizerIntent.getVoiceDetailsIntent(this), null,
				new SupportedLanguageBroadcastReceiver(), null, Activity.RESULT_OK, null, null);
	}

}