package ca.cmpt276.restaurantreport.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.RestaurantListAdapter;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

/*
dinner_attr_freepik by Freepik from: https://www.flaticon.com/free-icon/dinner_272186
Green diamond hazard Icon from http://clipart-library.com/clipart/2019534.htm
Red and Yellow diamonds are edited by dolivera from the green one
 */
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
            String resName = ((TextView) view.findViewById(R.id.txtRestaurantName)).getText().toString();
            String totalIssues = ((TextView) view.findViewById(R.id.txtNumOfIssues)).getText().toString();

            //parse out the number part
            totalIssues = totalIssues.substring(0,totalIssues.indexOf(" "));
            Intent intent = RestaurantActivity.makeIntent(MainActivity.this,resName,totalIssues);
            startActivity(intent);

        });
    }

}
