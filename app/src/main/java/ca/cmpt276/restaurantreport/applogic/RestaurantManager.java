package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
        for(ShortViolation shortViolation :shortViolationList) {
            if(shortViolation.getViolationCode() == violationCode) {
                return shortViolation;
            }
        }
        return null;
    }
    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }

    private void fillViolationList(){
        for (TypedArray item: ResourceHelper.getMultiTypedArray(context)) {
            @SuppressLint("ResourceType") ShortViolation shortViolation = new ShortViolation(item.getInt(0,0),item.getString(1));
            shortViolationList.add(shortViolation);
        }
    }

    //returns the day of the last inspection based on how long ago it was
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDisplayDate(int date){

        String output = "Never";

        //gets the current date on the phone
        LocalDate currentDate = LocalDate.now();

        //convert the inspection date to string
        String lastInspectedDate = Integer.toString(date);
            if(lastInspectedDate.equals("0")){
            return "Never";
        }

        //format the inspection date
        LocalDate lastInspection = null;
            try {
            lastInspection = LocalDate.parse(lastInspectedDate, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            Log.d("RestaurantListAdapter","String cannot be parsed into LocalDate");
            e.printStackTrace();
        }

        //get values of month, day and year of the inspection
            assert lastInspection != null;
        int inspectionDay = lastInspection.getDayOfMonth();
        int inspectionYear = lastInspection.getYear();
        Month inspectionMonth = lastInspection.getMonth();

        //get values of the current date
        int currentDay = currentDate.getDayOfMonth();
        int currentYear = currentDate.getYear();
        Month currentMonth = currentDate.getMonth();

        //get month number for calculations
        int numInspMon = inspectionMonth.getValue();
        int numCurMon = currentMonth.getValue();

        //check the recency of the inspection compared to today's date
            if(inspectionYear == currentYear) {
            if(numInspMon == numCurMon){
                int result = currentDay - inspectionDay;
                if(result > 1){
                    output = result + " days ago.";
                }
                else if(result < 1){
                    output = "Inspection scheduled in " + result + " days";
                }
                else {
                    output = result + "day ago.";
                }
            }
            //if within the last month, calculate how many days ago
            else if(numInspMon == numCurMon - 1){
                int inspMonthLen = inspectionMonth.length(lastInspection.isLeapYear());
                int result = currentDay + inspMonthLen;
                result -= inspectionDay;
                output = result + " days ago.";
            }
            else{
                output = inspectionMonth.getDisplayName(TextStyle.SHORT, Locale.US) + " " + inspectionDay;
            }
        }
            else if(inspectionYear == currentYear - 1){
            int monthsAgo = (currentMonth.getValue() + 12) - inspectionMonth.getValue();
            if(monthsAgo <= 12 && monthsAgo >= 0){
                output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionDay;
            }
            else{
                output = inspectionMonth.getDisplayName(TextStyle.SHORT, Locale.US) + " " + inspectionYear;
            }
        }
            else{
            output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionYear;
        }

            return output;
    }

    public void getHazardIcon(String hazardText, ImageView hazIcon){
        switch(hazardText){
            case("\"Low\""):
            default:{
                hazIcon.setImageResource(R.drawable.haz_low);
                break;
            }
            case("Mid"):
            case("Moderate"):{
                hazIcon.setImageResource(R.drawable.haz_medium);
                break;
            }
            case("High"):{
                hazIcon.setImageResource(R.drawable.haz_high);
                break;
            }
        }
    }
}