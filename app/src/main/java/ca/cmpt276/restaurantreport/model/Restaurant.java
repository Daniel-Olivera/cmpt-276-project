package ca.cmpt276.restaurantreport.model;

import java.util.List;

public class Restaurant {
    private String trackingNum;
    private String resName;
    private String address;
    private String city;
    private String type;
    private String latitude;
    private String longitude;
    private List<InspectionReport> reportList;

    public String getTrackingNum() {
        return trackingNum;
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longitude;
    }

    public void setLongtitude(String longtitude) {
        this.longitude = longtitude;
    }
}
