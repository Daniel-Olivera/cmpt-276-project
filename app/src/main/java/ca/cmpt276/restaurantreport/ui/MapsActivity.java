package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.MapInfoWindowAdapter;
import ca.cmpt276.restaurantreport.applogic.CustomClusterRenderer;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

/*
displays a google maps view showing the user where the restaurants are
and their current location
 */
public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private Algorithm clusterManagerAlgorithm;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 10;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String LOCATION_KEY = "location";
    private static final String CAMERA_KEY = "camera_position";
    View mapView;
    //SFU Surrey Campus
    private final LatLng SFU_SURREY = new LatLng(49.1864, -122.8483);
    //change camera animation speed, lower number = higher speed
    private final int UPDATE_CAM_SPEED = 300;
    boolean favouritesReadFromFile = false;

    private ClusterManager clusterManager;
    RestaurantManager manager;
    List<Restaurant> allRestaurants;

    public MapsActivity() {
    }


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
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
        } catch (NullPointerException e) {
            Log.d("MapView", "getMapAsync Returned null");
            e.printStackTrace();
        }

        if (mapView == null) {
            mapView = mapFragment.getView();
        }

        manager = RestaurantManager.getInstance(this);
        setupListButton();
        setupSearchButton();
    }

    @Override
    protected void onResume(){
        super.onResume();
        allRestaurants = manager.getRestaurants();
        if(clusterManager != null){
            mMap.clear();
            populateRestaurants();
        }
    }

    private void setupSearchButton() {
        FloatingActionButton mapSearchButton = mapView.findViewById(R.id.btnMapSearch);
        mapSearchButton.setOnClickListener(v -> {
            Intent intent = SearchActivity.makeIntent(MapsActivity.this);
            intent.putExtra("calling activity","maps activity");
            startActivity(intent);
        });

    }

    private void findAndShowMarker(String trackingID) {
        @SuppressWarnings("unchecked")
        Collection<Restaurant> restaurantMarkers = clusterManagerAlgorithm.getItems();

        for (Restaurant restaurant : restaurantMarkers) {
            String markerSnippet = restaurant.getTrackingNum();
            if (markerSnippet.equals(trackingID)) {

                LatLng restaurantPos = new LatLng(restaurant.getLatitude(),
                        restaurant.getLongitude());

                CameraUpdate updateCam = CameraUpdateFactory.newLatLngZoom(restaurantPos, 20);
                mMap.moveCamera(updateCam);
                break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                findDeviceLocation();
            }
        }
        updateLocationUI();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        moveRealignButton();

        populateRestaurants();

        //executed only once
        if(!favouritesReadFromFile) {
            manager.readFavoriteList();
            favouritesReadFromFile = true;
        }

        getLocationPermission();

        updateLocationUI();
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(this);

        if(getIntent().getStringExtra("trackingID") != null){
            String trackingID = getIntent().getStringExtra("trackingID");
            findAndShowMarker(trackingID);
        }
        else{
            findDeviceLocation();
        }

        boolean isRestaurantsUpdated = getIntent().getBooleanExtra("updatedFavouritesTag",
                false);
        if(isRestaurantsUpdated && !manager.getFavoriteRestaurantList().isEmpty()){
            showUpdatedFavourites();
        }

        clusterManager.setOnClusterClickListener(cluster -> {
            CameraUpdate updateCam = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                    DEFAULT_ZOOM);

            mMap.animateCamera(updateCam,UPDATE_CAM_SPEED,null);

            return true;
        });
    }

    private void showUpdatedFavourites() {
        NewStarredInspections newStarredInspections = new NewStarredInspections(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        newStarredInspections.show(fragmentManager, "newStarredInspections");
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
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void findDeviceLocation() {

        try {
            if (locationPermissionGranted) {
                Task locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = (Location) task.getResult();
                            assert lastKnownLocation != null;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                     new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }
                    else {
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }

    }

    private void populateRestaurants(){

        clusterManager = new ClusterManager(this, mMap);
        clusterManagerAlgorithm = new NonHierarchicalDistanceBasedAlgorithm();
        clusterManager.setAlgorithm(clusterManagerAlgorithm);

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        for(int i = 0; i < allRestaurants.size(); i++) {
            Restaurant currentRes = allRestaurants.get(i);
            clusterManager.addItem(currentRes);
        }
        CustomClusterRenderer customClusterRenderer = new CustomClusterRenderer(this, mMap, clusterManager);
        clusterManager.setRenderer(customClusterRenderer);


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        String trackingNum = marker.getSnippet();
        //don't want intent to be null so by default send user back to map
        Intent intent = MapsActivity.makeIntent(this);

        for(int i = 0; i < allRestaurants.size(); i++){
            if(trackingNum.equals(allRestaurants.get(i).getTrackingNum())) {
                Restaurant currentRes = allRestaurants.get(i);

                intent = RestaurantActivity.makeIntent(this, trackingNum);
                break;
            }
        }

        startActivity(intent);
    }

    private void setupListButton() {
        Button listButton = findViewById(R.id.btnList);
        listButton.setOnClickListener(v -> {
            Intent intent = MainActivity.makeIntent(MapsActivity.this);
            startActivity(intent);
        });
    }

    private void moveRealignButton(){
        //access realign button
        ViewGroup parent = (ViewGroup) mapView.findViewById(Integer.parseInt("1")).getParent();
        View realignBtn = parent.getChildAt(4);
        //position realign button
        RelativeLayout.LayoutParams realignBtnParams = (RelativeLayout.LayoutParams) realignBtn.getLayoutParams();
        realignBtnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        realignBtnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        realignBtnParams.setMargins(0, 180, 180, 0);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }
}
