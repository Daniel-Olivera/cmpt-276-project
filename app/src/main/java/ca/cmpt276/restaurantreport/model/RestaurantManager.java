package ca.cmpt276.restaurantreport.model;

import java.util.List;

public class RestaurantManager {
    private List<Restaurant> restaurantList;
    private static RestaurantManager instance;
    private RestaurantManager()
    {
    }
    public static RestaurantManager getInstance()
    {
        if (instance == null)
        {
            instance = new RestaurantManager();
        }
        return instance;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }
}
