package ca.cmpt276.restaurantreport.applogic;

public class SearchState {

    /*
    true if we clicked search on the search activity leading
     to an ongoing search false when clicked clear
    */
    private boolean searchStateActive;

    private boolean doHazardSearch;
    private boolean doViolationSearch;

    private String searchKeyWords;
    private String searchHazardLevel;

    private boolean lesserOrGreaterThanFlag;
    private int numOfCriticalViolations;

    private boolean searchByFavourites;

    private static SearchState instance;

    public static SearchState getInstance() {
        if(instance == null) {
            instance = new SearchState(false,0);
        }
        return instance;
    }

    private SearchState(boolean searchStateActive, int numOfCriticalViolations) {
        this.searchStateActive = searchStateActive;
        this.numOfCriticalViolations = numOfCriticalViolations;
    }

    boolean getSearchStateActive() {
        return searchStateActive;
    }

    public void setSearchStateActive(boolean searchStateActive) {
        this.searchStateActive = searchStateActive;
    }

    public String getSearchKeyWords() {
        return searchKeyWords;
    }

    public void setSearchKeyWords(String searchKeyWords) {
        this.searchKeyWords = searchKeyWords;
    }

    public String getSearchHazardLevel() {
        return searchHazardLevel;
    }

    public void setSearchHazardLevel(String searchHazardLevel) {
        if(searchHazardLevel.equals("Mid")){
            this.searchHazardLevel = "Moderate";
        }
        else {
            this.searchHazardLevel = searchHazardLevel;
        }
    }

    public boolean getLesserOrGreaterThanFlag() {
        return lesserOrGreaterThanFlag;
    }

    public void setLesserOrGreaterThanFlag(boolean lesserOrGreaterThanFlag) {
        this.lesserOrGreaterThanFlag = lesserOrGreaterThanFlag;
    }

    public int getNumOfCriticalViolations() {
        return numOfCriticalViolations;
    }

    public void setNumOfCriticalViolations(int numOfCriticalViolations) {
        this.numOfCriticalViolations = numOfCriticalViolations;
    }

    public boolean getSearchByFavourites() {
        return searchByFavourites;
    }

    public void setSearchByFavourites(boolean searchByFavourites) {
        this.searchByFavourites = searchByFavourites;
    }

    public boolean isDoHazardSearch() {
        return doHazardSearch;
    }

    public void setDoHazardSearch(boolean doHazardSearch) {
        this.doHazardSearch = doHazardSearch;
    }

    public boolean isDoViolationSearch() {
        return doViolationSearch;
    }

    public void setDoViolationSearch(boolean doViolationSearch) {
        this.doViolationSearch = doViolationSearch;
    }

    public void clearSearchState(){
        searchStateActive = false;
        doHazardSearch = false;
        doViolationSearch = false;
        searchKeyWords = "";
        searchHazardLevel = "none";
        lesserOrGreaterThanFlag = false;
        numOfCriticalViolations = 0;
        searchByFavourites = false;
    }

    @Override
    public String toString() {
        return "SearchState{" +
                "activeSearchStateFlag=" + searchStateActive +
                ", restaurantName='" + searchKeyWords + '\'' +
                ", hazardLevel='" + searchHazardLevel + '\'' +
                ", lessOrGreaterThanFlag=" + lesserOrGreaterThanFlag +
                ", numOfCriticalViolations=" + numOfCriticalViolations +
                ", onlyFavourites=" + searchByFavourites +
                '}';
    }
}
