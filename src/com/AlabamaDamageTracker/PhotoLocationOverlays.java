package com.AlabamaDamageTracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PhotoLocationOverlays extends ItemizedOverlay<OverlayItem> 
{
	/**
	 * Overlay class for the map.
	 */
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private static Context mContext;
	protected boolean isMapMoving = false;

	/**
	 * Constructor 
	 * @param defaultMarker is default marker for each of the overlay items. 
	 */
	public PhotoLocationOverlays(Drawable defaultMarker)
	{
		super(boundCenterBottom(defaultMarker));
	}

	/**
	 * Returns an OverLayItem from the specified position.
	 */
	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	/**
	 * Returns the current number of items in the ArrayList.
	 */
	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	
	public void addOverlay(OverlayItem overlay) 
	{
	    mOverlays.add(overlay);
	    populate();
	}
	
	public PhotoLocationOverlays(Drawable defaultMarker, Context context) 
	{
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
	}

	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  //AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  //dialog.setTitle(item.getTitle());
	  String id = item.getSnippet();
	  ((currentDamage) mContext.getApplicationContext()).setLocationID(Long.parseLong(id));
	  Intent intent = new Intent(mContext,review_notes_screen.class);
	  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	  PhotoLocationOverlays.mContext.startActivity(intent);
	  //dialog.setMessage(item.getSnippet());
	  //dialog.show();
	  return true;
	}


	
}
