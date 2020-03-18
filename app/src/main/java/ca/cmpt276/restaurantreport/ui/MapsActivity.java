package ca.cmpt276.restaurantreport.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

//https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial#get-the-code
/*
displays a google maps view showing the user where the restaurants are
and their current location
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private CameraPosition cameraPosition;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //default location of SFU Surrey
    private final LatLng defaultLocation = new LatLng(49.1867, 122.8494);
    private static final int DEFAULT_ZOOM = 15;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String LOCATION_KEY = "location";
    private static final String CAMERA_KEY = "camera_position";


    RestaurantManager manager = RestaurantManager.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(LOCATION_KEY);
        }
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch (NullPointerException e) {
            Log.d("MapView", "getMapAsync Returned null");
            e.printStackTrace();
        }

        setupListButton();
        ReadCSV.getInstance(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(CAMERA_KEY, mMap.getCameraPosition());
            outState.putParcelable(LOCATION_KEY, lastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        populateRestaurants();
        findDeviceLocation();
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void findDeviceLocation() {

        try {
            if (locationPermissionGranted) {
                Task locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                        } else {
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }


    private void populateRestaurants(){
        List<Restaurant> allRestaurants = manager.getRestaurants();

        for(int i = 0; i < allRestaurants.size(); i++) {
            Restaurant currentRes = allRestaurants.get(i);
            LatLng restaurantLocation = new LatLng(currentRes.getLatitude(), currentRes.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(restaurantLocation);
            setMarkerHazardIcon(marker, currentRes);
            mMap.addMarker(marker);
        }

    }

    private void setMarkerHazardIcon(MarkerOptions marker, Restaurant currentRestaurant){

        String hazardString = currentRestaurant.getLatestInspectionHazard();
        BitmapDrawable bitmapDrawable;

        switch(hazardString){
            case("Low"):{
                bitmapDrawable = (BitmapDrawable)getDrawable(R.drawable.map_low);
                break;
            }
            case("Mid"):
            case("Moderate"):{
                bitmapDrawable = (BitmapDrawable)getDrawable(R.drawable.map_med);
                break;
            }
            case("High"):{
                bitmapDrawable = (BitmapDrawable)getDrawable(R.drawable.map_high);
                break;
            }
            default:{
                bitmapDrawable = (BitmapDrawable)getDrawable(R.drawable.map_low);
                break;
            }
        }

        //rescaling the image to better fit the map
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,75,75,false);
        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
    }

    private void setupListButton() {
        Button listButton = findViewById(R.id.btnList);
        listButton.setOnClickListener(v -> {
            Intent intent = MainActivity.makeIntent(MapsActivity.this);
            startActivity(intent);
        });

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }
}
