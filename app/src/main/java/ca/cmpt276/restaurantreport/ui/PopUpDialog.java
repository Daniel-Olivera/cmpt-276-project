package ca.cmpt276.restaurantreport.ui;

import android.app.Dialog;
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
    PopUpDialog(String message)
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
        builder.setTitle(R.string.popup_title)
                .setMessage(message)
                .setNegativeButton(R.string.back, (dialog, id) -> {

                });
        return builder.create();
    }
}
