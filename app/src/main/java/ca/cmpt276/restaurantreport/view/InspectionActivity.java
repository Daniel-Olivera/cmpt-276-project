package ca.cmpt276.restaurantreport.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Inspection;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

public class InspectionActivity extends AppCompatActivity {

    RestaurantManager manager;
    String trackingNumber;
    int inspectionPosition;

    public static Intent makeIntent(Context context, String trackingNumber,int inspectionPosition)
    {
        Intent intent = new Intent(context, InspectionActivity.class);
        intent.putExtra("trackingNumber", trackingNumber);
        intent.putExtra("inspectionPosition",inspectionPosition);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        manager = RestaurantManager.getInstance(this);

        Intent intent = getIntent();
        trackingNumber = intent.getStringExtra("trackingNumber");
        inspectionPosition = intent.getIntExtra("inspectionPosition",0);

        updateUI();

    }

    private void updateUI() {
        List<Restaurant> restaurants = manager.getRestaurants();
        Restaurant restaurant = manager.get(0);

        for(Restaurant r : restaurants) {
            if(r.getTrackingNum().equalsIgnoreCase(trackingNumber)) {
                restaurant = r;
            }
        }

        final List<Inspection> inspectionList = restaurant.getInspections();

        //Sort the inspection list according to date
        Collections.sort(inspectionList,Collections.reverseOrder());

        int date = inspectionList.get(inspectionPosition).getDate();

        //convert the inspection date to string
        String dateOfInspection = Integer.toString(date);

        //format the inspection date
        LocalDate inspectionDate = null;
        try {
            inspectionDate = LocalDate.parse(dateOfInspection, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            Log.d("InspectionActivity","String cannot be parsed into LocalDate");
            e.printStackTrace();
        }

        //get values of month, day and year of the inspection
        assert inspectionDate != null;
        int inspectionDay = inspectionDate.getDayOfMonth();
        int inspectionYear = inspectionDate.getYear();
        Month inspectionMonth = inspectionDate.getMonth();

        TextView dateTextView = (TextView) findViewById(R.id.txtDate);
        dateTextView.setText("" + inspectionDay +" " + inspectionMonth + " " + inspectionYear);

        String inspectionType = inspectionList.get(inspectionPosition).getInspectionType().replace("\"", "");
        TextView inspectionTypeTextView = (TextView) findViewById(R.id.txtInspectionType);
        inspectionTypeTextView.setText("Inspection Type : " + inspectionType);

        int criticalIssues = inspectionList.get(inspectionPosition).getNumCritIssues();
        TextView criticalIssuesTextView = (TextView) findViewById(R.id.txtCriticalIssues);
        criticalIssuesTextView.setText("" + criticalIssues +" Critical Issues");

        int nonCriticalIssues = inspectionList.get(inspectionPosition).getNumNonCritIssues();
        TextView nonCriticalIssuesTextView = (TextView) findViewById(R.id.txtNonCriticalIssues);
        nonCriticalIssuesTextView.setText("" + nonCriticalIssues +" Non-Critical Issues");

        String hazardLevel = inspectionList.get(inspectionPosition).getHazardRating().replace("\"","");
        TextView hazardLevelTextView = (TextView) findViewById(R.id.txtHazardLevel);
        hazardLevelTextView.setText("" + hazardLevel);

        ImageView hazardLevelIcon = (ImageView) findViewById(R.id.imgHazardIcon);
        switch(hazardLevel){
            case("Low"):
            default:{
                hazardLevelIcon.setImageResource(R.drawable.low);
                break;
            }
            case("Moderate"):{
                hazardLevelIcon.setImageResource(R.drawable.medium);
                break;
            }
            case("High"):{
                hazardLevelIcon.setImageResource(R.drawable.high);
                break;
            }
        }
    }

}
