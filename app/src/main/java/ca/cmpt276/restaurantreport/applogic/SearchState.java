package ca.cmpt276.restaurantreport.applogic;

import android.annotation.SuppressLint;

public class SearchState {

    private boolean activeSearchStateFlag; // true if we clicked search on the search activity leading to an ongoing search false when clicked clear

    private String restaurantName;
    private String hazardLevel;
    private int lessOrGreaterThanFlag;  //-1 means less than or equal and 1 means greater or equal to; while 0 means condition not used
    private int numOfCriticalViolations;

    private boolean onlyFavourites;

    @SuppressLint("StaticFieldLeak")
    private static SearchState instance;

    public static SearchState getInstance() {
        if(instance == null) {
            instance = new SearchState();
        }
        return instance;
    }
    public boolean activeSearchStateFlag() {
        return activeSearchStateFlag;
    }

    public void setActiveSearchStateFlag(boolean activeSearchStateFlag) {
        this.activeSearchStateFlag = activeSearchStateFlag;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public int getLessOrGreaterThanFlag() {
        return lessOrGreaterThanFlag;
    }

    public void setLessOrGreaterThanFlag(int lessOrGreaterThanFlag) {
        this.lessOrGreaterThanFlag = lessOrGreaterThanFlag;
    }

    public int getNumOfCriticalViolations() {
        return numOfCriticalViolations;
    }

    public void setNumOfCriticalViolations(int numOfCriticalViolations) {
        this.numOfCriticalViolations = numOfCriticalViolations;
    }

    public boolean onlyFavourites() {
        return onlyFavourites;
    }

    public void setOnlyFavourites(boolean onlyFavourites) {
        this.onlyFavourites = onlyFavourites;
    }

    @Override
    public String toString() {
        return "SearchState{" +
                "activeSearchStateFlag=" + activeSearchStateFlag +
                ", restaurantName='" + restaurantName + '\'' +
                ", hazardLevel='" + hazardLevel + '\'' +
                ", lessOrGreaterThanFlag=" + lessOrGreaterThanFlag +
                ", numOfCriticalViolations=" + numOfCriticalViolations +
                ", onlyFavourites=" + onlyFavourites +
                '}';
    }
}
