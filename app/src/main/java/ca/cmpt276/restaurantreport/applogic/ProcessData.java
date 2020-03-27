package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ProcessData {
    int currentLine;
    int totalLine;
    int totalRestaurant;



    public void readRestaurantData(String data, Context context) {
        try {
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
            connection.disconnect();
            saveRestaurantData(restaurants, context);
        } catch (Exception e) {
            Log.e("ProcessData","Failed to read restaurant data");

            e.printStackTrace();
        }
    }

    private void saveRestaurantData(List<String[]> restaurants, Context context) {
        try {
            File root = context.getDir("RestaurantReport", Context.MODE_PRIVATE);

            boolean isDirectoryCreated = root.mkdirs();

            if(isDirectoryCreated)
                Log.d("saveRestaurantData","Directory created successfully");
            else
                Log.d("saveRestaurantData","Directory was not created successfully");

            File gpxfile = new File(root, "RestaurantDetails.csv");
            CSVWriter writer = new CSVWriter(new FileWriter(gpxfile));
            writer.writeAll(restaurants);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readReportData(String data, Context context)
    {
        try {
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
            connection.disconnect();
            saveReportData(name, context);
        } catch (Exception e) {
            Log.e("readReportData","Failed to read report data");
            e.printStackTrace();
        }


    }

    private void saveReportData(List<String> reports, Context context) {
        try {

            File root = context.getDir("RestaurantReport", Context.MODE_PRIVATE);

            boolean isDirectoryCreated = root.mkdirs();

            if(isDirectoryCreated)
                Log.d("saveReportData","Directory created successfully");
            else
                Log.d("saveReportData","Directory was not created successfully");
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

    public void saveFinalCopy(Context context)
    {
        File root = context.getDir("RestaurantReport", Context.MODE_PRIVATE);

        File source1 = new File(root, "RestaurantDetails.csv");
        File dest1 = new File(root, "FinalRestaurantDetails.csv");
        File source2 = new File(root, "RestaurantReports.csv");
        File dest2 = new File(root, "FinalRestaurantReports.csv");

        FileChannel sourceChannel1 = null;
        FileChannel destChannel1 = null;
        FileChannel sourceChannel2 = null;
        FileChannel destChannel2 = null;
        try {
            sourceChannel1 = new FileInputStream(source1).getChannel();
            destChannel1 = new FileOutputStream(dest1).getChannel();
            destChannel1.transferFrom(sourceChannel1, 0, sourceChannel1.size());

            sourceChannel2 = new FileInputStream(source2).getChannel();
            destChannel2 = new FileOutputStream(dest2).getChannel();
            destChannel2.transferFrom(sourceChannel2, 0, sourceChannel2.size());

            sourceChannel1.close();
            destChannel1.close();
            sourceChannel2.close();
            destChannel2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}




