package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
This class show the details about the Restaurant
and list of inspection related to it
 */
public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        manager = RestaurantManager.getInstance(this);

        Intent intent = getIntent();
        String trackingNum = intent.getStringExtra("trackingNumber");

        List<Restaurant> listRes = manager.getRestaurants();
        int index = 0;

        for(int i = 0; i < listRes.size(); i++){
            assert trackingNum != null;
            if(trackingNum.equals(listRes.get(i).getTrackingNum())){
                index = i;
                break;
            }
        }

        final Restaurant restaurant = listRes.get(index);

        String address = manager.get(index).getPhysicalAddr();

        TextView toolbar_title = findViewById(R.id.tbrSearchTitle);
        TextView txtAddress = findViewById(R.id.txtAddress);
        TextView txtLatitude = findViewById(R.id.txtLattitude);
        TextView txtLongitude = findViewById(R.id.txtLongtitude);
        TextView txtInspHeader = findViewById(R.id.txtInspHeader);


        toolbar_title.setText(listRes.get(index).getName());
        manager.setText(txtAddress,R.string.rest_addr_prefix,address);
        manager.setText(txtLatitude,R.string.rest_lat_prefix,manager.get(index).getLatitude());
        manager.setText(txtLongitude,R.string.rest_long_prefix,manager.get(index).getLongitude());
        txtInspHeader.setText(R.string.rest_insp_prefix);

        List<Inspection> inspectionList = manager.get(index).getInspections();

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
