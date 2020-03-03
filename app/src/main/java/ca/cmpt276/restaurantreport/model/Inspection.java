package ca.cmpt276.restaurantreport.model;

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

    public Inspection(String trackingNum,int date, String inspectionType,int numCritIssues, int numNonCritIssues, String hazardRating){
        this.trackingNum = trackingNum;
        this.date = date;
        this.inspectionType = inspectionType;
        this.numCritIssues = numCritIssues;
        this.numNonCritIssues = numNonCritIssues;
        this.hazardRating = hazardRating;

        violationList = new ArrayList<>();
    }



    public String getTrackingNum() {
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

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public void addViolation(String violation) { violationList.add(violation);}

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
