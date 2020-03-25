package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
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

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.ProcessData;
import ca.cmpt276.restaurantreport.applogic.ReadCSV;

public class UpdateDialog extends DialogFragment {

    private ProgressBar progressBar;
    private TextView title;
    private Button cancelButton;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private ProcessData processData;
    private Context context;

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
                System.out.println("clicked cancel on progress");
               // ReadCSV readCSV = ReadCSV.getInstance(context,false);
                ReadCSV readCSV = new ReadCSV(context,false);

                getDialog().dismiss();
            }
        });
        //textView = (TextView) findViewById(R.id.textView);
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 3;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            //textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();



        return view;
    }


}
