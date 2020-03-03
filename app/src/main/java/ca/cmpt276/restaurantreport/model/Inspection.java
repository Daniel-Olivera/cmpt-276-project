package ca.cmpt276.restaurantreport.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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

    public int getDate() {
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

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    void addViolation(String violation) { violationList.add(violation);}

    //Formats the date from an integer into readable form
    //(i.e. 2018/02/01)
    public String formatDate(){
        String tempFullDate = Integer.toString(date);

        String tempYear = tempFullDate.substring(0,4);
        String tempMonth = tempFullDate.substring(4,6);
        String tempDay = tempFullDate.substring(6,8);

        return tempYear + "/" + tempMonth + "/" + tempDay;
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
