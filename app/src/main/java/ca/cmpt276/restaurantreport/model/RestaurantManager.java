package ca.cmpt276.restaurantreport.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;

/*
This class is a singleton that store all the Restaurant as
a list and can be call in every Activity to get all the data
in the database
 */
public class RestaurantManager implements Iterable<Restaurant> {

    private List<Restaurant> restaurantList;
    private List<ShortViolation> shortViolationList;
    private Context context;

    //constructor with context of an activity passed because we need the context when we want to access the data files to read from
    private RestaurantManager(Context context) {
        restaurantList = new ArrayList<>();
        shortViolationList = new ArrayList<>();
        this.context = context;

        readRestaurantData();
        readInspectionData();
        fillViolationList();
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

    @SuppressLint("StaticFieldLeak")
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

    public List<ShortViolation> getShortViolationList() {return this.shortViolationList; }

    public ShortViolation getShortViolation(int violationCode) {
        for(ShortViolation s :shortViolationList) {
            if(Objects.equals(s.getViolationCode(), violationCode)) {
                return s;
            }
        }
        return null;
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
                new InputStreamReader(is, StandardCharsets.UTF_8)
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
            Log.e("Main Activity", "Error Reading Data File on Line" + line,e);
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
                String [] tokens = line.split(",");

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
            Log.e("Inspection Data", "Error Reading Data File on Line" + line,e);
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
                String [] tokens = line.split("\\|");

                Inspection sampleInspection = inspectionList.get(index);
                for(String violation: tokens) {

                    String [] tokens2 = violation.split(",");

                    Violation newViolation = new Violation();
                    int i = 0;
                    for(String violationString: tokens2) {
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
                inspectionList.set(index,sampleInspection);
                index++;
            }
        }catch (IOException e) {
            Log.e("Violation Data", "Error Reading Data File on Line" + line,e);
            e.printStackTrace();
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

            restaurantList.set(restaurantListIndex,tempRestaurant);
            restaurantListIndex++;
        }
    }

    /*
    * Code snippet modified from:
    * https://basememara.com/storing-multidimensional-resource-arrays-in-android/
    * */
    private void fillViolationList(){
        for (TypedArray item: ResourceHelper.getMultiTypedArray(context)) {
            @SuppressLint("ResourceType") ShortViolation shortViolation = new ShortViolation(item.getInt(0,0),item.getString(1));
            shortViolationList.add(shortViolation);
            Log.d("shortViolationList",shortViolation.toString());
        }

    }
}