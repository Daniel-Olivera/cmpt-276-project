package ca.cmpt276.restaurantreport.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Inspection;
import ca.cmpt276.restaurantreport.model.InspectionListAdapter;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantListAdapter;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;
    String resName;
    int totalIssues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        manager = RestaurantManager.getInstance(this);

        // get Restaurant name and total issues
        Intent intent = getIntent();
        resName = intent.getStringExtra("resName");
        totalIssues = Integer.parseInt(intent.getStringExtra("totalIssues"));

        //get restaurantList then find the index of the restaurant
        List<Restaurant> listRes = manager.getRestaurants();
        int index = 0;
        for (int i = 0 ; i < listRes.size();i++)
        {
            if (listRes.get(i).getName().replace("\"", "").equals(resName))
            {
                int issueCount = 0;
                List<Inspection> insp = listRes.get(i).getInspections();
                //Compare total issues to get the right restaurant
                for (int y = 0; y < insp.size(); y++) {
                    issueCount += insp.get(y).getTotalIssues();
                }
                if (issueCount == totalIssues) {
                    index = i;
                    break;
                }
                else
                    continue;
            }
        }

        // get the restaurant
        Restaurant restaurant = manager.get(index);
        // parse out the double quote
        String addr = restaurant.getPhysicalAddr().replace("\"", "");
        //Modify the appropriate data
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(resName);
        TextView address = findViewById(R.id.address);
        address.setText("Address: " + addr);
        TextView latitude = findViewById(R.id.latitude);
        latitude.setText("Latitude: " + restaurant.getLatitude());
        TextView longitude = findViewById(R.id.longtitude);
        longitude.setText("Longitude: " + restaurant.getLongitude());
        TextView inspection = findViewById(R.id.inspection);
        inspection.setText("Inspection:");

        // Get inspection list
        List<Inspection> inspectionList = restaurant.getInspections();

        //Sort the inspection list according to date
        Collections.sort(inspectionList,Collections.reverseOrder());

        // Adding values to appropriate string
        int []critIssue  = new int [inspectionList.size()];
        int []nonCritIssue = new int [inspectionList.size()];
        String[]lastInspec =  new String[inspectionList.size()];
        String[]hazardLevel = new String[inspectionList.size()];
        for(int i = 0 ; i < inspectionList.size(); i++)
        {
            critIssue[i] = inspectionList.get(i).getNumCritIssues();
            nonCritIssue[i] = inspectionList.get(i).getNumNonCritIssues();
            lastInspec[i]=inspectionList.get(i).dayFromLastInspection();
            hazardLevel[i]=inspectionList.get(i).getHazardRating();
        }

        ListView listView = findViewById(R.id.listView);
        //Set up adapter for the scrollable inspection list
        InspectionListAdapter adapter = new InspectionListAdapter(this,critIssue,nonCritIssue,lastInspec,hazardLevel);
        listView.setAdapter(adapter);

        //TODO: Set up on click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"clickedd hahahaha",Toast.LENGTH_SHORT).show();
                }
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"123 hahahaha",Toast.LENGTH_SHORT).show();
                }
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"456  hahahaha",Toast.LENGTH_SHORT).show();
                }
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"789 hahahaha",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Passing value from MainActivity
    public static Intent makeIntent(Context context, String resName, String totalIssues)
    {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("resName", resName);
        intent.putExtra("totalIssues", totalIssues);
        return intent;
    }



}
