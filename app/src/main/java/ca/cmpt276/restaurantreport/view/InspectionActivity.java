package ca.cmpt276.restaurantreport.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Inspection;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantManager;
import ca.cmpt276.restaurantreport.model.ShortViolation;
import ca.cmpt276.restaurantreport.model.Violation;
import ca.cmpt276.restaurantreport.model.ViolationListAdapter;

public class InspectionActivity extends AppCompatActivity {

    RestaurantManager manager;
    String trackingNumber;
    int inspectionPosition;
    int totalIssues;

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
        setupListView();
    }

    //Displays the Date, number of Critical and non-Critical issues and hazard Level
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
        //So it's easier to figure out which inspection was clicked from the previous activity
        //as the inspections were sorted according to recent Date first
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
        String inspectionDateFull = inspectionDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"));

        //set the texts for Date, Crit Issues and Non Crit Issues and hazard level
        TextView dateTextView = findViewById(R.id.txtDate);
        dateTextView.setText(inspectionDateFull);

        String inspectionType = inspectionList.get(inspectionPosition).getInspectionType().replace("\"", "");
        TextView inspectionTypeTextView = findViewById(R.id.txtInspectionType);
        setText(inspectionTypeTextView, inspectionType);

        int criticalIssues = inspectionList.get(inspectionPosition).getNumCritIssues();
        TextView criticalIssuesTextView = findViewById(R.id.txtCriticalIssues);
        if(criticalIssues == 1){
            setText(criticalIssuesTextView, R.string.crit_postfix, criticalIssues);
        } else {
            setText(criticalIssuesTextView, R.string.crit_postfix_s, criticalIssues);
        }

        int nonCriticalIssues = inspectionList.get(inspectionPosition).getNumNonCritIssues();
        TextView nonCriticalIssuesTextView = findViewById(R.id.txtNonCriticalIssues);
        if(nonCriticalIssues == 1){
            setText(nonCriticalIssuesTextView, R.string.non_crit_postfix,nonCriticalIssues);
        } else {
            setText(nonCriticalIssuesTextView,R.string.non_crit_postfix_s,nonCriticalIssues);
        }

        String hazardLevel = inspectionList.get(inspectionPosition).getHazardRating().replace("\"","");
        TextView hazardLevelTextView = findViewById(R.id.txtHazardLevel);
        hazardLevelTextView.setText(hazardLevel);

        ImageView hazardLevelIcon = findViewById(R.id.imgHazardIcon);
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

        //Incase there are no issues we won't need to setup the listView
        totalIssues = criticalIssues + nonCriticalIssues;
    }

    private void setupListView() {
        //No need to setup ListView if there are no issues
        if(Objects.equals(totalIssues,0)){
            return;
        }

        List<Restaurant> restaurants = manager.getRestaurants();
        Restaurant restaurant = manager.get(0);

        for(Restaurant r : restaurants) {
            if(r.getTrackingNum().equalsIgnoreCase(trackingNumber)) {
                restaurant = r;
            }
        }

        final List<Inspection> inspectionList = restaurant.getInspections();

        //Sort the inspection list according to date
        //So it's easier to figure out which inspection was clicked from the previous activity
        //as the inspections were sorted according to recent Date first
        Collections.sort(inspectionList,Collections.reverseOrder());

        final List<Violation> violationList = inspectionList.get(inspectionPosition).getViolations();

        List<ShortViolation> shortViolationList = new ArrayList<>();

        //For the violationList of the inspection creating a shortViolation List for use in the listView
        for(Violation violation: violationList) {
            String sampleViolationCode = violation.getViolationCode().replace("\"","");
            int violationCode;
            if(sampleViolationCode.equalsIgnoreCase("")){
                violationCode = 0;
            }
            else{
                violationCode = Integer.parseInt(sampleViolationCode);
            }

            ShortViolation shortViolation = manager.getShortViolation(violationCode);
            shortViolationList.add(shortViolation);
        }

        //Required arrays for use in the ListView
        int [] violationCodes = new int[violationList.size()];
        String[] shortDescription = new String[violationList.size()];
        String [] violationCriticalities = new String[violationList.size()];


        int index = 0;//index for accessing the long violation to get criticality as shortViolation object doesn't have criticality field
        for(ShortViolation s:shortViolationList) {
            violationCodes[index] = s.getViolationCode();
            shortDescription[index] = s.getShortDescriptor();
            violationCriticalities[index] = violationList.get(index).getViolationCriticality();
            index++;
        }

        ViolationListAdapter adapter = new ViolationListAdapter(this, violationCodes,shortDescription,violationCriticalities);
        ListView listView = findViewById(R.id.violationListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void setText(TextView textBox, int stringResID, int item ){
        textBox.setText(getString(stringResID, Integer.toString(item)));
    }

    private void setText(TextView textBox, String item){
        textBox.setText(getString(R.string.inpect_type_prefix, item));
    }
}
