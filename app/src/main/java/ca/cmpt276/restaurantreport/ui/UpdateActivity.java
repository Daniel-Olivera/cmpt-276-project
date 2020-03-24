package ca.cmpt276.restaurantreport.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ProcessData;

public class UpdateActivity extends AppCompatActivity {

    private String dateModify;
    private String csvUrl;
    private String reportUrl;
    private TextView textView;
    private RequestQueue mQueue;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ProgressBar progressBar;
    private ProcessData processData;



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
        //requestPermission();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    jsonParse();
                } catch (IOException e) {
                    e.printStackTrace();
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
                            dateModify = jsonObject.getString("metadata_modified");
                            //testing
                            //textView.setText(dateModify);
                            // end
                            // get the url to download csv file
                            JSONArray res = jsonObject.getJSONArray("resources");
                            JSONObject obj = res.getJSONObject(0);
                            csvUrl = obj.getString("url");


                            // Copy data from the url to local file
                            ProcessData processData  = new ProcessData();
                            processData.readRestaurantData(csvUrl, UpdateActivity.this);

                            // testing
                            startActivity(new Intent(UpdateActivity.this,MapsActivity.class));



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
                            ProcessData processData = new ProcessData();
                            processData.readReportData(reportUrl, UpdateActivity.this);

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








                            startActivity(new Intent(UpdateActivity.this,MapsActivity.class));



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

        UpdateDialog dialog =new UpdateDialog();
        dialog.show(getSupportFragmentManager(),"UpdateDialog");




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
