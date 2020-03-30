package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.RestaurantListAdapter;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;


/*
This class show the list of all restaurants in the database
 */
public class MainActivity extends AppCompatActivity {


    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        RestaurantManager manager = RestaurantManager.getInstance(this);

        setupListView(manager);
        setupMapButton();
        setupSearchButton();
    }

    private void setupSearchButton() {
        FloatingActionButton btnSearch = findViewById(R.id.btnFloatSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SearchActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }

    private void setupMapButton() {
        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(v -> {
            Intent intent = MapsActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });

    }

   @Override
   public void onBackPressed(){
        finishAffinity();
    }

    private void setupListView(RestaurantManager manager){

        List<Restaurant> allRestaurantsList = manager.getRestaurants();

        //Sorts all the restaurants by name
        Collections.sort(allRestaurantsList, (firstRes, nextRes) -> firstRes.getName().compareTo(nextRes.getName()));

        String[] restTitles = new String[allRestaurantsList.size()];

        //the adapter constructor requires a string array to work
        for(int i = 0; i < restTitles.length; i++){
           restTitles[i] = allRestaurantsList.get(i).getName();
        }

        RestaurantListAdapter adapter = new RestaurantListAdapter(this, allRestaurantsList, restTitles);
        ListView listView = findViewById(R.id.listRestaurant);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String trackingNum = allRestaurantsList.get(position).getTrackingNum();

            Intent intent = RestaurantActivity.makeIntent(MainActivity.this, trackingNum);
            startActivity(intent);

        });
    }

}
