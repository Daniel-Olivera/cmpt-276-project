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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.RequestQueue;
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
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.MapInfoWindowAdapter;
import ca.cmpt276.restaurantreport.applogic.CustomClusterRenderer;
import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

//https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial#get-the-code
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

    //Mike
    private String dateModify;
    private String csvUrl;
    private String reportUrl;
    private TextView textView;
    private RequestQueue mQueue;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ProgressBar progressBar;
    private ProcessData processData;
    //////

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
        //MIKE
        // request permission to use external storage
       /* requestPermission();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mQueue = Volley.newRequestQueue(this);

        try {
            jsonParse();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ///

        manager = RestaurantManager.getInstance(this);
        //ReadCSV.getInstance(this,true);
        allRestaurants = manager.getRestaurants();
        setupListButton();
    }

    private void findAndShowMarker(String trackingID) {
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
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
        /*switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MapsActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();

                }
                else {
                    // Permission Denied
                    Toast.makeText(MapsActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        moveRealignButton();
        populateRestaurants();

        updateLocationUI();
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String trackingID = extras.getString("trackingID");
            findAndShowMarker(trackingID);
        }
        else
        {
            //for testing
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SFU_SURREY,DEFAULT_ZOOM));
            findDeviceLocation();
        }

        clusterManager.setOnClusterClickListener(cluster -> {
            CameraUpdate updateCam = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                    DEFAULT_ZOOM);

            mMap.animateCamera(updateCam,UPDATE_CAM_SPEED,null);

            return true;
        });
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
                String resName = currentRes.getName();
                int issueCount = currentRes.getTotalIssues();
                String totalIssues = Integer.toString(issueCount);

                intent = RestaurantActivity.makeIntent(this, resName, totalIssues);
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

    //MIKE
   /* private void jsonParse() throws IOException {

        String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
        String reportURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
        // Process restaurant data for restaurantURL
        JsonObjectRequest restaurantRequest = new JsonObjectRequest(Request.Method.GET, restaurantURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // get object name result
                            JSONObject jsonObject = response.getJSONObject("result");
                            // get date modify
                            dateModify = jsonObject.getString("metadata_modified");

                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            csvUrl = obj.getString("url");


                            // Copy data from the url to local file
                            processData = processData.getInstance();
                            processData.readRestaurantData(csvUrl, MapsActivity.this);

                            // testing
                            //startActivity(new Intent(UpdateActivity.this,MainActivity.class));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Get request
        mQueue.add(restaurantRequest);

        // Process report data from reportURL
        JsonObjectRequest reportRequest = new JsonObjectRequest(Request.Method.GET, reportURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // get object name result
                            JSONObject jsonObject = response.getJSONObject("result");

                            // get date modify
                            dateModify = jsonObject.getString("metadata_modified");

                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            reportUrl = obj.getString("url");
                            System.out.println("report url is " + reportURL);

                            // Copy data from the url to local file
                            processData = processData.getInstance();
                            processData.readReportData(reportUrl, MapsActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(reportRequest);

       *//* UpdateDialog dialog =new UpdateDialog();
        dialog.show(getSupportFragmentManager(),"UpdateDialog");*//*




    }
    //MIKEE
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(MapsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
   *//* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MapsActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();

                }
                else {
                    // Permission Denied
                    Toast.makeText(MapsActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/
}
