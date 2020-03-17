package ca.cmpt276.restaurantreport.ui;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    RestaurantManager manager = RestaurantManager.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        }
        catch(NullPointerException e){
            Log.d("MapView","getMapAsync Returned null");
            e.printStackTrace();
        }

        ReadCSV readCSV = ReadCSV.getInstance(this);

        //String apiKey = getString(R.string.mapApiKey);
        //Places.initialize(this, apiKey);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        populateRestaurants();
    }

    private void populateRestaurants(){
        List<Restaurant> allRestaurants = manager.getRestaurants();

        for(int i = 0; i < allRestaurants.size(); i++) {
            Restaurant currentRes = allRestaurants.get(i);
            LatLng restaurantLocation = new LatLng(currentRes.getLatitude(), currentRes.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(restaurantLocation);
            setMarkerHazardIcon(marker, currentRes);
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation));
        }

    }

    private void setMarkerHazardIcon(MarkerOptions marker, Restaurant currentRestaurant){

        String hazardString = currentRestaurant.getLatestInspectionHazard();
        BitmapDrawable bitmapDrawable;

        switch(hazardString){
            case("Low"):{
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.map_low);
                break;
            }
            case("Mid"):
            case("Moderate"):{
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.map_med);
                break;
            }
            case("High"):{
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.map_high);
                break;
            }
            default:{
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.map_low);
            }
        }

        //rescaling the image to better fit the map
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,75,75,false);
        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
    }
}
