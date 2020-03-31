package ca.cmpt276.restaurantreport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;


/*
This class is used for putting data about each Restaurant
to a Linear Layout that show all the Restaurant in a database
 */
public class RestaurantListAdapter extends ArrayAdapter<String>{

    private List<Restaurant> res;
    private Context context;
    private RestaurantManager manager;

    public RestaurantListAdapter (Context c, List<Restaurant> rest, String[] titles){
        super(c,R.layout.restaurant_row, R.id.txtRestaurantName, titles);
        this.res = rest;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //Sets up which layout is being modified
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;

        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.restaurant_row,
                parent, false);

        TextView txtRestaurantName = row.findViewById(R.id.txtRestaurantName);
        TextView txtNumOfIssues = row.findViewById(R.id.txtNumOfIssues);
        TextView txtInspectionDate = row.findViewById(R.id.txtInspectionDate);
        TextView txtHazardLevel = row.findViewById(R.id.txtHazardLevel);



        //Get the current restaurant information for the appropriate row and its inspections
        Restaurant currentRestaurant = res.get(position);

        int issueCount = currentRestaurant.getMostRecentIssues();
        manager = RestaurantManager.getInstance(context);

        String lastInspected = getLatestInspectionDate(currentRestaurant);
        String hazardText = currentRestaurant.getLatestInspectionHazard(context);

        //set the texts with the right parameters
        txtRestaurantName.setText(currentRestaurant.getName());
        if(issueCount == 1){
            manager.setText(txtNumOfIssues,R.string.lastInspect_found_issue,issueCount);
        }else{
            manager.setText(txtNumOfIssues,R.string.lastInspect_found_issue_s,issueCount);
        }

        manager.setText(txtInspectionDate,R.string.lastInspect_date,lastInspected);
        if(hazardText.equals("Moderate")){
            txtHazardLevel.setText(context.getString(R.string.restaurant_hazard_mid));
        }else{
            manager.setHazardLevelText(txtHazardLevel,hazardText);
        }



        //changes the hazard icon based on the hazard level
        ImageView hazIcon = row.findViewById(R.id.imgHazardIcon);
        manager.setHazardIcon(hazardText,hazIcon);
        ImageView restaurantIcon = row.findViewById(R.id.imgRestaurantIcon);
        getRestaurantIcon(currentRestaurant.getName(), restaurantIcon);

        return row;
    }

    private void getRestaurantIcon(String name, ImageView restaurantIcon) {

        if(name.contains("Subway ")){
            restaurantIcon.setImageResource(R.drawable.subway_logo);
        }
        else if(name.contains("Tim Horton")){
            restaurantIcon.setImageResource(R.drawable.tim_hortons_logo);
        }
        else if(name.contains("Starbucks Coffee")) {
            restaurantIcon.setImageResource(R.drawable.starbucks_logo);
        }
        else if(name.contains("7-Eleven")){
            restaurantIcon.setImageResource(R.drawable.seven_eleven_logo);
        }
        else if(name.contains("McDonald's")){
            restaurantIcon.setImageResource(R.drawable.mcdonald_logo);
        }
        else if(name.contains("A&W")){
            restaurantIcon.setImageResource(R.drawable.aw_logo);
        }
        else if(name.contains("Save On Foods")){
            restaurantIcon.setImageResource(R.drawable.save_on_foods_logo);
        }
        else if(name.contains("Blenz")){
            restaurantIcon.setImageResource(R.drawable.blenz_logo);
        }
        else if(name.contains("Dairy Queen")){
            restaurantIcon.setImageResource(R.drawable.dairy_queen_logo);
        }
        else if(name.contains("Panago")){
            restaurantIcon.setImageResource(R.drawable.panago_logo);
        }

    }

    private String getLatestInspectionDate(Restaurant restaurant) {
        //get the latest inspection of the current restaurant
        int dateOfLastInspection = restaurant.getLatestInspectionDate();
        return manager.getDisplayDate(dateOfLastInspection);
    }
}
