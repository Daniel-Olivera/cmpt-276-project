package ca.cmpt276.restaurantreport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.Inspection;
import ca.cmpt276.restaurantreport.applogic.Restaurant;


/*
This class is used for putting data about each Restaurant
to a Linear Layout that show all the Restaurant in a database
 */
public class RestaurantListAdapter extends ArrayAdapter<String>{

    private List<Restaurant> res;

    public RestaurantListAdapter (Context c, List<Restaurant> rest, String[] titles){
        super(c,R.layout.restaurant_row, R.id.txtRestaurantName, titles);
        this.res = rest;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //Sets up which layout is being modified
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;

        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.restaurant_row,
                parent, false);

        TextView txtRestaurantName = row.findViewById(R.id.txtRestaurantName);
        TextView txtNumOfIssues = row.findViewById(R.id.txtNumOfIssues);
        TextView txtInspectionDate = row.findViewById(R.id.txtInspectionDate);
        TextView txtHazardLevel = row.findViewById(R.id.txtHazardLevel);



        //Get the current restaurant information for the appropriate row and its inspections
        Restaurant currentRestaurant = res.get(position);
        List<Inspection> insp = currentRestaurant.getInspections();

        int issueCount = currentRestaurant.getTotalIssues();

        String issuesFound = issueCount + " Issues Found";
        String lastInspected = lastInspection(currentRestaurant);
        String inspectDate = "Last Inspected: " + lastInspected;
        String hazardText = currentRestaurant.getLatestInspectionHazard();
        if(hazardText.equals("Moderate")){
            hazardText = "Mid";
        }
        //set the texts with the right parameters
        txtRestaurantName.setText(currentRestaurant.getName());
        txtNumOfIssues.setText(issuesFound);
        txtInspectionDate.setText(inspectDate);
        txtHazardLevel.setText(hazardText.replace("\"",""));

        //changes the hazard icon based on the hazard level
        ImageView hazIcon = row.findViewById(R.id.imgHazardIcon);
        getHazardIcon(hazardText,hazIcon);
        ImageView restaurantIcon = row.findViewById(R.id.imgRestaurantIcon);
        getRestaurantIcon(currentRestaurant.getName(), restaurantIcon);

        return row;
    }

    private void getRestaurantIcon(String name, ImageView restaurantIcon) {

        if(name.contains("Subway ")){
            restaurantIcon.setImageResource(R.drawable.subway_logo);
        }
        else if(name.contains("Tim Horton")){
            restaurantIcon.setImageResource(R.drawable.tim_hortons_logo);
        }
        else if(name.contains("Starbucks Coffee")) {
            restaurantIcon.setImageResource(R.drawable.starbucks_logo);
        }
        else if(name.contains("7-Eleven")){
            restaurantIcon.setImageResource(R.drawable.seven_eleven_logo);
        }
        else if(name.contains("McDonald's")){
            restaurantIcon.setImageResource(R.drawable.mcdonald_logo);
        }
        else if(name.contains("A&W")){
            restaurantIcon.setImageResource(R.drawable.aw_logo);
        }
        else if(name.contains("Save On Foods")){
            restaurantIcon.setImageResource(R.drawable.save_on_foods_logo);
        }
        else if(name.contains("Blenz")){
            restaurantIcon.setImageResource(R.drawable.blenz_logo);
        }
        else if(name.contains("Dairy Queen")){
            restaurantIcon.setImageResource(R.drawable.dairy_queen_logo);
        }
        else if(name.contains("Panago")){
            restaurantIcon.setImageResource(R.drawable.panago_logo);
        }

    }

    private void getHazardIcon(String hazardText, ImageView hazIcon){
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

    //returns the day of the last inspection based on how long ago it was
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String lastInspection(Restaurant restaurant){

        String output = "Never";

        //gets the current date on the phone
        LocalDate currentDate = LocalDate.now();

        //get the latest inspection of the current restaurant
        int dateLastInspection = restaurant.getLatestInspectionDate();

        //convert the inspection date to string
        String lastInspectedDate = Integer.toString(dateLastInspection);
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
                output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionDay;
            }
        }
        else if(inspectionYear == currentYear - 1){
            int monthsAgo = (currentMonth.getValue() + 12) - inspectionMonth.getValue();
            if(monthsAgo <= 12 && monthsAgo >= 0){
                output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionDay;
            }
        }
        else{
            output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionYear;
        }

        return output;
    }


}
