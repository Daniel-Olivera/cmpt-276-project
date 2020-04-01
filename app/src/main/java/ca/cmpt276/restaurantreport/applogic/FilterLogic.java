package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;

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

    FilterLogic(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        manager = RestaurantManager.getInstance(context);
        searchState = SearchState.getInstance();
        this.restaurantList = restaurantList;

        this.hazardSearchOn = searchState.isHazardSearchOn();
        this.violationSearchOn = searchState.isViolationSearchOn();
        this.restaurantName = searchState.getRestaurantName();
        this.hazardLevel = searchState.getHazardLevel();
        this.lessOrGreaterThanFlag = searchState.getLessOrGreaterThanFlag();
        this.numOfCriticalViolations = searchState.getNumOfCriticalViolations();
    }

    public void populateFilteredRestaurantList(){
        /*
        If favourites checkbox is checked search from the favouritedRestaurantList in manager
        else search from the default restaurantList in manager
         */
        if(searchState.onlyFavourites()){
            // TODO: create a favouritedRestaurantList in manager that holds all the restaurants marked as favourite
        }
        else{
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
               TODO: take care of this in search activity when clicking search. Either show a log or something else
             */
            //Number 1
            if((!restaurantName.isEmpty()) && (hazardSearchOn) && (violationSearchOn)){
                System.out.println("entering number one");
                for(Restaurant restaurant: restaurantList){
                    if((checkNameSimilarity(restaurant)) && (checkHazardLevel(restaurant)) && (checkNumCritViolations(restaurant))){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
            //Number 2
            else if(!restaurantName.isEmpty() && hazardSearchOn && !violationSearchOn){
                System.out.println("entering number two");
                for(Restaurant restaurant: restaurantList){
                    if((checkNameSimilarity(restaurant)) && (checkHazardLevel(restaurant))){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
            //Number 3
            else if(!restaurantName.isEmpty() && !hazardSearchOn && violationSearchOn){
                System.out.println("entering number three");
                for(Restaurant restaurant: restaurantList){
                    if((checkNameSimilarity(restaurant)) && (checkNumCritViolations(restaurant))){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
            //Number 4
            else if(restaurantName.isEmpty() && hazardSearchOn && violationSearchOn){
                System.out.println("entering number four");
                for(Restaurant restaurant: restaurantList){
                    if((checkHazardLevel(restaurant)) && (checkNumCritViolations(restaurant))){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
            //Number 5
            else if (!restaurantName.isEmpty() && !hazardSearchOn && !violationSearchOn){
                System.out.println("entering number five");
                for(Restaurant restaurant: restaurantList){
                    if(checkNameSimilarity(restaurant)){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
            //Number 6
            else if (restaurantName.isEmpty() && hazardSearchOn && !violationSearchOn){
                System.out.println("entering number six");
                for(Restaurant restaurant: restaurantList){
                    if(checkHazardLevel(restaurant)){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }
            else{
                System.out.println("entering number seven");
                for(Restaurant restaurant: restaurantList){
                    if(checkNumCritViolations(restaurant)){
                        manager.addToFilteredRestaurantList(restaurant);
                    }
                }
            }

        }
    }

    private boolean checkNameSimilarity(Restaurant restaurant) {
        String actualRestaurantName = restaurant.getName().toLowerCase();
        String searchedWord = searchState.getRestaurantName().toLowerCase();

        if(actualRestaurantName.contains(searchedWord)){
            return true;
        }
        return false;
    }

    private boolean checkHazardLevel(Restaurant restaurant) {
        String actualRestaurantHazardLevel = restaurant.getLatestInspectionHazard(context);
        if(actualRestaurantHazardLevel.equalsIgnoreCase(hazardLevel)){
            return true;
        }
        return false;
    }

    private boolean checkNumCritViolations(Restaurant restaurant) {
        int critViolationsOfActualRestaurant = restaurant.getNumCritViolationsWithinLastYear();
        if(lessOrGreaterThanFlag){
            if(critViolationsOfActualRestaurant >= numOfCriticalViolations){
                return true;
            }
        }
        else{
            if(critViolationsOfActualRestaurant <= numOfCriticalViolations){
                return true;
            }
        }
        return false;
    }

}
