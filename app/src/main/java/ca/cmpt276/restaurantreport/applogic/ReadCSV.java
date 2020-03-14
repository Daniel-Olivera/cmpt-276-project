package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ContentHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restaurantreport.R;

/*
//Singleton Class to read the CSV files and populate the manager class with the restaurants and it's inspections
 */
public class ReadCSV {
    RestaurantManager manager;
    Context context;

    @SuppressLint("StaticFieldLeak")
    private static ReadCSV instance;


    public ReadCSV(Context context) {
        this.context = context;
        readRestaurantData();
        readInspectionData();
    }

    private void readRestaurantData() {
        manager = RestaurantManager.getInstance(context);

        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );
        String line = "";
        try {
            reader.readLine(); // to skip the first line with the name, addrs,
            while ((line = reader.readLine()) != null) {

                String[] tokens = line.split(",");

                // makes a new restaurant from the tokens array we read from the file
                //and adds it to the restaurantList
                manager.add(new Restaurant(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        tokens[3],
                        tokens[4],
                        Double.parseDouble(tokens[5]),
                        Double.parseDouble(tokens[6])
                ));
            }
        } catch (IOException e) {
            Log.e("Main Activity", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }
    }

    private void readInspectionData() {

        List<Inspection> inspectionList = new ArrayList<>(); //temporary inspectionList

        InputStream isInspection = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader inspectionReader = new BufferedReader(
                new InputStreamReader(isInspection, StandardCharsets.UTF_8)
        );
        String line = "";
        try {
            inspectionReader.readLine();
            while ((line = inspectionReader.readLine()) != null) {
                String[] tokens = line.split(",");

                inspectionList.add(new Inspection(
                        tokens[0],
                        Integer.parseInt(tokens[1]),
                        tokens[2],
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        tokens[5]
                ));
            }
        } catch (IOException e) {
            Log.e("Inspection Data", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }

        //reading violations from the separated_values_2 file and adding the violations to the inspections in the above inspectionList
        InputStream isViolation = context.getResources().openRawResource(R.raw.separated_values_2);
        BufferedReader violationReader = new BufferedReader(
                new InputStreamReader(isViolation, StandardCharsets.UTF_8)
        );
        line = "";
        try {
            violationReader.readLine();
            int index = 0;

            while ((line = violationReader.readLine()) != null) {
                String[] tokens = line.split("\\|");

                Inspection sampleInspection = inspectionList.get(index);
                for (String violation : tokens) {

                    String[] tokens2 = violation.split(",");

                    Violation newViolation = new Violation();
                    int i = 0;
                    for (String violationString : tokens2) {
                        switch (i) {
                            case 0:
                                newViolation.setViolationCode(violationString);
                                i++;
                                break;
                            case 1:
                                newViolation.setViolationCriticality(violationString);
                                i++;
                                break;
                            case 2:
                                newViolation.setViolationDescriptor(violationString);
                                i++;
                                break;
                            default:
                                i = 0;
                                break;
                        }
                    }
                    sampleInspection.addNewViolation(newViolation);
//
                }
                inspectionList.set(index, sampleInspection);
                index++;
            }
        } catch (IOException e) {
            Log.e("Violation Data", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }

        //for each inspection in the temporary inspectionList match the tracking number to the restaurant in the
        //restaurantList and add the inspection to the restaurant

        List<Restaurant> restaurantList = manager.getRestaurants();
        int restaurantListIndex = 0;
        for (Restaurant tempRestaurant : restaurantList) {
            for (Inspection inspection : inspectionList) {
                if (tempRestaurant.getTrackingNum().equals(inspection.getTrackingNum())) {
                    tempRestaurant.addInspection(inspection);
                }
            }

            manager.set(restaurantListIndex, tempRestaurant);
            restaurantListIndex++;
        }
    }

    public static ReadCSV getInstance(Context context) {
        if(instance == null) {
            instance = new ReadCSV(context);
        }
        return instance;
    }
}
