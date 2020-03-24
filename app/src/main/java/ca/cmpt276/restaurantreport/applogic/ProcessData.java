package ca.cmpt276.restaurantreport.applogic;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

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

import ca.cmpt276.restaurantreport.ui.MainActivity;
import ca.cmpt276.restaurantreport.ui.UpdateActivity;

public class ProcessData {
    int currentLine;
    int totalLine;
    int percentage;
    int totalRestaurant;



    public void readRestaurantData(String data, Context context) {
        try {
            System.out.println(data);
            URL url = new URL(data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String current;
            List<String[]> restaurants = new ArrayList<String[]>();

            while ((current = in.readLine()) != null) {
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
            saveRestaurantData(restaurants, context);
            System.out.println("hahahahhahaahahha");
        } catch (Exception e) {
            System.out.println("Big offf");

            e.printStackTrace();
        }
    }

    private void saveRestaurantData(List<String[]> restaurants, Context context) {
        try {
            File root = context.getDir("RestaurantReport", Context.MODE_PRIVATE);

            boolean isDirectoryCreated = root.mkdirs();

            if(isDirectoryCreated)
                System.out.println("Directory created successfully");
            else
                System.out.println("Directory was not created successfully");

            File gpxfile = new File(root, "RestaurantDetails.csv");
            CSVWriter writer = new CSVWriter(new FileWriter(gpxfile));
            writer.writeAll(restaurants);
            percentage = 10;
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readReportData(String data, Context context)
    {
        try {
            System.out.println(data);
            URL url = new URL(data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String current;
            List<String[]> reports = new ArrayList<String[]>();
            List<String> name = new ArrayList<String>();

            // Re-open the file to read it

            while ((current = in.readLine()) != null) {
                if(!current.equals(",,,,,,"))
                {
                    name.add(current);
                }

            }
            in.close();
            saveReportData(name, context);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void saveReportData(List<String> reports, Context context) {
        try {
            /*File root = context.getDir("RestaurantReport", Context.MODE_PRIVATE);
            if (!root.exists()) {
                root.mkdirs();
            }*/
            File root = context.getDir("RestaurantReport", Context.MODE_PRIVATE);

            boolean isDirectoryCreated = root.mkdirs();

            if(isDirectoryCreated)
                System.out.println("Directory created successfully");
            else
                System.out.println("Directory was not created successfully");
            File gpxfile = new File(root, "RestaurantReports.csv");
            FileWriter writer = new FileWriter(gpxfile);
           for(int i =0 ;i < reports.size();i++)
           {
               currentLine++;
               writer.write(reports.get(i) + "\n");
           }

            writer.flush();
            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getCurrentLine() {
        return currentLine;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public int getPercentage()
    {
        if (totalLine <= 10000)
        {
            return 0;
        }
        percentage = (currentLine+totalRestaurant) / totalLine * 100;
        return  percentage;
    }


}




