package ca.cmpt276.restaurantreport.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import ca.cmpt276.restaurantreport.R;

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
                   case Dialog.BUTTON_NEUTRAL:
                       ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                       TextView textView = (TextView) view.findViewById(R.id.txtUpdateProgress);
                      while(progressStatus < 100){
                          progressStatus++;
                          progressBar.setProgress(progressStatus);
                          textView.setText(progressStatus+"/"+progressBar.getMax());
                      }
                      break;
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("update progress")
                .setView(view)
                .setNegativeButton(R.string.back,listener)
                .setNeutralButton("continue",listener)
                .create();
    }
}
