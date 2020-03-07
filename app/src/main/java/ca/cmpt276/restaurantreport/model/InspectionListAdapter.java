package ca.cmpt276.restaurantreport.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ca.cmpt276.restaurantreport.R;

/*
most of the code was learned/taken from:
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
public class InspectionListAdapter extends ArrayAdapter<String> {
    Context context;
    int critNum[];
    int nonCritNum[];
    String lastInspec[];
    String hazardLevels[];


    public InspectionListAdapter(Context c, int critNum[], int nonCritNum[], String lastInspec[], String hazardLevel[]) {
        super(c, R.layout.inspection_row, R.id.numCritical, lastInspec);
        this.context = c;
        this.critNum = critNum;
        this.nonCritNum = nonCritNum;
        this.lastInspec = lastInspec;
        this.hazardLevels = hazardLevel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Setup the layout
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.inspection_row, parent, false);
        TextView date = row.findViewById(R.id.inspectorReport);
        TextView numCritical = row.findViewById(R.id.numCritical);
        TextView numNonCritical = row.findViewById(R.id.numNonCritical);
        TextView hazard = row.findViewById(R.id.inspectionHazardLevel);
        ImageView hazardLevel = row.findViewById(R.id.inspectionHazardIcon);

        //Modify the appropriate layout
        date.setText("Date: " + lastInspec[position]);
        numCritical.setText("Number of critical issue: " + critNum[position]);
        numNonCritical.setText("Number of non-critical issue: " + nonCritNum[position]);
        hazard.setText(hazardLevels[position].replace("\"", ""));
        getHazardIcon(hazardLevels[position], hazardLevel);

        return row;
    }


    private void getHazardIcon(String hazardLevel, ImageView icon) {
        switch (hazardLevel) {
            case ("\"Low\""):
            default: {
                icon.setImageResource(R.drawable.low);
                break;
            }
            case ("\"Moderate\""): {
                icon.setImageResource(R.drawable.medium);
                break;
            }
            case ("\"High\""): {
                icon.setImageResource(R.drawable.high);
                break;
            }
        }
    }
}