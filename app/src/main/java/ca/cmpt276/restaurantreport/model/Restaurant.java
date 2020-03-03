package ca.cmpt276.restaurantreport.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private String trackingNum;

    private String Name;
    private String physicalAddr;
    private String physicalCity;
    private String facType;

    private double latitude;
    private double longitude;

    private List<Inspection> Inspections;

    public Restaurant(String trackingNum, String Name, String physicalAddr, String physicalCity,String facType,double latitude, double longitude) {

        this.trackingNum = trackingNum;
        this.Name = Name;
        this.physicalAddr = physicalAddr;
        this.physicalCity = physicalCity;
        this.facType = facType;
        this.latitude = latitude;
        this.longitude = longitude;

        Inspections = new ArrayList<>();
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

    public void setPhysicalAddr(String physicalAddr) {
        this.physicalAddr = physicalAddr;
    }

    public String getPhysicalCity() {
        return physicalCity;
    }

    public void setPhysicalCity(String physicalCity) {
        this.physicalCity = physicalCity;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public String getFacType() {
        return facType;
    }

    public void setFacType(String facType) {
        this.facType = facType;
    }

    String getTrackingNum() {
        return this.trackingNum;
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    void addInspection(Inspection inspection) {
        Inspections.add(inspection);
    }

    public List<Inspection> getInspections(){
        return Inspections;
    }

    //gets the date of the last inspection
    public String getLatestInspection(){

        int mostRecentInspectionDate = 0;
        String result = "Never";

        //date with a higher "number" is more recent (i.e. 2019 > 2018)
        for (int i = 0; i < Inspections.size(); i++){
            if(Inspections.get(i).getDate() > mostRecentInspectionDate){
                mostRecentInspectionDate = Inspections.get(i).getDate();
                //formats the date (i.e. 2018/02/14)
                result = Inspections.get(i).formatDate();
            }
        }

        return result;
    }

    @NonNull //Remove if nullable
    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNum='" + trackingNum + '\'' +
                ", Name='" + Name + '\'' +
                ", physicalAddr='" + physicalAddr + '\'' +
                ", physicalCity='" + physicalCity + '\'' +
                ", facType='" + facType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", Inspections=" + Inspections +
                '}';
    }
}