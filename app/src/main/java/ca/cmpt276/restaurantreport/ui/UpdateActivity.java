package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

public class UpdateActivity extends AppCompatActivity {

    private String csvUrl;
    private String reportUrl;
    private RequestQueue mQueue;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ProgressBar progressBar;
    private ProcessData processData;
    private ReadCSV readCSV;

    //MANAV
    private String dateModifyRestaurants;
    private String dateModifyInspections;
    boolean timeForUpdate;
    boolean newDataAvailable;
    int updateFlag;
    static public boolean clickedUpdate;
    static public boolean clickedCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mQueue = Volley.newRequestQueue(this);

        updateFlag = getUpdateFlagValue(this);

        if(!isOnline()){
            Intent intent = MapsActivity.makeIntent(this);
            startActivity(intent);
        }
        else{
            try {
                jsonParse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isOnline() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        if ( cm.getActiveNetworkInfo() != null ) {
            connected = true;
        }
        return connected;
    }

    private void jsonParse() throws IOException {

        String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
        String reportURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
        // Process restaurant data for restaurantURL
        JsonObjectRequest restaurantRequest = new JsonObjectRequest(Request.Method.GET, restaurantURL, null,
                response -> {
                    try {
                        // get object name result
                        JSONObject jsonObject = response.getJSONObject("result");
                        // get date modify
                        dateModifyRestaurants = jsonObject.getString("metadata_modified");
                        // get the url to download csv file
                        JSONArray res = jsonObject.getJSONArray("resources");
                        JSONObject obj = res.getJSONObject(0);
                        csvUrl = obj.getString("url");

                        // Copy data from the url to local file
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            // Do something after 5s = 5000ms
                        timeForUpdate = checkTimeForUpdate();
                        newDataAvailable = checkNewDataAvailable();
                        askUserForUpdate();

                        }, 2000);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        // Get request
        mQueue.add(restaurantRequest);

        // Process report data from reportURL
        JsonObjectRequest reportRequest = new JsonObjectRequest(Request.Method.GET, reportURL, null,
                response -> {
                    try {
                        // get object name result
                        JSONObject jsonObject = response.getJSONObject("result");
                        // get date modify
                        dateModifyInspections = jsonObject.getString("metadata_modified");
                        // get the url to download csv file
                        JSONArray res = jsonObject.getJSONArray("resources");
                        JSONObject obj = res.getJSONObject(0);
                        reportUrl = obj.getString("url");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mQueue.add(reportRequest);
    }

    private boolean checkTimeForUpdate() {

        String lastUpdated = getWhenLastUpdated(UpdateActivity.this);

        if(lastUpdated.equalsIgnoreCase("never")){
            return true;
        }else{
            LocalDateTime timeOfLastUpdate = LocalDateTime.parse(lastUpdated);
            LocalDateTime currentDateAndTime = LocalDateTime.now();

            Duration diffLastUpdateAndNow = Duration.between(timeOfLastUpdate,currentDateAndTime);

            long daysFromLastUpdate = diffLastUpdateAndNow.toDays();
            diffLastUpdateAndNow = diffLastUpdateAndNow.minusDays(daysFromLastUpdate);
            long hoursFromLastUpdate = diffLastUpdateAndNow.toHours();
            diffLastUpdateAndNow = diffLastUpdateAndNow.minusHours(hoursFromLastUpdate);

            if(daysFromLastUpdate > 0) {
                return true;
            }
            return hoursFromLastUpdate >= 20;
        }
    }

    private boolean checkNewDataAvailable() {
        LocalDateTime lastModifiedRestaurants = LocalDateTime.parse(dateModifyRestaurants);
        LocalDateTime lastModifiedInspections = LocalDateTime.parse(dateModifyInspections);

        String lastUpdated = getWhenLastUpdated(UpdateActivity.this);

        if(lastUpdated.equalsIgnoreCase("never")){
            return true;
        }
        LocalDateTime timeOfLastUpdate = LocalDateTime.parse(lastUpdated);
        return false;
    }

    static public String getWhenLastUpdated (Context context) {
        SharedPreferences sharedPreferencesLastUpdated = context.getSharedPreferences("Update_prefs",MODE_PRIVATE);
        return sharedPreferencesLastUpdated.getString("last_updated","never");
    }

    private void saveWhenLastUpdated(String lastUpdated) {
        SharedPreferences sharedPreferencesLastUpdated = UpdateActivity.this.
                getSharedPreferences("Update_prefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferencesLastUpdated.edit();
        editor.putString("last_updated",lastUpdated);
        editor.apply();
    }

    private void saveUpdateFlag(@SuppressWarnings("SameParameterValue") int i) {
        SharedPreferences sharedPreferencesUpdateFlag = UpdateActivity.this
                .getSharedPreferences("Update_flag_prefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferencesUpdateFlag.edit();
        editor.putInt("update_flag_value1",i);
        editor.apply();
    }
    static public int getUpdateFlagValue (Context context) {
        SharedPreferences sharedPreferencesUpdateFlag = context.getSharedPreferences("Update_flag_prefs",MODE_PRIVATE);
        return sharedPreferencesUpdateFlag.getInt("update_flag_value1",-1);
    }

    private void askUserForUpdate() {

        RestaurantManager manager = RestaurantManager.getInstance(this);
        FragmentManager askUpdateFragmentManager = getSupportFragmentManager();
        AskForUpdateDialog askForUpdateDialog = new AskForUpdateDialog(csvUrl, reportUrl,
                UpdateActivity.this);

        switch (updateFlag) {
            case -1: {
                saveUpdateFlag(0);
                readCSV = new ReadCSV(this, false, -1);
                startActivity(new Intent(this, MapsActivity.class));
                break;
            }
            case 0: {
                if (timeForUpdate && newDataAvailable) {
                    askForUpdateDialog.show(askUpdateFragmentManager, "ask_for_update_dialog");
            }
                else {
                    if(manager.getRestaurants().isEmpty()) {
                        readCSV = new ReadCSV(this, false, 0);
                    }
                    startActivity(new Intent(this, MapsActivity.class));
                }
                break;
            }
        }
    }
}

