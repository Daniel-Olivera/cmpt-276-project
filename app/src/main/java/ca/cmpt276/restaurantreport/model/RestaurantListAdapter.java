package ca.cmpt276.restaurantreport.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ca.cmpt276.restaurantreport.R;

/*
most of the code was learned/taken from:
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
public class RestaurantListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<Restaurant> res;
    private List<Inspection> insp;

    public RestaurantListAdapter(Context c, List<Restaurant> rest, String[] titles){
        super(c,R.layout.list_row, R.id.txtName, titles);
        this.context = c;
        this.res = rest;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.list_row, parent, false);

        TextView myName = row.findViewById(R.id.txtName);
        TextView myIssues = row.findViewById(R.id.txtIssues);
        TextView myDate = row.findViewById(R.id.txtDate);

        Restaurant currentRes = res.get(position);
        insp = res.get(position).getInspections();

        int issueCount = 0;

        for (int i = 0; i < insp.size(); i++) {
            issueCount += insp.get(i).getTotalIssues();
        }

        String issuesFound = issueCount + " Issues Found";

        //TODO display date in intelligent format (i.e. 23 days ago)
        String inspectDate = "Last Inspected: " + currentRes.getLatestInspection();

        myName.setText(currentRes.getName());
        myIssues.setText(issuesFound);
        myDate.setText(inspectDate);

        //TODO change hazard level icon and text

        return row;
    }
}
