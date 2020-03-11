package ca.cmpt276.restaurantreport.model;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/*
This class store data of every Restaurant in the database
 */
public class Restaurant {

    private String trackingNum;

    private String Name;
    private String physicalAddr;
    private String physicalCity;
    private String facType;

    private double latitude;
    private double longitude;

    private List<Inspection> Inspections = new ArrayList<>();

    public Restaurant(String trackingNum, String Name, String physicalAddr, String physicalCity,String facType,double latitude, double longitude) {

        this.trackingNum = trackingNum;
        this.Name = Name;
        this.physicalAddr = physicalAddr;
        this.physicalCity = physicalCity;
        this.facType = facType;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void addInspection(Inspection inspection) {
        Inspections.add(inspection);
    }

    public List<Inspection> getInspections(){
        return Inspections;
    }

    String getLatestInspectionHazard() {

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

    int getLatestInspectionDate(){

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
}