package ca.cmpt276.restaurantreport.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;

public class UpdateActivity extends AppCompatActivity {

    private String csvUrl;
    private String reportUrl;
    private RequestQueue mQueue;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ProgressBar progressBar;
    private ProcessData processData;

    //MANAV
    private String dateModifyRestaurants;
    private String dateModifyInspections;
    boolean timeForUpdate;
    boolean newDataAvailable;
    int updateFlag;
    static public boolean clickedUpdate;
    static public boolean clickedCancel;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mQueue = Volley.newRequestQueue(this);

        updateFlag = getUpdateFlagValue(this);

        try {
            jsonParse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch(updateFlag) {
                    case -1:
                        saveUpdateFlag(0);
                        ReadCSV readCSV = new ReadCSV(UpdateActivity.this,false,-1);
                        startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                        break;
                    case 0:
                        if((timeForUpdate) && (newDataAvailable)){
                            FragmentManager askUpdateFragmentManager = getSupportFragmentManager();
                            AskForUpdateDialog askForUpdateDialog = new AskForUpdateDialog(csvUrl,reportUrl,UpdateActivity.this);
                            askForUpdateDialog.show(askUpdateFragmentManager, "ask_for_update_dialog");
                            //code snippet from https://stackoverflow.com/questions/15874117/how-to-set-delay-in-android
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(clickedUpdate) {
                                        UpdateDialog dialog = new UpdateDialog(UpdateActivity.this);
                                        dialog.show(getSupportFragmentManager(), "UpdateDialog");

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 3s = 3000ms
                                                if(!clickedCancel){
                                                    ReadCSV readCSV = new ReadCSV(UpdateActivity.this,true,1);
                                                    LocalDateTime currentTime = LocalDateTime.now();
                                                    String strCurrentTime = currentTime.toString();
                                                    saveWhenLastUpdated(strCurrentTime);
                                                    String dateLastSaved = getWhenLastUpdated(UpdateActivity.this);
                                                    ProcessData processData = new ProcessData();
                                                    processData.saveFinalCopy(UpdateActivity.this);

                                                    final Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Do something after 5s = 5000ms
                                                            startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                                                        }
                                                    }, 4000);
                                                }
                                                else{
                                                    startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                                                }
                                            }
                                        }, 4000);
                                    }
                                }
                            }, 3000);
                            final Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(!clickedUpdate){
                                        if(getWhenLastUpdated(UpdateActivity.this).equalsIgnoreCase("never")){
                                            ReadCSV readCSV1 = new ReadCSV(UpdateActivity.this,false,-1);
                                        }else{
                                            ReadCSV readCSV1 = new ReadCSV(UpdateActivity.this,false,0);
                                        }
                                        startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                                    }
                                }
                                }, 6000);


                        }else{
                            if(getWhenLastUpdated(UpdateActivity.this).equalsIgnoreCase("never")){
                                ReadCSV readCSV1 = new ReadCSV(UpdateActivity.this,false,-1);
                            }else{
                                ReadCSV readCSV1 = new ReadCSV(UpdateActivity.this,false,0);
                            }

                            startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                        }
                }

            }
        }, 3000);
    }

    private void jsonParse() throws IOException {

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
                            dateModifyRestaurants = jsonObject.getString("metadata_modified");
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            csvUrl = obj.getString("url");

                            // Copy data from the url to local file
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                timeForUpdate = checkTimeForUpdate();
                                newDataAvailable = checkNewDataAvailable();

                                }
                            }, 2000);

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
                            dateModifyInspections = jsonObject.getString("metadata_modified");
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            reportUrl = obj.getString("url");

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
            if(hoursFromLastUpdate >= 20){
                return true;
            }
        }
        return false;
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
        SharedPreferences sharedPreferencesLastUpdated = UpdateActivity.this.getSharedPreferences("Update_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesLastUpdated.edit();
        editor.putString("last_updated",lastUpdated);
        editor.apply();
    }

    private void saveUpdateFlag(int i) {
        SharedPreferences sharedPreferencesUpdateFlag = UpdateActivity.this.getSharedPreferences("Update_flag_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesUpdateFlag.edit();
        editor.putInt("update_flag_value1",i);
        editor.apply();
    }
    static public int getUpdateFlagValue (Context context) {
        SharedPreferences sharedPreferencesUpdateFlag = context.getSharedPreferences("Update_flag_prefs",MODE_PRIVATE);
        return sharedPreferencesUpdateFlag.getInt("update_flag_value1",-1);
    }

}
