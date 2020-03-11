package ca.cmpt276.restaurantreport.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Inspection;
import ca.cmpt276.restaurantreport.model.InspectionListAdapter;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantManager;
/*
* cutlery_attr_freepik by Freepik from: https://www.flaticon.com/free-icon/cutlery_263125
* */
public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;
    String resName;
    int totalIssues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        manager = RestaurantManager.getInstance(this);

        Intent intent = getIntent();
        resName = intent.getStringExtra("resName");
        totalIssues = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("totalIssues")));

        List<Restaurant> listRes = manager.getRestaurants();
        int index = 0;
        for (int i = 0 ; i < listRes.size();i++)
        {
            if (listRes.get(i).getName().replace("\"", "").equals(resName))
            {
                int issueCount = 0;
                List<Inspection> insp = listRes.get(i).getInspections();
                for (int y = 0; y < insp.size(); y++) {
                    issueCount += insp.get(y).getTotalIssues();
                }
                if (issueCount == totalIssues) {
                    index = i;
                    break;
                }
            }
        }
        final Restaurant restaurant = manager.get(index);
        DecimalFormat decimalFormat = new DecimalFormat("0",DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        decimalFormat.setMaximumFractionDigits(340);
        // parse out the double quote
        String addr = manager.get(index).getPhysicalAddr().replace("\"", "");
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(resName);
        TextView txtAddress = findViewById(R.id.txtAddress);
        txtAddress.setText(getString(R.string.rest_addr_prefix, addr));
        TextView txtLattitude = findViewById(R.id.txtLattitude);
        txtLattitude.setText(getString(R.string.rest_lat_prefix, Double.toString(manager.get(index).getLatitude())));
        TextView txtLongtitude = findViewById(R.id.txtLongtitude);
        txtLongtitude.setText(getString(R.string.rest_long_prefix, Double.toString(manager.get(index).getLongitude())));
        TextView txtInspHeader = findViewById(R.id.txtInspHeader);
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
            lastInspec[i]=inspectionList.get(i).dayFromLastInspection();
            hazardLevel[i]=inspectionList.get(i).getHazardRating();
        }
        ListView listView;

        listView = findViewById(R.id.lstInspections);

        InspectionListAdapter adapter = new InspectionListAdapter(this,critIssue,nonCritIssue,lastInspec,hazardLevel);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trackingNumber = restaurant.getTrackingNum();
                Intent intent = InspectionActivity.makeIntent(RestaurantActivity.this,trackingNumber,position);
                startActivity(intent);
            }
        });

    }

    public static Intent makeIntent(Context context, String resName, String totalIssues)
    {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("resName", resName);
        intent.putExtra("totalIssues", totalIssues);
        return intent;
    }
}
