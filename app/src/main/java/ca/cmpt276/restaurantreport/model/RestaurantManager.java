package ca.cmpt276.restaurantreport.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.cmpt276.restaurantreport.R;

public class RestaurantManager implements Iterable<Restaurant> {

    private List<Restaurant> restaurantList;

    private Context context;

    //constructor with context of an activity passed because we need the context when we want to access the data files to read from
    private RestaurantManager(Context context) {
        restaurantList = new ArrayList<>();
        this.context = context;

        readRestaurantData();
        readInspectionData();
    }

    //adds a Restaurant object to the list of restaurants
    private void add(Restaurant restaurant) {
        restaurantList.add(restaurant);
    }
    //overwrites the restaurant at index mentioned
    public void set(int index, Restaurant restaurant) {
        restaurantList.set(index, restaurant);
    }
    //returns the restaurant in the list at index
    public Restaurant get(int index) {
        return restaurantList.get(index);
    }

    private static RestaurantManager instance;

    public static RestaurantManager getInstance(Context context) {
        if(instance == null) {
            instance = new RestaurantManager(context);
        }
        return instance;
    }

    public List<Restaurant> getRestaurants(){
        return this.restaurantList;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }



    //method for reading the restaurant_itr1 file containing the restaurant details
    // and populating the restaurantList
    private void readRestaurantData() {

        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            reader.readLine(); // to skip the first line with the name, addrs,
            while ((line = reader.readLine()) != null) {

                String [] tokens = line.split(",");

                // makes a new restaurant from the tokens array we read from the file
                //and adds it to the restaurantList
                this.add(new Restaurant(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        tokens[3],
                        tokens[4],
                        Double.parseDouble(tokens[5]),
                        Double.parseDouble(tokens[6])
                ));
            }
        }catch (IOException e) {
            Log.wtf("Main Activity", "Error Reading Data File on Line" + line,e);
            e.printStackTrace();
        }
    }

    // reads the inspectionreports_itr1 and instantiates new inspection with the data read
    // also reads the separated_values_2 file containing only the violations and adds the violations to the respective inspections in the inspectionList
    // Finally it adds the inspection to the respective restaurants in our restaurantList
    private void readInspectionData() {

        List<Inspection> inspectionList = new ArrayList<>(); //temporary inspectionList

        //reading from the inspectionreports_itr1 and adding inspection to the temporary inspectionList above
        InputStream isInspection = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader inspectionReader = new BufferedReader(
                new InputStreamReader(isInspection, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            inspectionReader.readLine();
            while ((line = inspectionReader.readLine()) != null) {
                String [] tokens = line.split(",");

                //Inspection(String trackingNum,int date, String inspectionType,int numCritIssues, int numNonCritIssues, String hazardRating)
                inspectionList.add(new Inspection(
                        tokens[0],
                        Integer.parseInt(tokens[1]),
                        tokens[2],
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        tokens[5]
                ));
            }
        }catch (IOException e) {
            Log.wtf("Inspection Data", "Error Reading Data File on Line" + line,e);
            e.printStackTrace();
        }


        //reading violations from the separated_values_2 file and adding the violations to the inspections in the above inspectionList
        InputStream isViolation = context.getResources().openRawResource(R.raw.separated_values_2);
        BufferedReader violationReader = new BufferedReader(
                new InputStreamReader(isViolation, Charset.forName("UTF-8"))
        );
        line = "";
        try {
            violationReader.readLine();
            int index = 0;

            while ((line = violationReader.readLine()) != null) {
                String [] tokens = line.split("\\|");

                Inspection sampleInspection = inspectionList.get(index);

                for(String violation: tokens) {
                    sampleInspection.addViolation(violation);
                }
                inspectionList.set(index,sampleInspection);
                index++;
            }
        }catch (IOException e) {
            Log.wtf("Violation Data", "Error Reading Data File on Line" + line,e);
            e.printStackTrace();
        }

        //debugging purposes
        for(Restaurant r: restaurantList) {
            System.out.println("Restaurant list before" + r);
        }

        //debugging purposes prints the fully completed inspections from the temporary inspectionList above
        for(Inspection i: inspectionList) {
            System.out.println(" " + i);
        }

        //for each inspection in the temporary inspectionList match the tracking number to the restaurant in the
        //restaurantList and add the inspection to the restaurant
        int restaurantListIndex = 0;
        for(Restaurant tempRestaurant: restaurantList) {
            for(Inspection inspection:inspectionList) {
                if(tempRestaurant.getTrackingNum().equals(inspection.getTrackingNum())) {
                    tempRestaurant.addInspection(inspection);
                }
            }
            //System.out.println("" + tempRestaurant);

            restaurantList.set(restaurantListIndex,tempRestaurant);
            restaurantListIndex++;
        }
    }



}