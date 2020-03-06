package ca.cmpt276.restaurantreport.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Inspection {

    private String trackingNum;

    private int date;
    private String inspectionType;
    private int numCritIssues;
    private int numNonCritIssues;
    private String hazardRating;

    private List<String> violationList;

    Inspection(String trackingNum,int date, String inspectionType,int numCritIssues, int numNonCritIssues, String hazardRating){
        this.trackingNum = trackingNum;
        this.date = date;
        this.inspectionType = inspectionType;
        this.numCritIssues = numCritIssues;
        this.numNonCritIssues = numNonCritIssues;
        this.hazardRating = hazardRating;

        violationList = new ArrayList<>();
    }

    String getTrackingNum() {
        return this.trackingNum;
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public int getNumCritIssues() {
        return numCritIssues;
    }

    public void setNumCritIssues(int numCritIssues) {
        this.numCritIssues = numCritIssues;
    }

    public int getNumNonCritIssues() {
        return numNonCritIssues;
    }

    public void setNumNonCritIssues(int numNonCritIssues) {
        this.numNonCritIssues = numNonCritIssues;
    }

    public int getTotalIssues(){
        return getNumNonCritIssues() + getNumCritIssues();
    }

    String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    void addViolation(String violation) { violationList.add(violation);}

    public String dayFromLastInspection()
    {
        String output;

        //gets the current date on the phone
        LocalDate currentDate = LocalDate.now();

        //get the latest inspection of the current restaurant
        int dateLastInspection = date;

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
                output = inspectionMonth.getDisplayName(TextStyle.SHORT, Locale.US) + " " + inspectionDay;
            }
        }
        else{
            output = inspectionMonth.getDisplayName(TextStyle.SHORT,Locale.US) + " " + inspectionYear;
        }

        return output;

    }

    @NonNull //Remove if nullable
    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNum='" + trackingNum + '\'' +
                ", date=" + date +
                ", inspectionType='" + inspectionType + '\'' +
                ", numCritIssues=" + numCritIssues +
                ", numNonCritIssues=" + numNonCritIssues +
                ", hazardRating='" + hazardRating + '\'' +
                ", violationList=" + violationList +
                '}';
    }
}
