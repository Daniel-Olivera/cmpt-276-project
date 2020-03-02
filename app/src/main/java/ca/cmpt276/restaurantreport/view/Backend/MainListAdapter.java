package ca.cmpt276.restaurantreport.view.Backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ca.cmpt276.restaurantreport.R;

/*
MOST OF THE CODE WAS TAKEN/LEARNED FROM
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
public class MainListAdapter {

    Context context;
    private ArrayList<Restaurant> restaurants;

    MainListAdapter(Context c, ArrayList<Restaurant> rest, String[] titles){
        super(c,R.layout.list_row, R.id.txtName, titles);
        this.context = c;
        this.restaurants = rest;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row, parent, false);

        TextView myName = row.findViewById(R.id.txtName);
        TextView myIssues = row.findViewById(R.id.txtIssues);
        TextView myDate = row.findViewById(R.id.txtDate);

        String issuesFound = " Issues Found";
        String inspectDate = "Date: ";

        Restaurant currentRes = restaurants.get(position);

        myName.setText(currentRes.getTitle());
        myIssues.setText(String.format("%s%s", currentRes.getIssues(), issuesFound));
        myDate.setText(String.format("%s%s", currentRes.getDate(), inspectDate));

        return row;
    }
}
