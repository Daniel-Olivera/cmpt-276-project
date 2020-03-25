package ca.cmpt276.restaurantreport.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;

/*
This class is use for creating a Dialog Fragment that show additional
detail about a violation that the user want to see
 */
public class AskForUpdateDialog extends DialogFragment {
    private String restaurantDataURL;
    private String inspectionsDataURL;
    private Context context;
    AskForUpdateDialog(String restaurantDataURL, String inspectionsDataURL, Context context)
    {
        this.restaurantDataURL = restaurantDataURL;
        this.inspectionsDataURL = inspectionsDataURL;
        this.context = context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("Update Available.")
                .setMessage("Would you like to update?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                       // ReadCSV readCSV = ReadCSV.getInstance(context,false);
                        ReadCSV readCSV = new ReadCSV(context,false);
                        startActivity(new Intent(context,MapsActivity.class));
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateActivity.clickedUpdate = true;
                        ProcessData processData = new ProcessData();
                        processData.readRestaurantData(restaurantDataURL, context);
                        processData.readReportData(inspectionsDataURL,context);
                       // ReadCSV readCSV = ReadCSV.getInstance(context,true);

                    }
                });
        return builder.create();
    }
}
