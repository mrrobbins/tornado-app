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

public class selectDamage_screen extends MapActivity implements OnClickListener
{
	/** Called when the activity is first created. */

	private LocationManager locationManager;
	private LocationListener locationListener;
	private GeoPoint point;
	private String address;
	private String notes;
	private MapController mapController;
	protected final Context Context = this;
	private MapView mapView;
	private Boolean streetView = true;
	private String LocationID;

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
		mapContinue.setText("Return Home");
		mapContinue.setOnClickListener(this);
		Button toggleView = (Button) findViewById(R.id.ToggleViewButton);
		toggleView.setOnClickListener(this);

		databaseHelper myDbHelper = new databaseHelper(this);
		myDbHelper = new databaseHelper(this);
		myDbHelper.openDataBase();
		Cursor c = myDbHelper.getAllLocations() ;
		try{
			c.moveToFirst();
			do {
				String lat =  c.getString(1);
				String lon = c.getString(2);
				if (lat != null || lon != null){
					point = new GeoPoint((int) (Double.valueOf(lat) * 1E6), (int) (Double.valueOf(lon) * 1E6));
					address = c.getString(3);
					LocationID = c.getString(0);
					drawPoint();
					mapController.animateTo(point);
					mapController.setZoom(19);
				}
			} while(c.moveToNext());
		}
		catch (Exception e){
			Toast.makeText(this, 
					"No Locations have been recorded.",
					Toast.LENGTH_LONG).show();
			finish();
		}


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
			Intent intent = new Intent(getBaseContext(),homeScreen.class);
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

			OverlayItem overlayitem = new OverlayItem(point, address, LocationID);

			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
		} 
		catch (Exception e) {


		}
	}
}








/*package com.AlabamaDamageTracker;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class selectDamage_screen extends ListActivity {

	public  ArrayList<String> Locations = new ArrayList<String>();
	public ArrayList<String> LocationIDs = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);


	  databaseHelper myDbHelper = new databaseHelper(this);
      myDbHelper = new databaseHelper(this);
      myDbHelper.openDataBase();
      Cursor c = myDbHelper.getAllLocations() ;
      try{
      c.moveToLast();
      do {
    	  String Address =  c.getString(3);
    	  Locations.add("Address: "+Address);
    	  String id = c.getString(0);
    	  LocationIDs.add(id);

      //}while (c.moveToNext());
      } while(c.moveToPrevious());
      }
      catch (Exception e){
    	  Toast.makeText(this, 
                  "No Locations have been recorded.",
                  Toast.LENGTH_LONG).show();
    	  finish();
      }

      c.close();
      myDbHelper.close();


	  setListAdapter( new ArrayAdapter<String>(this, R.layout.select_damage, Locations));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	}

	 protected void onListItemClick(ListView l, View view, int position, long id) {
		// 	  super.onListItemClick(l, view,position,id);

		// 	  String IDNum = LocationIDs.get(position);
		// 	  long ID = Long.parseLong(IDNum.trim());;
		 //    ((currentDamage) this.getApplicationContext()).setReviewID( (ID));
		 //	  Intent intent = new Intent(getBaseContext(),review_notes_screen.class);
		 //	  intent.putExtra("ID", ID);
		 //	  startActivityForResult(intent,0); 
		 //	  finish();
		    }
}

 */
