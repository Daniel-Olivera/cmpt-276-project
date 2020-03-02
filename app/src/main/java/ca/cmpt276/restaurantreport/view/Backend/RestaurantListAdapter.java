package ca.cmpt276.restaurantreport.view.Backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ca.cmpt276.restaurantreport.R;

/*
most of this code was learned/taken from:
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
public class RestaurantListAdapter {

    Context context;
    //TODO: need the Restaurant Class
    private ArrayList<Restaurant> restaurants;

    RestaurantListAdapter(Context c, ArrayList<Restaurant> rest, String[] titles){
        super(c,R.layout.list_row, R.id.txtName, titles);
        this.context = c;
        this.restaurants = rest;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.list_row, parent, false);
        //set the view we are working with


        //get the text views
        TextView restaurantName = row.findViewById(R.id.txtName);
        TextView numIssues = row.findViewById(R.id.txtIssues);
        TextView lastInspectDate = row.findViewById(R.id.txtDate);

        //messages for display
        String issuesFound = " Issues Found";
        String inspectDate = "Date: ";

        //avoid code clutter such as 'restaurants.get(position).getTitle()
        Restaurant currentRes = restaurants.get(position);

        //set the text views to display the proper information
        restaurantName.setText(currentRes.getTitle());
        numIssues.setText(String.format("%s%s", currentRes.getIssues(), issuesFound));
        lastInspectDate.setText(String.format("%s%s", currentRes.getDate(), inspectDate));

        return row;
    }
}
