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
import androidx.annotation.Nullable;

import ca.cmpt276.restaurantreport.R;

/*
most of the code was learned/taken from:
https://www.youtube.com/watch?v=5Tm--PHhbJo
*/
/*
This class is used for putting data about each inspection
to a Linear Layout that show all the inspection of a restaurant
*/

public class InspectionListAdapter extends ArrayAdapter<String> {
    private int[] critNum;
    private int[] nonCritNum;
    private String[] lastInspec;
    private String[] hazardLevels;

    public InspectionListAdapter(Context c, int[] critNum, int[] nonCritNum, String[] lastInspec, String[] hazardLevel) {
        super(c, R.layout.inspection_row, R.id.txtInspCritNums, lastInspec);
        this.critNum = critNum;
        this.nonCritNum = nonCritNum;
        this.lastInspec = lastInspec;
        this.hazardLevels = hazardLevel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.inspection_row,parent,false);
        TextView txtInspDate = row.findViewById(R.id.txtInspDate);
        TextView txtInspCritNums = row.findViewById(R.id.txtInspCritNums);
        TextView txtInspNCrtiNums = row.findViewById(R.id.txtInspNCrtiNums);
        TextView txtInspHazLvl = row.findViewById(R.id.txtInspHazLvl);

        ImageView imgInspHazIcon = row.findViewById(R.id.imgInspHazIcon);

            setText(txtInspDate, lastInspec[position]);
            if(critNum[position] == 1){
                setText(txtInspCritNums, R.string.crit_postfix, critNum[position]);
            } else {
                setText(txtInspCritNums, R.string.crit_postfix_s, critNum[position]);
            }
            if(nonCritNum[position] == 1){
                setText(txtInspNCrtiNums, R.string.non_crit_postfix,nonCritNum[position]);
            } else {
                setText(txtInspNCrtiNums,R.string.non_crit_postfix_s,nonCritNum[position]);
            }

        txtInspHazLvl.setText(hazardLevels[position]);

        getHazardIcon(hazardLevels[position],imgInspHazIcon);

        return row;
    }

    private void setText(TextView textBox, String arrayItem){
        textBox.setText(getContext().getString(R.string.insp_date_prefix, arrayItem));
    }

    private void setText(TextView textBox, int stringResID, int arrayItem ){
        textBox.setText(getContext().getString(stringResID, Integer.toString(arrayItem)));
    }

    private void getHazardIcon(String hazardLevel, ImageView icon) {
        switch (hazardLevel) {
            case ("Low"):
            default: {
                icon.setImageResource(R.drawable.low);
                break;
            }
            case ("Moderate"): {
                icon.setImageResource(R.drawable.medium);
                break;
            }
            case ("High"): {
                icon.setImageResource(R.drawable.high);
                break;
            }
        }
    }
}