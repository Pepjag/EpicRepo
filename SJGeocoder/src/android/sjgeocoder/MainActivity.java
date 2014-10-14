package android.sjgeocoder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements LocationListener {
	GoogleMap Gmap;
	LocationManager locationManager;
	String provider;
	Criteria criteria;

	MarkerOptions foundMarker;
	LatLng foundLatLong;
	Location foundLocation;

	MarkerOptions userMarker;
	LatLng userLatLong;
	Location userLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Gmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		Button button = (Button) findViewById(R.id.button1);
		userMarker = new MarkerOptions();
		
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Gmap.setMyLocationEnabled(true);
		
		// the last known location of this provider
		userLocation = locationManager
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

		// set up criteria to find a better location services provider than passive
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // default
		provider = locationManager.getBestProvider(criteria, false);

		// see if we can get a provider, if not, attempt to go to location services options
		if (userLocation != null) {
			System.out.println("Provider has been selected.");
			onLocationChanged(userLocation);
			if ((userLatLong != null)
					&& ((Math.abs(userLatLong.latitude) > 0.01) && (Math.abs(userLatLong.longitude) > 0.01))) {
				Gmap.addMarker(userMarker);
				userMarker.title("User location!");
				Gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 10.0f));
			}
		} else {
			System.out.println("Provider has NOT been selected.");
			// leads to the settings because there is no last known location
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		// attempt to get location updates
		locationManager.requestLocationUpdates(provider, 500, 1, this);

		// set up "find" button listener
		OnClickListener findClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputLocation = ((EditText) findViewById(R.id.editText1))
						.getText().toString();

				if (inputLocation != null && !inputLocation.equals("")) {
					new GeocoderTask().execute(inputLocation);
				}
			}

		};
		button.setOnClickListener(findClickListener);
		Gmap.setMyLocationEnabled(false);

	}

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... locationName) {

			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> address = null;

			try {
				address = geocoder.getFromLocationName(locationName[0], 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return address;
		}

		@Override
		protected void onPostExecute(List<Address> address) {
			Gmap.clear();
			
			Address currentAddress = (Address) address.get(0);
			foundLatLong = new LatLng(currentAddress.getLatitude(),
					currentAddress.getLongitude());

			foundMarker = new MarkerOptions();
			foundMarker.position(foundLatLong);
			foundMarker.title(currentAddress.getAddressLine(0));
			foundMarker.snippet(currentAddress.getAddressLine(1));

			if ((foundLatLong != null)
					&& ((Math.abs(foundLatLong.latitude) > 0.01) && (Math.abs(foundLatLong.longitude) > 0.01))) {
				Gmap.addMarker(foundMarker);
				
				PolylineOptions lineOptions = new PolylineOptions()
				.add(userLatLong).add(foundLatLong).width(2.0f)
				.color(Color.BLUE);
				
				Gmap.addPolyline(lineOptions);
				Gmap.addPolyline(lineOptions);
			
				if ((userLatLong != null)
						&& ((Math.abs(userLatLong.latitude) > 0.01) && (Math.abs(userLatLong.longitude) > 0.01))) {
					Gmap.addMarker(userMarker);
					LatLngBounds bounds = new LatLngBounds.Builder().include(userLatLong).include(foundLatLong).build();
					Gmap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
					
					float results[] = new float[1];
					Location.distanceBetween(userLatLong.latitude, userLatLong.longitude, foundLatLong.latitude, foundLatLong.longitude, results);
					
					TextView distanceText = (TextView) findViewById(R.id.textView1);
					distanceText.setText(String.format("Distance: \n%.2f miles", results[0] * 0.000621371));
				} else {
					Gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(foundLatLong, 10.0f));
				}
			}
		}
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		//userLocation = Gmap.getMyLocation();
		userLatLong = new LatLng(location.getLatitude(), location.getLongitude());
		userMarker.position(userLatLong);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(MainActivity.this,
				provider + "'s status changed to " + status + "!",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(MainActivity.this,
				"Provider " + provider + " disabled!", Toast.LENGTH_SHORT)
				.show();
	}

}
