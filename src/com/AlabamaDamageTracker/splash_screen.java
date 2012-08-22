package com.AlabamaDamageTracker;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class splash_screen extends Activity implements Runnable{

	public int X = 0;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	  
		setContentView(R.layout.splash);	
		Context x = this;
		databaseHelper myDbHelper = new databaseHelper(x);
		myDbHelper = new databaseHelper(this);
		try {
			myDbHelper.createDataBase();
		} catch (IOException ioe) {	 
			throw new Error("Unable to create database");	 
		}		 
		try {	 
			myDbHelper.openDataBase();	 
		} catch(SQLException sqle){
			throw sqle;		 
		}
		Intent intent = new Intent(getBaseContext(),homeScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent,0);
		finish();
	}

	public void onResume(Bundle savedInstanceState){
		Intent intent = new Intent(getBaseContext(),homeScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent,0);
		finish();
	}

	public void run() {
		databaseHelper myDbHelper = new databaseHelper(this);
		myDbHelper = new databaseHelper(this);
		try {
			myDbHelper.createDataBase();
		} catch (IOException ioe) {	 
			throw new Error("Unable to create database");	 
		}		 
		try {	 
			myDbHelper.openDataBase();	 
		}catch(SQLException sqle){
			throw sqle;		 
		}
		myDbHelper.close(); 
		handler.sendEmptyMessage(0);
	}


	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Intent intent = new Intent(getBaseContext(),homeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent,0);
			finish();
		}
	};


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Intent intent = new Intent(getBaseContext(),homeScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent,0);
		finish();}
}

