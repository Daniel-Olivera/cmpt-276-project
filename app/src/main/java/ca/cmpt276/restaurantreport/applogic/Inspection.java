package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/*
This class store all data about each Inspection of the restaurants in the
data base
 */

public class Inspection implements Comparable<Inspection> {

    private String trackingNum;

    private Integer date;
    private String inspectionType;
    private int numCritIssues;
    private int numNonCritIssues;
    private String hazardRating;


    private List<Violation> ViolationList = new ArrayList<>();

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

    void addNewViolation(Violation violation) { ViolationList.add(violation);}

    public List getViolations() {return this.ViolationList;}

    public String getInspectionDateDisplay(Context context) {
        RestaurantManager manager = RestaurantManager.getInstance(context);
        return manager.getDisplayDate(date);
    }

    @Override
    public int compareTo(Inspection o) {
        return this.getDate().compareTo(o.getDate());
    }
}
