package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;
import android.util.Log;

import java.util.List;

public class FilterLogic {
    private RestaurantManager manager;
    private SearchState searchState;
    private List<Restaurant> restaurantList;
    private Context context;

    private boolean hazardSearchOn;
    private boolean violationSearchOn;

    private String restaurantName;
    private String hazardLevel;
    private boolean lessOrGreaterThanFlag;
    private int numOfCriticalViolations;

    public FilterLogic(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        manager = RestaurantManager.getInstance(context);
        searchState = SearchState.getInstance();
        this.restaurantList = restaurantList;

        this.hazardSearchOn = searchState.isDoHazardSearch();
        this.violationSearchOn = searchState.isDoViolationSearch();
        this.restaurantName = searchState.getSearchKeyWords();
        this.hazardLevel = searchState.getSearchHazardLevel();
        this.lessOrGreaterThanFlag = searchState.getLesserOrGreaterThanFlag();
        this.numOfCriticalViolations = searchState.getNumOfCriticalViolations();
    }

    public void populateFilteredRestaurantList() {
        /*
        If favourites checkbox is checked search from the favouritedRestaurantList in manager
        else search from the default restaurantList in manager
         */
        if (searchState.getSearchByFavourites()) {
            // get the favorite restaurant list
            restaurantList = manager.getFavoriteRestaurantList();
        }

            /*
            account for these possibilities
            ** used 3 of the criteria
                1.) search bar used, hazard level used, num of violation used
            ** used 2 of the criteria
                2.) search bar used, hazard level used, num of violations not used
                3.) search bar used, hazard level not used, num of violations used
                4.) search bar not used, hazard level used, num of violations used
            ** used 1 of the criteria
                5.) search bar used, hazard level not used, num of violations not used
                6.) search bar not used, hazard level used, num of violations not used
                7.) search bar not used, hazard level not used, num of violations used
            ** used none of the criteria
                8.) search bar not used, hazard level not used, num of violations not used
             */
        //Number 1
        if ((!restaurantName.isEmpty()) && (hazardSearchOn) && (violationSearchOn)) {
            System.out.println("entering number one");
            for (Restaurant restaurant : restaurantList) {
                if ((checkNameSimilarity(restaurant)) && (checkHazardLevel(restaurant)) && (checkNumCritViolations(restaurant))) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        }
        //Number 2
        else if (!restaurantName.isEmpty() && hazardSearchOn && !violationSearchOn) {
            System.out.println("entering number two");
            for (Restaurant restaurant : restaurantList) {
                if ((checkNameSimilarity(restaurant)) && (checkHazardLevel(restaurant))) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        }
        //Number 3
        else if (!restaurantName.isEmpty() && !hazardSearchOn && violationSearchOn) {
            System.out.println("entering number three");
            for (Restaurant restaurant : restaurantList) {
                if ((checkNameSimilarity(restaurant)) && (checkNumCritViolations(restaurant))) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        }
        //Number 4
        else if (restaurantName.isEmpty() && hazardSearchOn && violationSearchOn) {
            System.out.println("entering number four");
            for (Restaurant restaurant : restaurantList) {
                if ((checkHazardLevel(restaurant)) && (checkNumCritViolations(restaurant))) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        }
        //Number 5
        else if (!restaurantName.isEmpty() && !hazardSearchOn && !violationSearchOn) {
            System.out.println("entering number five");
            for (Restaurant restaurant : restaurantList) {
                if (checkNameSimilarity(restaurant)) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        }
        //Number 6
        else if (restaurantName.isEmpty() && hazardSearchOn && !violationSearchOn) {
            System.out.println("entering number six");
            for (Restaurant restaurant : restaurantList) {
                if (checkHazardLevel(restaurant)) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        }
        //Number 7
        else if (restaurantName.isEmpty() && !hazardSearchOn && violationSearchOn) {
            System.out.println("entering number seven");
            for (Restaurant restaurant : restaurantList) {
                if (checkNumCritViolations(restaurant)) {
                    if (searchState.getSearchByFavourites()) {
                        manager.addToFilterFavoriteList(restaurant);
                    } else {
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
        } else {
            System.out.println("entering number eight");
            if (searchState.getSearchByFavourites()) {
                for (int i = 0 ; i < restaurantList.size(); i++)
                {
                    manager.addToFilterFavoriteList(restaurantList.get(i));
                }
            }
        }
    }
    //}

    private boolean checkNameSimilarity(Restaurant restaurant) {
        String actualRestaurantName = restaurant.getName().toLowerCase();
        String searchedWord = searchState.getSearchKeyWords().toLowerCase();

        return actualRestaurantName.contains(searchedWord);
    }

    private boolean checkHazardLevel(Restaurant restaurant) {
        String actualRestaurantHazardLevel = restaurant.getLatestInspectionHazard(context);
        return actualRestaurantHazardLevel.equalsIgnoreCase(hazardLevel);
    }

    private boolean checkNumCritViolations(Restaurant restaurant) {
        int critViolationsOfActualRestaurant = restaurant.getNumCritViolationsWithinLastYear();
        if (lessOrGreaterThanFlag) {
            return critViolationsOfActualRestaurant >= numOfCriticalViolations;
        } else {
            return critViolationsOfActualRestaurant <= numOfCriticalViolations;
        }
    }
}
