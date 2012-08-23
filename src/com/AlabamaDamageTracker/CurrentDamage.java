package com.AlabamaDamageTracker;

import android.app.Application;

public class CurrentDamage extends Application {

	private long userID;
	private long locationID;
	private long reviewID;

	public void setUserID(long id){
		userID = id;
	}

	public void setLocationID(long id){
		locationID = id;
	}

	public void setReviewID(long id){
		reviewID = id;
	}

	public long getUserID(){
		return userID;
	}

	public long getLocationID(){
		return locationID;
	}

	public long getReviewID(){
		return reviewID;
	}
}