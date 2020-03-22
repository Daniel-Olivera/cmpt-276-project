package ca.cmpt276.restaurantreport.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class UpdateActivity extends AppCompatActivity {

    private String dateModifyRestaurants;
    private String dateModifyInspections;
    private String csvUrl;
    private String reportUrl;
    private TextView textView;
    private RequestQueue mQueue;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean timeForUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        textView = findViewById(R.id.textview);
        Button button = findViewById(R.id.praseButton);

        mQueue = Volley.newRequestQueue(this);
        // request permission to use external storage
        requestPermission();
        try {
            jsonParse();
        } catch (IOException e) {
            e.printStackTrace();
        }

       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               FragmentManager fragManager = getSupportFragmentManager();
               UpdateProgressFragment dialog = new UpdateProgressFragment();
               dialog.show(fragManager,"updateDialog");

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
                            System.out.println("inside parse dateModifyRestaurants = " + dateModifyRestaurants);
                            //testing
                            textView.setText(dateModifyRestaurants);
                            // end
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            csvUrl = obj.getString("url");


                            // Copy data from the url to local file


                            // testing
                            //startActivity(new Intent(UpdateActivity.this,MainActivity.class));
                            timeForUpdate = checkTimeForUpdate();
                            //newDataAvailable = checkNewDataAvailable();


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
                            System.out.println("insdie json parse dateModifyInsepctiions = " + dateModifyInspections);
                            // testing
                            textView.setText(dateModifyInspections);
                            //end
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            reportUrl = obj.getString("url");
                            // request permission to use external storage
                            //requestPermission();

                            // Copy data from the url to local file


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

        if(lastUpdated.equalsIgnoreCase("")){
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
        return true;
    }

    static public String getWhenLastUpdated (Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Update_prefs",MODE_PRIVATE);
        return sharedPreferences.getString("last updated","");
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
