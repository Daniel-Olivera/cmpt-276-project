package ca.cmpt276.restaurantreport.model;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import ca.cmpt276.restaurantreport.R;

/*
This class is use for creating a Dialog Fragment that show additional
detail about a violation that the user want to see
 */
public class PopUpDialog extends DialogFragment {
    private String message;
    public PopUpDialog(String message)
    {
        this.message = message;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("Details")
                .setMessage(message)
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

}
