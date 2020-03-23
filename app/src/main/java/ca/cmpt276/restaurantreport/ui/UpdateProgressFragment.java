package ca.cmpt276.restaurantreport.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;

public class UpdateProgressFragment extends AppCompatDialogFragment {

    int progressStatus = 0;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.update_progress,null);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               switch(which) {
                   case Dialog.BUTTON_NEGATIVE:
                       ReadCSV readCSV = ReadCSV.getInstance(getContext(),false);

                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("update progress")
                .setView(view)
                .setNegativeButton(R.string.back,listener)
                .setNegativeButton("Cancel",listener)
                .create();
    }
}
