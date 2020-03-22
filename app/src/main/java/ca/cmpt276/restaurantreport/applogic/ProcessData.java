package ca.cmpt276.restaurantreport.applogic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProcessData {

    public void readRestaurantData(String data) {
        try {
            URL url = new URL(data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String current;
            List<String[]> restaurants = new ArrayList<String[]>();
            //List<String> name = new ArrayList<String>();
            while ((current = in.readLine()) != null) {// name.add(current);

                String[] row = current.split(",");

                if(row.length == 8)
                {
                    row[1] = row[1] + row[2];
                    row[1] = row[1].replace("\"", "");
                    row[2] = row[3];
                    row[3] = row[4];
                    row[4] = row[5];
                    row[5] = row[6];
                    row[6] = row[7];
                }
                restaurants.add(row);
            }
            in.close();
            saveRestaurantData(restaurants);
        } catch (Exception e) {
            System.out.println("Big offf");

            e.printStackTrace();
        }
    }

    private void saveRestaurantData(List<String[]> restaurants) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            String state = Environment.getExternalStorageState();

            File gpxfile = new File(root, "MyTest.csv");
            CSVWriter writer = new CSVWriter(new FileWriter(gpxfile));
            writer.writeAll(restaurants);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readReportData(String data)
    {
        try {
            URL url = new URL(data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String current;
            List<String[]> reports = new ArrayList<String[]>();
            List<String> name = new ArrayList<String>();
            while ((current = in.readLine()) != null) {
                if(!current.equals(",,,,,,"))
                {
                    name.add(current);
                }



                //String[] row = current.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);


                //reports.add(row);
            }
            in.close();
            saveReportData(name);
        } catch (Exception e) {
            System.out.println("Big offf");

            e.printStackTrace();
        }


    }

    private void saveReportData(List<String> reports) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            String state = Environment.getExternalStorageState();
            File gpxfile = new File(root, "MyTestReport.csv");
            //testing
            File file = new File(root, "TestingFile.txt");
           /* CSVWriter writer = new CSVWriter(new FileWriter(gpxfile));
            writer.writeAll(reports);
            writer.flush();
            writer.close();*/
           FileWriter writer = new FileWriter(gpxfile);
           FileWriter writter1 = new FileWriter(file);
           for(int i = 0 ; i < 30 ; i++)
           {
               writter1.write(reports.get(i) + "\n");

           }
           for(int i =0 ;i < reports.size();i++)
           {
               writer.write(reports.get(i) + "\n");
           }
           writter1.flush();
           writter1.close();
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}




