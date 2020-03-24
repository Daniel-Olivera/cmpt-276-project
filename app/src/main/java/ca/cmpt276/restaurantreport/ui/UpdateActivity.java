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
    private TextView textView;
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

        textView = findViewById(R.id.textview);
        Button button = findViewById(R.id.praseButton);
        // request permission to use external storage
        requestPermission();

        try {
            jsonParse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateFlag = getUpdateFlagValue(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(updateFlag) {
                    case -1:
                        saveUpdateFlag(0);
                        ReadCSV readCSV = ReadCSV.getInstance(UpdateActivity.this,false);
                        startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                        break;
                    case 0:
                        if((timeForUpdate == true) && (newDataAvailable == true)){
                            FragmentManager askUpdateFragmentManager = getSupportFragmentManager();
                            AskForUpdateDialog askForUpdateDialog = new AskForUpdateDialog(csvUrl,reportUrl,UpdateActivity.this);
                            askForUpdateDialog.show(askUpdateFragmentManager, "ask_for_update_dialog");
                            //code snippet from https://stackoverflow.com/questions/15874117/how-to-set-delay-in-android
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    if(clickedUpdate){
                                        UpdateDialog dialog =new UpdateDialog();
                                        dialog.show(getSupportFragmentManager(),"UpdateDialog");

                                    }
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 3s = 3000ms
                                            if(!clickedCancel){
                                                LocalDateTime currentTime = LocalDateTime.now();
                                                String strCurrentTime = currentTime.toString();
                                                saveWhenLastUpdated(strCurrentTime);
                                            }
                                        }
                                    }, 2000);
                                }
                            }, 1000);
                            //startActivity(new Intent(UpdateActivity.this,MapsActivity.class));

                        }else{
                            ReadCSV readCSV1 = ReadCSV.getInstance(UpdateActivity.this,false);
                            startActivity(new Intent(UpdateActivity.this,MapsActivity.class));
                        }

                }
            }
        });


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
                            //testing
                            //textView.setText(dateModify);
                            // end
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            csvUrl = obj.getString("url");


                            // Copy data from the url to local file

                            // testing
                           // startActivity(new Intent(UpdateActivity.this,MapsActivity.class));



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
                            // testing
                            //textView.setText(dateModify);
                            //end
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            reportUrl = obj.getString("url");
                            System.out.println("report url is " + reportURL);
                            // request permission to use external storage
                            //requestPermission();
                            // Copy data from the url to local file

                            /*UpdateDialog dialog =new UpdateDialog();
                            dialog.show(getSupportFragmentManager(),"UpdateDialog");*/

                            /*for (int i = 0 ; i < 120;)
                            {
                                i = dialog.getProgressStatus();
                                System.out.println("progress status is " + i);
                                if( i >= 100) {
                                    startActivity(new Intent(UpdateActivity.this, MainActivity.class));
                                    break;
                                }
                            }
*/

                           //startActivity(new Intent(UpdateActivity.this,MapsActivity.class));



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
                if(hoursFromLastUpdate >= 20){
                    return true;
                }
            }
        }
        return true;
    }

    private boolean checkNewDataAvailable() {
        LocalDateTime lastModifiedRestaurants = LocalDateTime.parse(dateModifyRestaurants);
        LocalDateTime lastModifiedInspections = LocalDateTime.parse(dateModifyInspections);

        String lastUpdated = getWhenLastUpdated(UpdateActivity.this);

        System.out.println("last updated = " + lastUpdated);
        if(lastUpdated.equalsIgnoreCase("never")){
            return true;
        }
        LocalDateTime timeOfLastUpdate = LocalDateTime.parse(lastUpdated);

        return (lastModifiedInspections.isAfter(timeOfLastUpdate)) || (lastModifiedRestaurants.isAfter(timeOfLastUpdate));
    }

    static public String getWhenLastUpdated (Context context) {
        SharedPreferences sharedPreferencesLastUpdated = context.getSharedPreferences("Update_prefs",MODE_PRIVATE);
        return sharedPreferencesLastUpdated.getString("last updated","never");
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
        editor.putInt("update_flag_value",i);
        editor.apply();
    }
    static public int getUpdateFlagValue (Context context) {
        SharedPreferences sharedPreferencesUpdateFlag = context.getSharedPreferences("Update_flag_prefs",MODE_PRIVATE);
        return sharedPreferencesUpdateFlag.getInt("update_flag_value",-1);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(UpdateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(UpdateActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();

                }
                else {
                    // Permission Denied
                    Toast.makeText(UpdateActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public void writeData() {
        System.out.println("hihihihihihiih");

        String file = "restaurants_name";
        String content = "hahahahahah";
        try (FileOutputStream fos = this.openFileOutput(file, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }




        /*try {
            String file = "restaurants_name";

            System.out.println(1);
            File folder = new File(getExternalFilesDir(null), "TestingCSV.csv");
            OutputStream os = new FileOutputStream(folder);
            System.out.println(2);

            String string = "hahahahahahahah";
            os.write(string.getBytes());
            System.out.println(3);

            os.close();
        } catch (FileNotFoundException e) {            System.out.println(4);

            e.printStackTrace();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            ;            System.out.println(5);

            e.printStackTrace();
        }*/

        /*boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        System.out.println("" + var);


        final String filename = folder.toString() + "/" + "Test.csv";
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + filename); // Here csv file name is MyCsvFile.csv
        System.out.println(csv);
                CSVWriter writer = null;
                try {
                    writer = new CSVWriter(new FileWriter(csv));

                    List<String[]> data = new ArrayList<String[]>();
                    data.add(new String[]{"Country", "Capital"});
                    data.add(new String[]{"India", "New Delhi"});
                    data.add(new String[]{"United States", "Washington D.C"});
                    data.add(new String[]{"Germany", "Berlin"});

                    writer.writeAll(data); // data is adding to csv

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
    }

}
