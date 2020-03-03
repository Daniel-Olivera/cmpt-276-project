package ca.cmpt276.restaurantreport.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

/*
restaurant icon from http://clipart-library.com/clipart/183878.htm
Green diamond hazard Icon from http://clipart-library.com/clipart/2019534.htm
Red and Yellow diamonds are edited by dolivera from the green one
 */
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

        Manager manager = Manager.getInstance();

//        for(int i = 0; i < title.length; i++){
//            manager.add(new Restaurant(title[i], issues[i], date[i]));
//        }

        setupListView(manager);
    }

    private void setupListView(Manager manager){
        //setup the adapter for the list view
        RestaurantListAdapter adapter = new RestaurantListAdapter(this, manager.restaurants, manager.getTitles());
        ListView listView = findViewById(R.id.restaurantList);
        listView.setAdapter(adapter);

        //TODO: allow user to click a restaurant and go to new activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

}
