package ca.cmpt276.restaurantreport.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.restaurantreport.R;

/*
most of the code was learned/taken from:
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
public class RestaurantListAdapter extends ArrayAdapter<String> {

    private List<Restaurant> res;

    public RestaurantListAdapter(Context c, List<Restaurant> rest, String[] titles){
        super(c,R.layout.list_row, R.id.txtRestaurantName, titles);
        this.res = rest;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Sets up which layout is being modified
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.list_row, parent, false);

        TextView resName = row.findViewById(R.id.txtRestaurantName);
        TextView resIssues = row.findViewById(R.id.txtNumOfIssues);
        TextView resDate = row.findViewById(R.id.txtInspectionDate);
        TextView resHaz = row.findViewById(R.id.txtHazardLevel);

        ImageView hazIcon = row.findViewById(R.id.imgHazardIcon);

        //Get the current restaurant information for the appropriate row and its inspections
        Restaurant currentRestaurant = res.get(position);
        List<Inspection> insp = currentRestaurant.getInspections();


        int issueCount = 0;



        //counts all the issues (critical and non-critical) that a restaurant has
        for (int i = 0; i < insp.size(); i++) {
            issueCount += insp.get(i).getTotalIssues();
        }

        String issuesFound = issueCount + " Issues Found";
        String lastInspected = lastInspection(currentRestaurant);
        String inspectDate = "Last Inspected: " + lastInspected;
        String hazardText = currentRestaurant.getLatestInspectionHazard();

        //set the texts with the right parameters
        resName.setText(currentRestaurant.getName().replace("\"", ""));
        resIssues.setText(issuesFound);
        resDate.setText(inspectDate);
        resHaz.setText(hazardText.replace("\"",""));

        //changes the hazard icon based on the hazard level
        getHazardIcon(hazardText,hazIcon);

        return row;
    }

    private void getHazardIcon(String hazardLevel, ImageView icon){
        switch(hazardLevel){
            case("\"Low\""):{
                icon.setImageResource(R.drawable.low);
                break;
            }
            case("\"Moderate\""):{
                icon.setImageResource(R.drawable.medium);
                break;
            }
            case("\"High\""):
            default:{
                icon.setImageResource(R.drawable.high);
                break;
            }
        }
    }

    //converts the number of the month into the corresponding name
    private String getMonth(Restaurant restaurant){

        String result;
        int numMonth = restaurant.getLatestInspectionDate();

        numMonth = (numMonth % 10000) / 100;

        switch(numMonth){
            case(1):{
                result = "Jan ";
                break;
            }
            case(2):{
                result = "Feb ";
                break;
            }
            case(3):{
                result = "Mar ";
                break;
            }
            case(4):{
                result = "Apr ";
                break;
            }
            case(5):{
                result = "May ";
                break;
            }
            case(6):{
                result = "Jun ";
                break;
            }
            case(7):{
                result = "Jul ";
                break;
            }
            case(8):{
                result = "Aug ";
                break;
            }
            case(9):{
                result = "Sep ";
                break;
            }
            case(10):{
                result = "Oct ";
                break;
            }
            case(11):{
                result = "Nov ";
                break;
            }
            case(12):{
                result = "Dec ";
                break;
            }
            default:{
                result = "Never";
                break;
            }
        }

        return result;
    }

    //returns the day of the last inspection based on how long ago it was
    private String lastInspection(Restaurant restaurant){

        String output = "Never";

        //gets the current date on the phone
        Date date = Calendar.getInstance().getTime();
        //get the latest inspection of the current restaurant
        int dateLastInspection = restaurant.getLatestInspectionDate();

        //format the Date above
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String currentDateString = formatter.format(date);

        //change into an integer
        int currentDate = Integer.parseInt(currentDateString);

        int inspectionYear = dateLastInspection / 10000;
        int inspectionMonth = (dateLastInspection % 10000) / 100;
        int inspectionDay = dateLastInspection % 100;

        int currentYear = currentDate / 10000;
        int currentMonth = (currentDate % 10000) / 100;
        int currentDay = currentDate % 100;

        //check the recency of the inspection compared to today's date
        if(inspectionYear == currentYear){
            if(inspectionMonth == currentMonth - 1){
                int result = (currentDay + 30) - inspectionDay;
                if(result > 1) {
                    output = result + " days ago.";
                }
                else if (result == 1){
                    output = result + " day ago.";
                }
            }
            else if(inspectionMonth == currentMonth){
                int result = currentDay - inspectionDay;
                if(result > 1) {
                    output = result + " days ago.";
                }
                else if (result == 1){
                    output = result + " day ago.";
                }
            }
            String month = getMonth(restaurant);
            output = month + inspectionDay;
        }
        else if(inspectionYear != 0){
            String month = getMonth(restaurant);
            output = month + inspectionYear;
        }

        return output;

    }


}
