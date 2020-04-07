package ca.cmpt276.restaurantreport.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

/*
This class is use for creating a Dialog Fragment that show additional
detail about a violation that the user want to see
 */
public class AskForUpdateDialog extends DialogFragment {
    private String restaurantDataURL;
    private String inspectionsDataURL;
    private Context context;
    private boolean clickedUpdate = false;
    private ReadCSV readCSV;
    RestaurantManager manager;
    AskForUpdateDialog(String restaurantDataURL, String inspectionsDataURL, Context context)
    {
        this.restaurantDataURL = restaurantDataURL;
        this.inspectionsDataURL = inspectionsDataURL;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestaurantManager.getInstance(context);
        readCSV = new ReadCSV(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(R.string.update_available)
                .setMessage(R.string.update_ask)
                .setNegativeButton(R.string.update_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            readCSV.getCSVData(context, false, -1);
                    }
                })
                .setPositiveButton(R.string.update_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateActivity.clickedUpdate = true;
                        clickedUpdate = true;
                        UpdateDialog updateDialog = new UpdateDialog(context);
                        updateDialog.setCancelable(false);
                        FragmentManager fragmentManager = getFragmentManager();
                        assert fragmentManager != null;
                        updateDialog.show(fragmentManager, "UpdateDialog");

                        ProcessData processData = new ProcessData();
                        processData.readRestaurantData(restaurantDataURL, context);
                        processData.readReportData(inspectionsDataURL,context);
                    }
                });

        return builder.create();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(!clickedUpdate) {
            Intent intent = MapsActivity.makeIntent(context);
            startActivity(intent);
        }

    }

}
