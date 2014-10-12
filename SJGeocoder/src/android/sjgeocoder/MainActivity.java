package android.sjgeocoder;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity {
	GoogleMap Gmap;
    MarkerOptions marker;
    LatLng latlong;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Gmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		Button button = (Button) findViewById(R.id.button1);
		
		OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = ((EditText)findViewById(R.id.editText1)).getText().toString();
 
                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };
        button.setOnClickListener(findClickListener);
	}

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
 
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
           latlong = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude());
                
           marker = new MarkerOptions();
           marker.position(latlong);
            
           Gmap.addMarker(marker);
           Gmap.animateCamera(CameraUpdateFactory.newLatLng(latlong));
            
        }
    }
}
