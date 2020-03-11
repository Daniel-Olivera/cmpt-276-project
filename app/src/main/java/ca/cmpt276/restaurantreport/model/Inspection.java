package ca.cmpt276.restaurantreport.model;

import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.restaurantreport.R;

public class Inspection implements Comparable<Inspection> {

    private String trackingNum;

    private Integer date;
    private String inspectionType;
    private int numCritIssues;
    private int numNonCritIssues;
    private String hazardRating;

    private List<Violation> ViolationList = new ArrayList<>();;

    Inspection(String trackingNum,int date, String inspectionType,int numCritIssues, int numNonCritIssues, String hazardRating){
        this.trackingNum = trackingNum;
        this.date = date;
        this.inspectionType = inspectionType;
        this.numCritIssues = numCritIssues;
        this.numNonCritIssues = numNonCritIssues;
        this.hazardRating = hazardRating;

    }

    String getTrackingNum() {
        return this.trackingNum;
    }

    public Integer getDate() {
        return date;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCritIssues() {
        return numCritIssues;
    }

    public int getNumNonCritIssues() {
        return numNonCritIssues;
    }

    public int getTotalIssues(){
        return getNumNonCritIssues() + getNumCritIssues();
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void addNewViolation(Violation violation) { ViolationList.add(violation);}

    public List getViolations() {return this.ViolationList;}


    public String dayFromLastInspection()
    {
        String output;

        //gets the current date on the phone
        LocalDate currentDate = LocalDate.now();

        int dateLastInspection = date;

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
                output = inspectionMonth.getDisplayName(TextStyle.SHORT, Locale.US) + " " + inspectionDay;
            }
        }
        else{
            output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionYear;
        }

        return output;

    }

    @Override
    public int compareTo(Inspection o) {
        return this.getDate().compareTo(o.getDate());
    }
}
