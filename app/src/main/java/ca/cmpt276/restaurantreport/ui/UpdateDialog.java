package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;

import static ca.cmpt276.restaurantreport.ui.UpdateActivity.getWhenLastUpdated;

public class UpdateDialog extends DialogFragment {

    private ProgressBar progressBar;
    private TextView title;
    private Button cancelButton;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private ProcessData processData;
    private Context context;

    private boolean updateCancelled = false;

    public int getProgressStatus() {
        return progressStatus;
    }

    UpdateDialog(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_dialog, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        title = view.findViewById(R.id.dialog_title);
        cancelButton = view.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateActivity.clickedCancel = true;

                updateCancelled = true;

                getDialog().dismiss();
                Intent intent = MapsActivity.makeIntent(context);
                startActivity(intent);
            }
        });
        //textView = (TextView) findViewById(R.id.textView);
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {

                    progressStatus += 2;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });

                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(!updateCancelled) {

                    ReadCSV readCSV = new ReadCSV(context,true,0);
                    LocalDateTime currentTime = LocalDateTime.now();
                    String strCurrentTime = currentTime.toString();
                    saveWhenLastUpdated(strCurrentTime);
                    String dateLastSaved = getWhenLastUpdated(context);
                    ProcessData processData = new ProcessData();
                    processData.saveFinalCopy(context);

                    Intent intent = MapsActivity.makeIntent(context);
                    startActivity(intent);
                }
            }
        }).start();

        return view;
    }

    private void saveWhenLastUpdated(String lastUpdated) {
        SharedPreferences sharedPreferencesLastUpdated = context.getSharedPreferences("Update_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesLastUpdated.edit();
        editor.putString("last_updated",lastUpdated);
        editor.apply();
    }


}
