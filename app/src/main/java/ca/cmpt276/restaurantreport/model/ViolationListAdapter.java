package ca.cmpt276.restaurantreport.model;

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

import java.util.Objects;

import ca.cmpt276.restaurantreport.R;

/*
This class is used for putting data about each violation
to a Linear Layout that show all the violation of a Inspection
 */
public class ViolationListAdapter extends ArrayAdapter<String> {

    private int [] violationCodes;
    private String[] shortDescriptions;
    private String [] violationCriticalities;

    public ViolationListAdapter(@NonNull Context context, int [] violationCodes, String[] shortDescriptions, String[] violationCriticalities) {
        super(context, R.layout.violation_row,R.id.txtShortDescription,shortDescriptions);
        this.violationCodes = violationCodes;
        this.shortDescriptions = shortDescriptions;
        this.violationCriticalities = violationCriticalities;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.violation_row, parent, false);

        TextView shortDescription = row.findViewById(R.id.txtShortDescription);
        TextView criticalityText = row.findViewById(R.id.txtCriticality);

        ImageView violationNature = row.findViewById(R.id.violationNatureIcon);
        ImageView criticalityIcon = row.findViewById(R.id.imgCriticalityIcon);

        shortDescription.setText(shortDescriptions[position].replace("\"",""));
        criticalityText.setText(violationCriticalities[position].replace("\"", ""));

        setCriticalityIcon(violationCriticalities[position],criticalityIcon);

        setViolationNatureIcon(violationCodes[position],violationNature);

        return row;
    }

    private void setCriticalityIcon(String violationCriticality, ImageView criticalityIcon) {
        switch(violationCriticality) {
            case ("Critical"):
                criticalityIcon.setImageResource(R.drawable.critical);
                break;
            case("Not Critical"):
                criticalityIcon.setImageResource(R.drawable.non_critical);
                break;
        }
    }


    private void setViolationNatureIcon(int violationCode, ImageView violationNature) {
        if((violationCode > 100) && (violationCode < 105)){
            violationNature.setImageResource(R.drawable.premise_coloured);
        }
        else if(((violationCode > 202) && (violationCode < 207)) || (Objects.equals(violationCode,211))) {
            violationNature.setImageResource(R.drawable.temp_coloured);
        }
        else if ((violationCode > 200) && (violationCode < 213)){
            violationNature.setImageResource(R.drawable.food);
        }
        else if (((violationCode > 303) && (violationCode < 306)) || (Objects.equals(violationCode,313))){
            violationNature.setImageResource(R.drawable.pest);
        }
        else if(((violationCode > 300) && (violationCode < 309)) || (Objects.equals(violationCode,311)) || (Objects.equals(violationCode,315))){
            violationNature.setImageResource(R.drawable.equipment);
        }
        else if((violationCode > 308) && (violationCode < 315)) {
            violationNature.setImageResource(R.drawable.liquid_coloured);
        }
        else if(violationCode > 400) {
            violationNature.setImageResource(R.drawable.employee_coloured);
        }
        else{
            violationNature.setImageResource(R.drawable.document_coloured);
        }

    }
}
