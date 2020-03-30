package ca.cmpt276.restaurantreport.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.restaurantreport.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupSearchButton();
        setupClearButton();
        setupRadioButtons();
        setupSearchBar();
        setupToggleSwitch();
        setupViolationInput();
        setupHazardCheckBox();
        setupViolationCheckBox();
        setupFavouriteCheckBox();
    }

    private void setupFavouriteCheckBox() {
        CheckBox chkFav = findViewById(R.id.chkBoxFav);
        if(chkFav.isChecked()){
            //TODO: send to search state class
        }
        else{
            //TODO: send to search state class
        }
    }

    @SuppressLint("ResourceType")
    private void setupViolationCheckBox() {
        CheckBox chkViolation = findViewById(R.id.chkBoxViolation);
        Switch lessOrGreaterThanSwitch = findViewById(R.id.valueSwitch);
        TextView txtGreaterThan = findViewById(R.id.txtGreaterThan);
        EditText violationNum = findViewById(R.id.edtxtNumViolations);

        if(chkViolation.isChecked()){
            txtGreaterThan.setTextColor(Color.BLACK);
            lessOrGreaterThanSwitch.setEnabled(true);
            violationNum.setEnabled(true);
        }
        else{
            txtGreaterThan.setTextColor(Color.LTGRAY);
            lessOrGreaterThanSwitch.setEnabled(false);
            violationNum.setEnabled(false);
        }

        chkViolation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                txtGreaterThan.setTextColor(Color.BLACK);
                lessOrGreaterThanSwitch.setEnabled(true);
                violationNum.setEnabled(true);
                //TODO: tell Search state class to use the values

            }
            else{
                txtGreaterThan.setTextColor(Color.LTGRAY);
                lessOrGreaterThanSwitch.setEnabled(false);
                violationNum.setEnabled(false);
                //TODO: tell Search state class to not use the values
            }
        });
    }

    private void setupHazardCheckBox() {
        RadioGroup hazardRadioGroup = findViewById(R.id.radioHazSelection);
        CheckBox chkHaz = findViewById(R.id.chkBoxHazard);

        if(chkHaz.isChecked()){
            for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                hazardRadioGroup.getChildAt(i).setEnabled(true);
            }
        }
        else{
            for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                hazardRadioGroup.getChildAt(i).setEnabled(false);
            }
        }

        chkHaz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                    hazardRadioGroup.getChildAt(i).setEnabled(true);
                }
                //TODO: tell search state class
            } else {
                for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                    hazardRadioGroup.getChildAt(i).setEnabled(false);
                }
                //TODO: tell search state class
            }
        });

    }

    private void setupViolationInput() {
        EditText violationNum = findViewById(R.id.edtxtNumViolations);
        String numCritViolations = violationNum.getText().toString();
        //TODO: send to search state class
    }

    private void setupToggleSwitch() {
        Switch lessOrGreaterThanSwitch = findViewById(R.id.valueSwitch);

        lessOrGreaterThanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                //TODO: set greater than...
            }
            else{
                //TODO: set less than...
            }
        });
    }

    private void setupSearchBar() {
        EditText restaurantSearch = findViewById(R.id.edtxtResName);
        String restaurantName = restaurantSearch.getText().toString();
        //TODO: send to search state class
    }

    private void setupRadioButtons() {
        RadioGroup hazardRadioGroup = findViewById(R.id.radioHazSelection);

        hazardRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = group.findViewById(checkedId);
                boolean isChecked = checkedButton.isChecked();

                if(isChecked){
                    //TODO: set the values in the search state class
                    Toast.makeText(SearchActivity.this, checkedButton.getText(), Toast.LENGTH_SHORT)
                    .show();
                }
            }
        });

    }

    private void setupSearchButton() {
        Button searchBtn = findViewById(R.id.btnSearch);

        searchBtn.setOnClickListener(v -> {
            //TODO: Apply search settings and finish the activity
            finish();
        });
    }

    private void setupClearButton() {
        Button clearBtn = findViewById(R.id.btnClear);

        clearBtn.setOnClickListener(v -> {
            //TODO: Clear search options and finish the activity to go back
            finish();
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, SearchActivity.class);
    }

}
