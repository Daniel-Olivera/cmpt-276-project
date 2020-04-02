package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.annotation.StyleableRes;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.restaurantreport.R;

/*
This class is a singleton that store all the Restaurant as
a list and can be call in every Activity to get all the data
in the database
 */
public class RestaurantManager implements Iterable<Restaurant> {

    private List<Restaurant> restaurantList;
    private List<Restaurant> filteredRestaurantList;
    private List<ShortViolation> shortViolationList;
    private Context context;
    private SearchState searchState;
    private FilterLogic filterLogic;

    //constructor with context of an activity passed because we need the context when we want to access the data files to read from
    private RestaurantManager(Context context) {
        restaurantList = new ArrayList<>();
        shortViolationList = new ArrayList<>();
        filteredRestaurantList = new ArrayList<>();
        this.context = context;

        fillViolationList();

        //TODO: After getting the instance use it for making a new list of restaurants with the specific search values

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
        searchState = SearchState.getInstance();
        if(searchState.activeSearchStateFlag()){
            return this.filteredRestaurantList.get(index);
        }
        else{
            return this.restaurantList.get(index);
        }
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
        searchState = SearchState.getInstance();
        if(searchState.activeSearchStateFlag()){
            return this.filteredRestaurantList;
        }
        else{
            return this.restaurantList;
        }
    }
    void addToFilteredRestaurantList(Restaurant restaurant){
        this.filteredRestaurantList.add(restaurant);
    }

    public void clearFilteredList(){
        this.filteredRestaurantList.clear();
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
            @StyleableRes int index = 1;
            ShortViolation shortViolation = new ShortViolation(item.getInt(0,0),item.getString(index));
            shortViolationList.add(shortViolation);
        }
    }

    //returns the day of the last inspection based on how long ago it was
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDisplayDate(int date){

        String output;

        //gets the current date on the phone
        LocalDate currentDate = LocalDate.now();

        //convert the inspection date to string
        String lastInspectedDate = Integer.toString(date);
            if(lastInspectedDate.equals("0")){
            return context.getString(R.string.lastInspect_never);
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
        String inspectionMonthName = inspectionMonth.getDisplayName(TextStyle.SHORT, Locale.US);

        //check the recency of the inspection compared to today's date
        if(inspectionYear == currentYear) {
            if(numInspMon == numCurMon){
                int result = currentDay - inspectionDay;
                if(result > 1){
                    //output = result + " days ago";
                    output = context.getString(R.string.get_date_ago_day_s,Integer.toString(result));
                }
                else if(result < 1){
                    output = context.getString(R.string.get_date_inspect_scheduled,Integer.toString(result));
                }
                else {
                    output = context.getString(R.string.get_date_ago_day,Integer.toString(result));
                }
            }
            //if within the last month, calculate how many days ago
            else if(numInspMon == numCurMon - 1){
                int inspMonthLen = inspectionMonth.length(lastInspection.isLeapYear());
                int result = currentDay + inspMonthLen;
                result -= inspectionDay;

                if(result <= 30){
                    output = context.getString(
                            R.string.get_date_ago_day,
                            Integer.toString(result));
                }
                else{
                    output = context.getString(
                            R.string.get_date_str1_str2,
                            inspectionMonthName,
                            Integer.toString(inspectionDay));
                }
            }
            else{
                output = context.getString(
                        R.string.get_date_str1_str2,
                        inspectionMonthName,
                        Integer.toString(inspectionDay));
            }
        }
        else if(inspectionYear == currentYear - 1){
            int monthsAgo = (currentMonth.getValue() + 12) - inspectionMonth.getValue();
            if(monthsAgo <= 12 && monthsAgo >= 0){
                output = context.getString(
                        R.string.get_date_str1_str2,
                        inspectionMonthName,
                        Integer.toString(inspectionDay));
            }
            else{
                output = context.getString(
                        R.string.get_date_str1_str2,
                        inspectionMonthName,
                        Integer.toString(inspectionYear));
            }
        }
        else{
            output = context.getString(
                    R.string.get_date_str1_str2,
                    inspectionMonthName,
                    Integer.toString(inspectionYear));
        }
        return output;
    }

    public void setHazardIcon(String hazardText, ImageView hazIcon){
        switch(hazardText){
            case("Low"):
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

    public void setHazardLevelText(TextView textbox, String hazardText){
        switch (hazardText){
            case "Low":
                textbox.setText(context.getString(R.string.restaurant_hazard_low));
                break;
            case "Moderate":
                textbox.setText(context.getString(R.string.restaurant_hazard_moderate));
                break;
            case "High":
                textbox.setText(context.getString(R.string.restaurant_hazard_high));
                break;
        }
    }

    public void setCriticalityText(TextView textbox, String violationCriticality) {
        switch(violationCriticality) {
            case ("Critical"):
                textbox.setText(context.getString(R.string.violation_list_critical));
                break;
            case("Not Critical"):
                textbox.setText(context.getString(R.string.violation_list_not_critical));
                break;
        }
    }

    public void setInspectionType(TextView textbox, String inspectionType) {
        switch(inspectionType) {
            case ("Routine"):
                textbox.setText(context.getString(R.string.inspect_type_routine));
                break;
            case("Follow-Up"):
                textbox.setText(context.getString(R.string.inspect_type_follow_up));
                break;
        }
    }

    public void setText(TextView textBox, int stringResID, String arrayItem){
        textBox.setText(context.getString(stringResID, arrayItem));
    }

    public void setText(TextView textBox, int stringResID, int arrayItem ){
        textBox.setText(context.getString(stringResID, Integer.toString(arrayItem)));
    }

    public void setText(TextView textBox, int stringResID, double arrayItem ){
        textBox.setText(context.getString(stringResID, Double.toString(arrayItem)));
    }
}