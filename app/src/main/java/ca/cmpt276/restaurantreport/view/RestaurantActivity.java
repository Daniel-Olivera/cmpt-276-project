package ca.cmpt276.restaurantreport.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ca.cmpt276.restaurantreport.R;

public class RestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }
}
