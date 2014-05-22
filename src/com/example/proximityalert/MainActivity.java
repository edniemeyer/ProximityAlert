package com.example.proximityalert;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	
	
	//public static final int RADIUS = 200;
	private static final String TAG_SUCCESS = "success";
	
	private SeekBar Radius = null;
	public static float progressChanged = 100;
	private static String lat;
	private static String lng;
	private static String radius;
	private static String idUser = "1";
	private static String idPoint = "1";
	//private Circle mCircle;
	
	GoogleMap googleMap;
	LocationManager locationManager;
	PendingIntent pendingIntent;
	SharedPreferences sharedPreferences;
	
	private ProgressDialog pDialog;
	JSONParser<HttpContext> jsonParser = new JSONParser<HttpContext>();
	
	private static final String url = "http://ec2-54-207-73-220.sa-east-1.compute.amazonaws.com/proximity_alert/sync.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available        	

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);           
            
            
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            
            
            // Opening the sharedPreferences object
    		sharedPreferences = getSharedPreferences("location", 0);
    		
    		// Getting stored latitude if exists else return 0
    		lat = sharedPreferences.getString("lat", "0");
    		
    		// Getting stored longitude if exists else return 0
    		lng = sharedPreferences.getString("lng", "0");
    		
    		// Getting stored zoom level if exists else return 0
    		String zoom = sharedPreferences.getString("zoom", "0");
    		
    		// Getting stored radius if exists else return 100
    		progressChanged = sharedPreferences.getFloat("radius", 100);
    		radius = String.valueOf(progressChanged);
    		
    		// If coordinates are stored earlier
    		if(!lat.equals("0")){
    			
    			// Drawing circle on the map
    			drawCircle(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), progressChanged);
    			
    			//mCircle.setRadius(Double.parseDouble(radius));
    			
    			// Drawing marker on the map
    			drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
    			
    			// Moving CameraPosition to previously clicked position
    			googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
    			
    			// Setting the zoom level in the map
    			googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));    			
    			/*
    			googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
                	
                	@Override
    				public void onMapLoaded() {
                		
                		sharedPreferences = getSharedPreferences("location", 0);
                		
                		// Getting stored latitude if exists else return 0
                		String lat = sharedPreferences.getString("lat", "0");
                		
                		// Getting stored longitude if exists else return 0
                		String lng = sharedPreferences.getString("lng", "0");
                		
                		Intent proximityIntent = new Intent("com.example.proximityalert.activity.proximity");					
    					
    			        // Creating a pending intent which will be invoked by LocationManager when the specified region is
    			        // entered or exited
    			        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);			        
    			        // Setting proximity alert 
    			        // The pending intent will be invoked when the device enters or exits the region
    			        // away from the marked point
    			        // The -1 indicates that, the monitor will not be expired
    			        locationManager.addProximityAlert(Double.parseDouble(lat), Double.parseDouble(lng), progressChanged, -1, pendingIntent);	
                	}
                });
                */
    		}
    		
    		Radius = (SeekBar) findViewById(R.id.radius);
			Radius.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	            	
	                progressChanged = progress+20;
	                //mCircle.setRadius(progress);
	                drawCircle(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), progressChanged);
	            }
	 
	            public void onStartTrackingTouch(SeekBar seekBar) {
	                // TODO Auto-generated method stub
	            }
	 
	            public void onStopTrackingTouch(SeekBar seekBar) {
	                Toast.makeText(MainActivity.this,progressChanged+" m", 
	                        Toast.LENGTH_SHORT).show();
	                
	                drawCircle(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), progressChanged);
	                drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
	                // This intent will call the activity ProximityActivity
			        Intent proximityIntent = new Intent("com.example.proximityalert.activity.proximity");					
					
			        // Creating a pending intent which will be invoked by LocationManager when the specified region is
			        // entered or exited
	                pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);			        
			        
			        // Setting proximity alert 
			        // The pending intent will be invoked when the device enters or exits the region with a dynamic radius
			        // away from the marked point
			        // The -1 indicates that, the monitor will not be expired
			        locationManager.addProximityAlert(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)).latitude, new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)).longitude, progressChanged, -1, pendingIntent);	
			        /** Opening the editor object to write data to sharedPreferences */
			        SharedPreferences.Editor editor = sharedPreferences.edit();

			        /** Storing the latitude of the current location to the shared preferences */
			        editor.putString("lat", Double.toString(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)).latitude));
			        lat = Double.toString(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)).latitude);
			        /** Storing the longitude of the current location to the shared preferences */
			        editor.putString("lng", Double.toString(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)).longitude));
			        lng = Double.toString(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)).longitude);;
			        /** Storing the zoom level to the shared preferences */
			        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));
			        
			        /** Storing the radius to the shared preferences */
			        editor.putFloat("radius", progressChanged);
			        radius = String.valueOf(progressChanged);
			        /** Saving the values stored in the shared preferences */
			        editor.commit();
			        Log.v("latitude123", "uid="+lat);
					Log.v("longitude123", "uid="+lng);
					Log.v("radiua123", "uid="+radius);
	            }
	            });
			
    		
            googleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					
					// Removes the existing marker from the Google Map
					googleMap.clear();		
										
					// Drawing circle on the map
					//drawDynamicCircle(point);
					drawCircle(point, progressChanged);
					
					// Drawing marker on the map
					drawMarker(point);
					
					//Edit circle radius
					
					
					
			        // This intent will call the activity ProximityActivity
			        Intent proximityIntent = new Intent("com.example.proximityalert.activity.proximity");					
					
			        // Creating a pending intent which will be invoked by LocationManager when the specified region is
			        // entered or exited
			        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);			        
			        
			        // Setting proximity alert 
			        // The pending intent will be invoked when the device enters or exits the region
			        // away from the marked point
			        // The -1 indicates that, the monitor will not be expired
			        locationManager.addProximityAlert(point.latitude, point.longitude, progressChanged, -1, pendingIntent);	
			        
			        /** Opening the editor object to write data to sharedPreferences */
			        SharedPreferences.Editor editor = sharedPreferences.edit();

			        /** Storing the latitude of the current location to the shared preferences */
			        editor.putString("lat", Double.toString(point.latitude));
			        lat = Double.toString(point.latitude);
			        /** Storing the longitude of the current location to the shared preferences */
			        editor.putString("lng", Double.toString(point.longitude));
			        lng = Double.toString(point.longitude);
			        /** Storing the zoom level to the shared preferences */
			        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));
			        
			        /** Storing the radius to the shared preferences */
			        editor.putFloat("radius", progressChanged);
			        radius = String.valueOf(progressChanged);
			        /** Saving the values stored in the shared preferences */
			        editor.commit();		        
			        Log.v("latitude", "uid="+lat);
					Log.v("longitude", "uid="+lng);
					Log.v("radiua", "uid="+radius);
			        //Toast.makeText(getBaseContext(), "Proximity Alert is added", Toast.LENGTH_SHORT).show();			        
			        
				}
			});    
            
            googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {				
				@Override
				public void onMapLongClick(LatLng point) {
					Intent proximityIntent = new Intent("com.example.proximityalert.activity.proximity");
					
					pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
					
					// Removing the proximity alert					
					locationManager.removeProximityAlert(pendingIntent);
					
					// Removing the marker and circle from the Google Map
					googleMap.clear();
					
					// Opening the editor object to delete data from sharedPreferences
			        SharedPreferences.Editor editor = sharedPreferences.edit();
			        
			        // Clearing the editor
			        editor.clear();
					
			        // Committing the changes
					editor.commit();
					
					//Toast.makeText(getBaseContext(), "Proximity Alert is removed", Toast.LENGTH_LONG).show();
				}
			});
            
            
            final Button button = (Button) findViewById(R.id.sync_button);
            button.setOnClickListener(new OnClickListener(){
   		        @Override
   		        //On click function
   		        public void onClick(View view) {
   		            //Create the intent to start another activity
   		        	new SyncCloud().execute();
   		        }
   		    });
		}
	}
		
	
	private void drawMarker(LatLng point){
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();					
		
		// Setting latitude and longitude for the marker
		markerOptions.position(point);
		
		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);
		
	}
	
	
	private void drawCircle(LatLng point,double radius){
		
		// Removing old circles from GoogleMap
		googleMap.clear();
				
		// Instantiating CircleOptions to draw a circle around the marker
		CircleOptions circleOptions = new CircleOptions();
		
		// Specifying the center of the circle
		circleOptions.center(point);
		
		// Radius of the circle
		circleOptions.radius(radius);
		
		// Border color of the circle
		circleOptions.strokeColor(Color.BLACK);
		
		// Fill color of the circle
		circleOptions.fillColor(0x30ff0000);
		
		// Border width of the circle
		circleOptions.strokeWidth(2);
		
		// Adding the circle to the GoogleMap
		googleMap.addCircle(circleOptions);
		
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	class SyncCloud extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(getString(R.string.synchronizing));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("latitude", lat));
						params.add(new BasicNameValuePair("longitude", lng));
						params.add(new BasicNameValuePair("radius", radius));
						params.add(new BasicNameValuePair("idUser", idUser));
						params.add(new BasicNameValuePair("idPoint", idPoint));
						
						// getting JSON Object
						
						JSONObject json = jsonParser.makeHttpRequest(url,
								"POST", params);
						Log.v("-+-+-+---+-+-+++-++-", "json="+params);
						// check log cat for response
						Log.d("Create Response", json.toString());
						Log.v("------------------", "uid="+lat);
						Log.v("=============2", "uid="+lng);
						Log.v("------------------", "uid="+idUser);
						Log.v("------------------", "uid="+idPoint);
						Log.v("------------------", "uid="+radius);
						// check for success tag
						try {
							int success = json.getInt(TAG_SUCCESS);
							Log.v("=============2", "uid="+lng);
							if (success == 1) {
								// successfull
								
							}
							else{
								
							}
						} catch (JSONException e) {
							e.printStackTrace();


				};
		return null;		
	}

		/*
		 * After completing background task Dismiss the progress dialog
		 * **/
		
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
}


