package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restaurantreport.R;

/*
//Singleton Class to read the CSV files and populate the manager class with the restaurants and it's inspections
 */
public class ReadCSV {
    private RestaurantManager manager;
    private Context context;

    @SuppressLint("StaticFieldLeak")
    private static ReadCSV instance;

    private ReadCSV(Context context) {
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
                        tokens[0].replace("\"",""),
                        tokens[1].replace("\"",""),
                        tokens[2].replace("\"",""),
                        tokens[3].replace("\"",""),
                        tokens[4].replace("\"",""),
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
            inspectionReader.readLine();// skip the first line of the titles

            int inspectionIndex = 0; // index to obtain individual inspections from the temporary inspectionList above
            while ((line = inspectionReader.readLine()) != null) {
                /*
                splitting using "" and ,   e.g string = "Name","Address","HazardLevel","violation1, violation2, violation3"
                  we will end up getting
                  Name
                  Address
                  HazardLevel
                  violation1, violation2, violation3

                  then further splitting violations using comma to get separate violations
                 */
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                inspectionList.add(new Inspection(
                        tokens[0].replace("\"",""),
                        Integer.parseInt(tokens[1]),
                        tokens[2].replace("\"",""),
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        tokens[5].replace("\"","")
                ));

                //if the inspection contains any violations move on and split the entire violation string
                //          into individual violations using the "|" delimiter
                //
                // further for the individual string of violation we use the "," delimiter to get the violation code, criticality and description

                if(tokens.length > 6) {
                    String[] violationTokens = tokens[6].split("\\|");
                    Inspection sampleInspection = inspectionList.get(inspectionIndex);

                    for (String violation : violationTokens) {

                        String[] tokens2 = violation.split(",");

                        Violation newViolation = new Violation();
                        int i = 0;
                        for (String violationString : tokens2) {
                            switch (i) {
                                case 0:
                                    newViolation.setViolationCode(violationString.replace("\"",""));
                                    i++;
                                    break;
                                case 1:
                                    newViolation.setViolationCriticality(violationString.replace("\"",""));
                                    i++;
                                    break;
                                case 2:
                                    newViolation.setViolationDescriptor(violationString.replace("\"",""));
                                    i++;
                                    break;
                                default:
                                    i = 0;
                                    break;
                            }
                        }
                        sampleInspection.addNewViolation(newViolation);
                    }
                    inspectionList.set(inspectionIndex, sampleInspection);
                    inspectionIndex++;

                } else {
                    Inspection sampleInspection = inspectionList.get(inspectionIndex);
                    Violation newViolation = new Violation("","","");
                    sampleInspection.addNewViolation(newViolation);
                    inspectionList.set(inspectionIndex, sampleInspection);
                    inspectionIndex++;
                }
            }
        } catch (IOException e) {
            Log.e("Inspection Data", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }

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
