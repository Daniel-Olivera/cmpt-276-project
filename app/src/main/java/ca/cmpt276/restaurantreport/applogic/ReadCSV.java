package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

        //testing
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        File gpxfile = new File(root, "MyTest.csv");
        InputStream is = null;
        try {
            is = new FileInputStream(gpxfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // end testing

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );
        String line = "";
        try {
            reader.readLine(); // to skip the first line with the name, addrs,
            while ((line = reader.readLine()) != null) {
                String [] tokens = line.split(",");
                for (int i = 0 ; i < 7 ; i++) {
                    tokens[i]= tokens[i].replace("\"", "");
                }
                double latitude = Double.parseDouble(tokens[5]);
                double longtitude = Double.parseDouble(tokens[6]);
                manager.add(new Restaurant(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        tokens[3],
                        tokens[4],
                        latitude,
                        longtitude
                ));
            }
        } catch (IOException e) {
            Log.e("Main Activity", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }
    }

    private void readInspectionData() {
        List<Inspection> inspectionList = new ArrayList<>(); //temporary inspectionList

        boolean update = true;
        //InputStream isInspection = context.getResources().openRawResource(R.raw.inspectionreports_itr1);


        if(update == true) {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            File gpxfile = new File(root, "MyTestReport.csv");

            InputStream isInspection = null;
            try {
                isInspection = new FileInputStream(gpxfile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


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
                    //String[] tokens = line.split(",");
                    System.out.println("line is " + line);
                    System.out.println("tokenns lengths = " + tokens.length);

                    //System.out.println("Before: token 5 is " + tokens[5]);
                    if (tokens.length == 5) {
                        // tao string[] moi xong copy paste sang
                        List<String> temp = new ArrayList<String>();
                        temp.addAll(Arrays.asList(tokens));
                        temp.add("0");
                        System.out.println("The list is " + temp);

                        inspectionList.add(new Inspection(
                                temp.get(0),
                                Integer.parseInt(temp.get(1)),
                                temp.get(2),
                                Integer.parseInt(temp.get(3)),
                                Integer.parseInt(temp.get(4)),
                                "Low"
                        ));

                            Inspection sampleInspection = inspectionList.get(inspectionIndex);
                            Violation newViolation = new Violation("", "", "");
                            sampleInspection.addNewViolation(newViolation);
                            inspectionList.set(inspectionIndex, sampleInspection);
                            inspectionIndex++;


                    } else if (tokens[5].isEmpty()) {
                        tokens[5] = "0";
                        inspectionList.add(new Inspection(
                                tokens[0],
                                Integer.parseInt(tokens[1]),
                                tokens[2],
                                Integer.parseInt(tokens[3]),
                                Integer.parseInt(tokens[4]),
                                tokens[6]
                        ));


                        //if the inspection contains any violations move on and split the entire violation string
                        //          into individual violations using the "|" delimiter
                        //
                        // further for the individual string of violation we use the "," delimiter to get the violation code, criticality and description

                        if (tokens.length > 6) {
                            String[] violationTokens = tokens[5].split("\\|");
                            Inspection sampleInspection = inspectionList.get(inspectionIndex);

                            for (String violation : violationTokens) {

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
                            }
                            inspectionList.set(inspectionIndex, sampleInspection);
                            inspectionIndex++;

                        } else {
                            Inspection sampleInspection = inspectionList.get(inspectionIndex);
                            Violation newViolation = new Violation("", "", "");
                            sampleInspection.addNewViolation(newViolation);
                            inspectionList.set(inspectionIndex, sampleInspection);
                            inspectionIndex++;
                        }
                    }
                    else {
                        inspectionList.add(new Inspection(
                                tokens[0].replace("\"", ""),
                                Integer.parseInt(tokens[1]),
                                tokens[2].replace("\"", ""),
                                Integer.parseInt(tokens[3]),
                                Integer.parseInt(tokens[4]),
                                tokens[6].replace("\"", "")
                        ));

                        //if the inspection contains any violations move on and split the entire violation string
                        //          into individual violations using the "|" delimiter
                        //
                        // further for the individual string of violation we use the "," delimiter to get the violation code, criticality and description

                        if (tokens.length > 6) {
                            String[] violationTokens = tokens[5].split("\\|");
                            Inspection sampleInspection = inspectionList.get(inspectionIndex);

                            for (String violation : violationTokens) {

                                String[] tokens2 = violation.split(",");

                                Violation newViolation = new Violation();
                                int i = 0;
                                for (String violationString : tokens2) {
                                    switch (i) {
                                        case 0:
                                            newViolation.setViolationCode(violationString.replace("\"", ""));
                                            i++;
                                            break;
                                        case 1:
                                            newViolation.setViolationCriticality(violationString.replace("\"", ""));
                                            i++;
                                            break;
                                        case 2:
                                            newViolation.setViolationDescriptor(violationString.replace("\"", ""));
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
                            Violation newViolation = new Violation("", "", "");
                            sampleInspection.addNewViolation(newViolation);
                            inspectionList.set(inspectionIndex, sampleInspection);
                            inspectionIndex++;
                        }

                    }
                }
            } catch (IOException e) {
                Log.e("Inspection Data", "Error Reading Data File on Line" + line, e);
                e.printStackTrace();
            }


            // cai nao  cung vao day
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


        else {
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
    }
    public static ReadCSV getInstance(Context context) {
        if(instance == null) {
            instance = new ReadCSV(context);
        }
        return instance;
    }
}
