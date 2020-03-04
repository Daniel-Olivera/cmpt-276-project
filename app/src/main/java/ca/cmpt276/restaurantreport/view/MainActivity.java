package ca.cmpt276.restaurantreport.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

public class MainActivity extends AppCompatActivity {

    RestaurantManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = RestaurantManager.getInstance(this);

        //debugging purposes
        for(Restaurant r: manager) {
            System.out.println("" + r);
        }

        // testing purpose
        startActivity(new Intent(MainActivity.this,RestaurantActivity.class));
    }

}
