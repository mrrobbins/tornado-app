package com.AlabamaDamageTracker;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class MapBackdropActivity extends MapActivity implements OnClickListener
{
    /** Called when the activity is first created. */
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private GeoPoint point;
	private GeoPoint point1;
	private MapController mapController;
	protected final Context Context = this;
	private MapView mapView;
	private Boolean streetView = true;
	private long LocationID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Configure the map.
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);
        
        // Manages panning and zooming of the map.
        mapController = mapView.getController();
        
        // Get access to the system location services. 
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        
        // Set up a location listener for the device.
        locationListener = new GPSLocationListener();
        
        // Receive GPS location updates for the device.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        // Event handlers for buttons.
        Button mapContinue = (Button) findViewById(R.id.mapContinue);
        mapContinue.setOnClickListener(this);
        Button toggleView = (Button) findViewById(R.id.ToggleViewButton);
        toggleView.setOnClickListener(this);
        
        currentDamage info = ((currentDamage)getApplicationContext());
	     LocationID = info.getLocationID(); 
	     databaseHelper myDbHelper = new databaseHelper(Context);    
	        myDbHelper.openDataBase();

        	Cursor c = myDbHelper.getDamageByID(LocationID);
        	//Toast.makeText( getApplicationContext(),
        	//		"Testomg. why is this not working"+c.getCount(),
        	//		Toast.LENGTH_SHORT).show();
	        try {

        	point = new GeoPoint((int) (Double.valueOf(c.getString(1)) * 1E6), (int) (Double.valueOf(c.getString(2)) * 1E6));
			drawPoint();
			mapController.animateTo(point);
			mapController.setZoom(19);
        }
        catch(Exception e){
        	Toast.makeText( getApplicationContext(),
                			"Problem with GPS Coordinates "+c.getCount(),
                			Toast.LENGTH_SHORT).show();
        	Intent intent = new Intent(getBaseContext(),add_notes_screen.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivityForResult(intent,0);
        	finish();}

    }
    
    /**
     * Inner class implementing a location listener for the map view.
     * @author Team 1
     *
     */
	private final class GPSLocationListener implements LocationListener
	{
		/**
		 * Get GPS location as location of device changes.
		 */
		public void onLocationChanged(Location location) 
		{
			if (location != null) 
			{
				 //Get current GPS location and zoom map view to that location.
				point1 = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
				//mapController.animateTo(point);
				//mapController.setZoom(19);
		    }
		}
		
		public void onProviderDisabled(String provider)
		{
			Toast.makeText(getApplicationContext(), "GPS is disabled...Must enable to use this app...", Toast.LENGTH_LONG).show();
		}

		public void onProviderEnabled(String provider) 
		{
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) 
		{
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Override method for displaying routes.
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
	
	/**
	 * Click handler.
	 */
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.mapContinue:
			{
				Intent intent = new Intent(getBaseContext(),add_notes_screen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,0);
				finish();
				break;
			}
			case R.id.ToggleViewButton:
			{
				if (streetView)
				{
					mapView.setSatellite(true);
					streetView = false;
					break;
				}
				else
				{
					mapView.setSatellite(false);
					streetView = true;
					break;
				}
			}
		}
	}
	
	/**
	 * Draw GPS point retrieved from location listener as overlay on map.
	 */
	private void drawPoint()
	{
		
		try {
		List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.gpsmarker);
        PhotoLocationOverlays itemizedoverlay = new PhotoLocationOverlays(drawable, this);
		
        OverlayItem overlayitem = new OverlayItem(point, null, null);
        
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
		} 
		catch (Exception e) {
			
			List<Overlay> mapOverlays = mapView.getOverlays();
	        Drawable drawable = this.getResources().getDrawable(R.drawable.gpsmarker);
	        PhotoLocationOverlays itemizedoverlay = new PhotoLocationOverlays(drawable, this);
			
	        OverlayItem overlayitem = new OverlayItem(point1, null, null);
	        
	        itemizedoverlay.addOverlay(overlayitem);
	        mapOverlays.add(itemizedoverlay);
			
		}
	}
}

