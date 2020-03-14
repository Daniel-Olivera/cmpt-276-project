package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

        fillViolationList();
    }

    //adds a Restaurant object to the list of restaurants
    void add(Restaurant restaurant) {
        restaurantList.add(restaurant);
    }
    //overwrites the restaurant at index mentioned
    void set(int index, Restaurant restaurant) {
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