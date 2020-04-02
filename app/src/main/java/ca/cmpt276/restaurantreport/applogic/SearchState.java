package ca.cmpt276.restaurantreport.applogic;

import android.util.Log;

public class SearchState {

    /*
    true if we clicked search on the search activity leading
     to an ongoing search false when clicked clear
    */
    private boolean activeSearchStateFlag;
    private boolean hazardSearchOn;
    private boolean violationSearchOn;

    private String restaurantName;
    private String hazardLevel;
    //-1 means less than or equal and 1 means greater or equal to; while 0 means condition not used
    private boolean lessOrGreaterThanFlag;
    private int numOfCriticalViolations;

    private boolean onlyFavourites;

    private static SearchState instance;

    public static SearchState getInstance() {
        if(instance == null) {
            instance = new SearchState(false,0);
        }
        return instance;
    }

    private SearchState(boolean activeSearchStateFlag, int numOfCriticalViolations) {
        this.activeSearchStateFlag = activeSearchStateFlag;
        this.numOfCriticalViolations = numOfCriticalViolations;
    }

    boolean activeSearchStateFlag() {
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
        if(hazardLevel.equals("Mid")){
            this.hazardLevel = "Moderate";
        }
        else {
            this.hazardLevel = hazardLevel;
        }
    }

    public boolean getLessOrGreaterThanFlag() {
        return lessOrGreaterThanFlag;
    }

    public void setLessOrGreaterThanFlag(boolean lessOrGreaterThanFlag) {
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

    public boolean isHazardSearchOn() {
        return hazardSearchOn;
    }

    public void setHazardSearchOn(boolean hazardSearchOn) {
        this.hazardSearchOn = hazardSearchOn;
    }

    public boolean isViolationSearchOn() {
        return violationSearchOn;
    }

    public void setViolationSearchOn(boolean violationSearchOn) {
        this.violationSearchOn = violationSearchOn;
    }

    public void clearSearchState(){
        activeSearchStateFlag = false;
        hazardSearchOn = false;
        violationSearchOn = false;
        restaurantName = "";
        hazardLevel = "none";
        lessOrGreaterThanFlag = false;
        numOfCriticalViolations = 0;
        onlyFavourites = false;
    }
}
