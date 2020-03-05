package ca.cmpt276.restaurantreport.model;

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

import ca.cmpt276.restaurantreport.R;

/*
most of the code was learned/taken from:
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
public class RestaurantListAdapter extends ArrayAdapter<String> {

    private List<Restaurant> res;

    public RestaurantListAdapter(Context c, List<Restaurant> rest, String[] titles){
        super(c,R.layout.list_row, R.id.txtName, titles);
        this.res = rest;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Sets up which layout is being modified
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.list_row, parent, false);

        TextView resName = row.findViewById(R.id.txtName);
        TextView resIssues = row.findViewById(R.id.txtIssues);
        TextView resDate = row.findViewById(R.id.txtDate);
        TextView resHaz = row.findViewById(R.id.txtHazard);

        ImageView hazIcon = row.findViewById(R.id.hazIcon);

        //Get the current restaurant information for the appropriate row and its inspections
        Restaurant currentRes = res.get(position);
        List<Inspection> insp = currentRes.getInspections();


        int issueCount = 0;



        //counts all the issues (critical and non-critical) that a restaurant has
        for (int i = 0; i < insp.size(); i++) {
            issueCount += insp.get(i).getTotalIssues();
        }

        String issuesFound = issueCount + " Issues Found";
        String lastInspected = lastInspection(currentRes);
        String inspectDate = "Last Inspected: " + lastInspected;
        String hazardText = currentRes.getLatestInspectionHazard();

        //set the texts with the right parameters
        resName.setText(currentRes.getName().replace("\"", ""));
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
        Date d = Calendar.getInstance().getTime();
        //get the latest inspection of the current restaurant
        int dateLastInspection = restaurant.getLatestInspectionDate();

        //formate the Date above
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String currentDate = formatter.format(d);

        //change into an integer
        int currDate = Integer.parseInt(currentDate);

        int inspYear = dateLastInspection / 10000;
        int inspMonth = (dateLastInspection % 10000) / 100;
        int inspDay = dateLastInspection % 100;

        int currYear = currDate / 10000;
        int currMonth = (currDate % 10000) / 100;
        int currDay = currDate % 100;

        //check the recency of the inspection compared to today's date
        if(inspYear == currYear){
            if(inspMonth == currMonth - 1){
                int result = (currDay + 30) - inspDay;
                if(result > 1) {
                    output = result + " days ago.";
                }
                else if (result == 1){
                    output = result + " day ago.";
                }
            }
            else if(inspMonth == currMonth){
                int result = currDay - inspDay;
                if(result > 1) {
                    output = result + " days ago.";
                }
                else if (result == 1){
                    output = result + " day ago.";
                }
            }
            String month = getMonth(restaurant);
            output = month + inspDay;
        }
        else if(inspYear != 0){
            String month = getMonth(restaurant);
            output = month + inspYear;
        }

        return output;

    }


}
