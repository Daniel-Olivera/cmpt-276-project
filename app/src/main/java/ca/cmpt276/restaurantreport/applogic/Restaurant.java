package ca.cmpt276.restaurantreport.applogic;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

/*
This class store data of every Restaurant in the database
 */
public class Restaurant implements ClusterItem {

    private String trackingNum;

    private String Name;
    private String physicalAddr;
    private String physicalCity;
    private String facType;


    private String mTitle;
    private String mSnippet;

    private double latitude;
    private double longitude;

    private LatLng mPosition;

    private List<Inspection> Inspections = new ArrayList<>();


    public Restaurant(String trackingNum, String Name, String physicalAddr, String physicalCity,String facType,double latitude, double longitude) {

        this.trackingNum = trackingNum;
        this.Name = Name;
        this.physicalAddr = physicalAddr;
        this.physicalCity = physicalCity;
        this.facType = facType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mPosition = new LatLng(latitude,longitude);
        this.mTitle = Name;
        this.mSnippet = trackingNum;

    }

    public Restaurant() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhysicalAddr() {
        return physicalAddr;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTrackingNum() {
        return this.trackingNum;
    }

    void addInspection(Inspection inspection) {
        Inspections.add(inspection);
    }

    public List<Inspection> getInspections(){
        return Inspections;
    }

    public String getLatestInspectionHazard() {

        int mostRecentInspectionDate = 0;
        String result = "N/A";

        for (int i = 0; i < Inspections.size(); i++) {
            if (Inspections.get(i).getDate() > mostRecentInspectionDate) {
                mostRecentInspectionDate = Inspections.get(i).getDate();

                result = Inspections.get(i).getHazardRating();
            }
        }
        return result;
    }

    public int getLatestInspectionDate(){

        int mostRecentInspectionDate = 0;
        int result = 0;
        for (int i = 0; i < Inspections.size(); i++){
            if(Inspections.get(i).getDate() > mostRecentInspectionDate){
                mostRecentInspectionDate = Inspections.get(i).getDate();

                result = Inspections.get(i).getDate();
            }
        }

        return result;
    }

    public int getMostRecentIssues(){
        int issueCount = 0;
        int latestInspectionDate = getLatestInspectionDate();

        //counts all the issues (critical and non-critical) that a restaurant has
        for (int i = 0; i < Inspections.size(); i++) {
            if(latestInspectionDate == Inspections.get(i).getDate()){
                issueCount = Inspections.get(i).getTotalIssues();
            }
        }

        return issueCount;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}