package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
/*
Singleton Class to read the CSV files and populate the manager class with the restaurants and it's inspections
*/
public class ReadCSV {
    private RestaurantManager manager;
    private Context context;
    private boolean updateAvailable;
    private int flag;

    public ReadCSV(Context context) {
        manager = RestaurantManager.getInstance(context);
    }

    public void getCSVData(Context context, boolean updateAvailable, int flag){
        this.context = context;
        this.updateAvailable = updateAvailable;
        this.flag = flag;

        if (manager.getRestaurants().isEmpty() || updateAvailable) {
            readRestaurantData();
            readInspectionData();
        }
    }

    private void readRestaurantData() {
        if(updateAvailable){
            File root = context.getDir("RestaurantReport",Context.MODE_PRIVATE);
            File gpxfile = new File(root,"RestaurantDetails.csv");
            InputStream is = null;

            try {
                is = new FileInputStream(gpxfile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            assert is != null;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8)
            );

            String line = "";
            try {
                reader.readLine();
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
                Log.e("ReadCSV", "Error Reading Data File on Line" + line, e);
                e.printStackTrace();
            }
        }else{
            switch(flag){
                case -1:
                    InputStream isInspection = context.getResources().openRawResource(R.raw.restaurants_itr1);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(isInspection, StandardCharsets.UTF_8)
                    );
                    String line = "";
                    try {
                        reader.readLine();
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
                        Log.e("ReadCSV", "Error Reading Data File on Line" + line, e);
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    File root = context.getDir("RestaurantReport",Context.MODE_PRIVATE);
                    File gpxfile = new File(root,"FinalRestaurantDetails.csv");
                    InputStream is = null;
                    try {
                        is = new FileInputStream(gpxfile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    assert is != null;
                    BufferedReader reader2 = new BufferedReader(
                            new InputStreamReader(is, StandardCharsets.UTF_8)
                    );

                    String line2 = "";
                    try {
                        reader2.readLine();
                        while ((line2 = reader2.readLine()) != null) {
                            String [] tokens = line2.split(",");
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
                        Log.e("ReadCSV", "Error Reading Data File on Line" + line2, e);
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e("ReadCSV","restaurant data not read due to flag being wrong");
                    break;
            }
        }
    }

    private void readInspectionData() {
        List<Inspection> inspectionList = new ArrayList<>();
        if(updateAvailable) {
            File root = context.getDir("RestaurantReport",Context.MODE_PRIVATE);
            File gpxfile = new File(root,"RestaurantReports.csv");

            InputStream isInspection = null;
            try {
                isInspection = new FileInputStream(gpxfile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader inspectionReader;
            assert isInspection != null;
            inspectionReader = new BufferedReader(
                    new InputStreamReader(isInspection, StandardCharsets.UTF_8)
            );

            String line = "";
            try {
                inspectionReader.readLine();

                int inspectionIndex = 0;
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

                    if (tokens.length == 5) {
                        List<String> temp = new ArrayList<>(Arrays.asList(tokens));
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
                    }else if (tokens[5].isEmpty()) {
                        tokens[5] = "0";
                        inspectionList.add(new Inspection(
                                tokens[0],
                                Integer.parseInt(tokens[1]),
                                tokens[2],
                                Integer.parseInt(tokens[3]),
                                Integer.parseInt(tokens[4]),
                                tokens[6]
                        ));

                        Inspection sampleInspection = inspectionList.get(inspectionIndex);
                        Violation newViolation = new Violation("", "", "");
                        sampleInspection.addNewViolation(newViolation);
                        inspectionList.set(inspectionIndex, sampleInspection);
                        inspectionIndex++;
                    }else {
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
                                        i = -1;
                                        break;
                                }
                            }
                            sampleInspection.addNewViolation(newViolation);
                        }
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


        } else {
            switch(flag){
                case -1:
                    InputStream isInspection = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
                    BufferedReader inspectionReader = new BufferedReader(
                            new InputStreamReader(isInspection, StandardCharsets.UTF_8)
                    );

                    String line = "";
                    try {
                        inspectionReader.readLine();

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
                                                i = -1;
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
                    break;
                case 0:
                    File root = context.getDir("RestaurantReport",Context.MODE_PRIVATE);
                    File gpxfile = new File(root,"FinalRestaurantReports.csv");

                    InputStream is = null;
                    try {
                        is = new FileInputStream(gpxfile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    BufferedReader inspectionReader2;
                    assert is != null;
                    inspectionReader2 = new BufferedReader(
                            new InputStreamReader(is, StandardCharsets.UTF_8)
                    );
                    String line2 = "";
                    try {
                        inspectionReader2.readLine();// skip the first line of the titles

                        int inspectionIndex = 0; // index to obtain individual inspections from the temporary inspectionList above
                        while ((line2 = inspectionReader2.readLine()) != null) {
                            /*
                            splitting using "" and ,   e.g string = "Name","Address","HazardLevel","violation1, violation2, violation3"
                              we will end up getting
                              Name
                              Address
                              HazardLevel
                              violation1, violation2, violation3

                              then further splitting violations using comma to get separate violations
                             */
                            String[] tokens = line2.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                            if (tokens.length == 5) {
                                // tao string[] moi xong copy paste sang
                                List<String> temp = new ArrayList<String>(Arrays.asList(tokens));
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

                                Inspection sampleInspection = inspectionList.get(inspectionIndex);
                                Violation newViolation = new Violation("", "", "");
                                sampleInspection.addNewViolation(newViolation);
                                inspectionList.set(inspectionIndex, sampleInspection);
                                inspectionIndex++;

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
                                                i = -1;
                                                break;
                                        }
                                    }
                                    sampleInspection.addNewViolation(newViolation);
                                }
                                inspectionList.set(inspectionIndex, sampleInspection);
                                inspectionIndex++;

                            }
                        }
                    } catch (IOException e) {
                        Log.e("Inspection Data", "Error Reading Data File on Line" + line2, e);
                        e.printStackTrace();
                    }

                    List<Restaurant> restaurantList2 = manager.getRestaurants();
                    int restaurantListIndex2 = 0;
                    for (Restaurant tempRestaurant : restaurantList2) {
                        for (Inspection inspection : inspectionList) {
                            if (tempRestaurant.getTrackingNum().equals(inspection.getTrackingNum())) {
                                tempRestaurant.addInspection(inspection);
                            }
                        }

                        manager.set(restaurantListIndex2, tempRestaurant);
                        restaurantListIndex2++;
                    }
            }
        }
    }
}
