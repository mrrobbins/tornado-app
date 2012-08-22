package com.AlabamaDamageTracker;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class homeScreen extends Activity {
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	protected final Context Context = this;
	private long LocationID;
	private String latitude = null;
	private String longitude = null;
	
    /** Called when the activity is first created. */
    @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	   }
	   
	   public void onResume(){
		super.onResume();
        setContentView(R.layout.home_screen);
    
        
        // Get access to the system location services. 
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        
        // Set up a location listener for the device.
        locationListener = new MyLocationListener();
        
        // Receive GPS location updates for the device.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        
        
        final Button newDamage = (Button) findViewById(R.id.home_new_location);
        final Button reportedDamage = (Button) findViewById(R.id.home_reported_damage);

        
        currentDamage info = ((currentDamage)getApplicationContext());
	    LocationID = info.getLocationID();
        
        newDamage.setOnClickListener (new View.OnClickListener() {
		   	public void onClick(View v) {
		   		databaseHelper myDbHelper = new databaseHelper(Context);    
		        myDbHelper.openDataBase();
		   		long locationID = myDbHelper.insertDamage(latitude, longitude, "", "", "", "", "", "", "");
		   		((currentDamage) Context.getApplicationContext()).setLocationID(locationID);
		   		//myDbHelper.updateGPS(latitude, LocationID, longitude);
				myDbHelper.close();
		   		Intent intent = new Intent(getBaseContext(),takePicture_screen.class);
	   			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	   		    startActivityForResult(intent,0);
		   	}
        });
        
        reportedDamage.setOnClickListener (new View.OnClickListener() {
		   	public void onClick(View v) {
	   			Intent intent = new Intent(getBaseContext(),selectDamage_screen.class);
	   			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	   		    startActivityForResult(intent,0);
		   	}
        });
        
    
    }
    
    public class MyLocationListener implements LocationListener

	{

	public void onLocationChanged(Location loc)
	{
	latitude = String.valueOf(loc.getLatitude());
	longitude = String.valueOf(loc.getLongitude());
	}


	public void onProviderDisabled(String provider)

	{

	Toast.makeText( getApplicationContext(),

	"Gps Disabled",

	Toast.LENGTH_SHORT ).show();

	}


	public void onProviderEnabled(String provider)

	{

	Toast.makeText( getApplicationContext(),
	"Gps Enabled",
	Toast.LENGTH_SHORT).show();
	}


	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}
	}
	
    
}