package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.InspectionListAdapter;
import ca.cmpt276.restaurantreport.applogic.Inspection;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

/*
This class shows the details about the Restaurant
and list of inspection related to it
 */
public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;
    int restaurantIndex = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        manager = RestaurantManager.getInstance(this);

        Intent intent = getIntent();
        String trackingNum = intent.getStringExtra("trackingNumber");

        List<Restaurant> listRes = manager.getRestaurants();

        for(int i = 0; i < listRes.size(); i++){
            assert trackingNum != null;
            if(trackingNum.equals(listRes.get(i).getTrackingNum())){
                restaurantIndex = i;
                break;
            }
        }

        final Restaurant restaurant = listRes.get(restaurantIndex);

        String address = manager.get(restaurantIndex).getPhysicalAddr();

        TextView toolbar_title = findViewById(R.id.tbrSearchTitle);
        TextView txtAddress = findViewById(R.id.txtAddress);
        TextView txtLatitude = findViewById(R.id.txtLattitude);
        TextView txtLongitude = findViewById(R.id.txtLongtitude);
        TextView txtInspHeader = findViewById(R.id.txtInspHeader);

        ImageView favIcon = findViewById(R.id.clickableFavIcon);
        if (restaurant.isFavorite()) {
            favIcon.setImageResource(R.drawable.ic_star_black_24dp);
        }
        else {
            favIcon.setImageResource(R.drawable.ic_star_border_black_24dp);
        }

        toolbar_title.setText(listRes.get(restaurantIndex).getName());
        manager.setText(txtAddress,R.string.rest_addr_prefix,address);
        manager.setText(txtLatitude,R.string.rest_lat_prefix,manager.get(restaurantIndex).getLatitude());
        manager.setText(txtLongitude,R.string.rest_long_prefix,manager.get(restaurantIndex).getLongitude());
        txtInspHeader.setText(R.string.rest_insp_prefix);

        List<Inspection> inspectionList = manager.get(restaurantIndex).getInspections();

        //Sort the inspection list according to date
        Collections.sort(inspectionList,Collections.reverseOrder());

        int []critIssue  = new int [inspectionList.size()];
        int []nonCritIssue = new int [inspectionList.size()];
        String[]lastInspec =  new String[inspectionList.size()];
        String[]hazardLevel = new String[inspectionList.size()];

        for(int i = 0 ; i < inspectionList.size(); i++)
        {
            critIssue[i] = inspectionList.get(i).getNumCritIssues();
            nonCritIssue[i] = inspectionList.get(i).getNumNonCritIssues();
            lastInspec[i]=inspectionList.get(i).getInspectionDateDisplay(this);
            hazardLevel[i]=inspectionList.get(i).getHazardRating();
        }
        ListView listView;

        listView = findViewById(R.id.lstInspections);

        InspectionListAdapter adapter = new InspectionListAdapter(this,critIssue,nonCritIssue,lastInspec,hazardLevel);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String trackingNumber = restaurant.getTrackingNum();
            Intent intent1 = InspectionActivity.makeIntent(RestaurantActivity.this,trackingNumber,position);
            startActivity(intent1);
        });

        setupCoordinatesButton(restaurant);
    }
    // If the favorite icon is clicked
    public void clickFavorite(View V)
    {
        Restaurant restaurant = manager.get(restaurantIndex);
        ImageView favIcon = findViewById(R.id.clickableFavIcon);
        if (restaurant.isFavorite()) {
            restaurant.setFavorite(false);
            favIcon.setImageResource(R.drawable.ic_star_border_black_24dp);
            manager.removeFromFavoriteList(restaurant);

            List<Restaurant> fullList = manager.getFullRestaurantList();
            for (int i = 0; i < fullList.size(); i++) {
                Restaurant tempRestaurant = fullList.get(i);
                if(restaurant.getTrackingNum().equals(tempRestaurant.getTrackingNum()))
                {
                    tempRestaurant.setFavorite(false);
                }
            }
        }
        else {
            restaurant.setFavorite(true);
            favIcon.setImageResource(R.drawable.ic_star_black_24dp);
            manager.addToFavoriteList(restaurant);

            List<Restaurant> fullList = manager.getFullRestaurantList();
            for (int i = 0; i < fullList.size(); i++) {
                Restaurant tempRestaurant = fullList.get(i);
                if(restaurant.getTrackingNum().equals(tempRestaurant.getTrackingNum()))
                {
                    tempRestaurant.setFavorite(true);
                }
            }
        }
        //Update the favoriteList for SharedPreferences
        manager.saveFavoriteList();
    }

    private void setupCoordinatesButton(Restaurant restaurant) {
        Button btnCoords = findViewById(R.id.btnCoords);
        btnCoords.setOnClickListener(v -> {
            Intent coordinatesIntent = MapsActivity.makeIntent(RestaurantActivity.this);

            coordinatesIntent.putExtra("trackingID", restaurant.getTrackingNum());
            startActivity(coordinatesIntent);
        });
    }

    public static Intent makeIntent(Context context, String trackingNum)
    {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("trackingNumber", trackingNum);
        return intent;
    }
}
