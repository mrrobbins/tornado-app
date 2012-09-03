package com.AlabamaDamageTracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ReviewNotesScreen extends Activity implements OnInitListener {
	
	public static final String KEY_LOCATION_ID = "location id";

	private final ReviewNotesScreen self = this;	private final static String TAG = "ReviewNotes";
	
	private String GPS;
	private String address;
	private String notesText;
	private String userID;
	private String Damage;
	private String EF;
	private long locationId;
	public TextToSpeech talker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationId = getIntent().getLongExtra(KEY_LOCATION_ID, -1);		
		setContentView(R.layout.review_notes_screen);

		talker = new TextToSpeech(this,this);


		DatabaseHelper myDbHelper = new DatabaseHelper(self);    
		myDbHelper.openDataBase();
		try {			Log.d(TAG, "locationId: " + locationId);
			Cursor c = myDbHelper.getDamageByID(locationId);			try {				GPS = c.getString(1)+", "+c.getString(2);
				address = c.getString(3);
				notesText = c.getString(5);
				userID = c.getString(6);
				Damage = c.getString(8);
				EF = c.getString(9);
				TextView tv = (TextView) findViewById(R.id.Review_GPS);
				tv.setText("GPS: "+ GPS);
				tv.requestFocus();
				TextView tv1 = (TextView) findViewById(R.id.review_EF);
				tv1.setText("EF Rating: "+EF);
				tv1.requestFocus();
				TextView tv2 = (TextView) findViewById(R.id.review_Degree_of_Damage);
				tv2.setText("Degree of Damage: "+Damage);
				tv2.requestFocus();
				TextView tv3 = (TextView) findViewById(R.id.review_notes);
				tv3.setText("Notes: "+notesText);
				tv3.requestFocus();
				TextView tv4 = (TextView) findViewById(R.id.Review_StreetAdd);
				tv4.setText("Street Address: "+ address);
				tv4.requestFocus();			} finally {				c.close();			}

			c = myDbHelper.getDamagePic(locationId);			try {
				ImageView i = (ImageView) findViewById(R.id.ReviewImage);
				String picture = c.getString(0);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
	
				Bitmap bitmap = BitmapFactory.decodeFile(picture, options);				if (bitmap == null) i.setVisibility(View.GONE);
				else i.setImageBitmap(bitmap);			} finally {
				c.close();  			}
		} finally {
			myDbHelper.close();
		}

		final Button HOME = (Button) findViewById(R.id.HOME);
		HOME.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(self, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
				finish();
			}
		});

		final Button deleteDamage = (Button) findViewById(R.id.editDamage);
		deleteDamage.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(self, AddNotesScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(AddNotesScreen.KEY_LOCATION_ID, locationId);
				startActivityForResult(intent,0);
				finish();
			}
		});

		final Button TalkButton = (Button) findViewById(R.id.ReviewAudio);
		TalkButton.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {

				talker.speak(notesText, TextToSpeech.QUEUE_FLUSH, null);
			}
		}); 
	}

	public void say(String text2say){

		talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);

	}

	public void onInit(int status) {



	}

	@Override

	public void onDestroy() {

		if (talker != null) {

			talker.stop();

			talker.shutdown();

		}



		super.onDestroy();

	}

}