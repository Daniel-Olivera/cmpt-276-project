package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.ViolationListAdapter;
import ca.cmpt276.restaurantreport.applogic.Inspection;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;
import ca.cmpt276.restaurantreport.applogic.ShortViolation;
import ca.cmpt276.restaurantreport.applogic.Violation;

/*
This class show the details about the inspection
and list of violation related to it

 */

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

        String dateOfInspection = Integer.toString(date);

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
        TextView txtDate = findViewById(R.id.txtDate);
        txtDate.setText(inspectionDateFull);

        String inspectionType = inspectionList.get(inspectionPosition).getInspectionType();
        TextView txtInspectionType = findViewById(R.id.txtInspectionType);
        setText(txtInspectionType, inspectionType);

        int criticalIssues = inspectionList.get(inspectionPosition).getNumCritIssues();
        TextView txtCriticalIssues = findViewById(R.id.txtCriticalIssues);
        if(criticalIssues == 1){
            setText(txtCriticalIssues, R.string.crit_postfix, criticalIssues);
        } else {
            setText(txtCriticalIssues, R.string.crit_postfix_s, criticalIssues);
        }

        int nonCriticalIssues = inspectionList.get(inspectionPosition).getNumNonCritIssues();
        TextView txtNonCriticalIssues = findViewById(R.id.txtNonCriticalIssues);
        if(nonCriticalIssues == 1){
            setText(txtNonCriticalIssues, R.string.non_crit_postfix,nonCriticalIssues);
        } else {
            setText(txtNonCriticalIssues,R.string.non_crit_postfix_s,nonCriticalIssues);
        }

        String hazardLevel = inspectionList.get(inspectionPosition).getHazardRating();
        TextView txtHazardLevel = findViewById(R.id.txtHazardLevel);
        txtHazardLevel.setText(hazardLevel);

        ImageView imgHazardIcon = findViewById(R.id.imgHazardIcon);
        switch(hazardLevel){
            case("Low"):
            default:{
                imgHazardIcon.setImageResource(R.drawable.haz_low);
                break;
            }
            case("Moderate"):{
                imgHazardIcon.setImageResource(R.drawable.haz_medium);
                break;
            }
            case("High"):{
                imgHazardIcon.setImageResource(R.drawable.haz_high);
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
            String sampleViolationCode = violation.getViolationCode();
            int violationCode;
            if(sampleViolationCode.isEmpty()){
                violationCode = 0;
            }
            else{
                violationCode = Integer.parseInt(sampleViolationCode);
            }
            ShortViolation shortViolation = manager.getShortViolation(violationCode);
            shortViolationList.add(shortViolation);
        }

        int [] violationCodes = new int[violationList.size()];
        String[] shortDescription = new String[violationList.size()];
        String [] violationCriticalities = new String[violationList.size()];


        int index = 0;//index for accessing the long violation to get criticality as shortViolation object doesn't have criticality field
        for(ShortViolation shortViolation:shortViolationList) {
            violationCodes[index] = shortViolation.getViolationCode();
            shortDescription[index] = shortViolation.getShortDescriptor();
            violationCriticalities[index] = violationList.get(index).getViolationCriticality();
            index++;
        }

        ViolationListAdapter adapter = new ViolationListAdapter(this, violationCodes,shortDescription,violationCriticalities);
        ListView listView = findViewById(R.id.violationListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            FragmentManager fm = getSupportFragmentManager();
            PopUpDialog popUp = new PopUpDialog(violationList.get(position).getDescription());

            popUp.show(fm, "pop_up_dialog");
        });
    }

    private void setText(TextView textBox, int stringResID, int item ){
        textBox.setText(getString(stringResID, Integer.toString(item)));
    }

    private void setText(TextView textBox, String item){
        textBox.setText(getString(R.string.inpect_type_prefix, item));
    }
}
