package ca.cmpt276.restaurantreport.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.FilterLogic;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;
import ca.cmpt276.restaurantreport.applogic.SearchState;

/*
* Allows the user to enter search/filter settings
* to find restaurants with specific criteria more easily
* */
public class SearchActivity extends AppCompatActivity {

    SearchState searchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchState = SearchState.getInstance();

        setupSearchButton();
        setupClearButton();
        setupHazardCheckBox();
        setupRadioButtons();
        setupViolationCheckBox();
        setupToggleSwitch();
        setupFavouriteCheckBox();
        setupViolationInputBox();
        setupSearchBar();
    }

    private void setupSearchBar() {
        EditText restaurantName = findViewById(R.id.edtxtResName);
        String resName = searchState.getRestaurantName();
        restaurantName.setText(resName);

        restaurantName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String resName = restaurantName.getText().toString();
                searchState.setRestaurantName(resName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setupViolationInputBox() {
        EditText violationNumberInput = findViewById(R.id.edtxtNumViolations);
        String numViolations = Integer.toString(searchState.getNumOfCriticalViolations());

        violationNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!violationNumberInput.getText().toString().equals("")){
                    int numViolations = Integer.parseInt(violationNumberInput.getText().toString());
                    searchState.setNumOfCriticalViolations(numViolations);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        violationNumberInput.setText(numViolations);
    }

    //set a flag for search settings to find favourited restaurants
    private void setupFavouriteCheckBox() {
        CheckBox chkFav = findViewById(R.id.chkBoxFav);

        boolean onlyFavouritesOn = searchState.onlyFavourites();

        if(onlyFavouritesOn){
            chkFav.setChecked(true);
            searchState.setOnlyFavourites(true);
        }
        else{
            chkFav.setChecked(false);
            searchState.setOnlyFavourites(false);
        }

        chkFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(chkFav.isChecked()){
                searchState.setOnlyFavourites(true);
            }
            else{
                searchState.setOnlyFavourites(false);
            }
        });
    }

    // Enables/disables the violation search setting
    // perhaps use a boolean in the search state class for this
    @SuppressLint("ResourceType")
    private void setupViolationCheckBox() {
        CheckBox chkViolation = findViewById(R.id.chkBoxViolation);
        Switch lessOrGreaterThanSwitch = findViewById(R.id.valueSwitch);
        TextView txtGreaterThan = findViewById(R.id.txtGreaterThan);
        EditText violationNum = findViewById(R.id.edtxtNumViolations);

        boolean violationSearchOn = searchState.isViolationSearchOn();

        //on activity launch, check the state of the checkbox
        //and disable/enable settings accordingly
        if(violationSearchOn){
            if(lessOrGreaterThanSwitch.isChecked()){
                txtGreaterThan.setTextColor(Color.BLACK);
            }
            else{
                txtGreaterThan.setTextColor(Color.LTGRAY);
            }

            lessOrGreaterThanSwitch.setEnabled(true);
            violationNum.setEnabled(true);
            chkViolation.setChecked(true);
        }
        else{
            txtGreaterThan.setTextColor(Color.LTGRAY);
            lessOrGreaterThanSwitch.setEnabled(false);
            violationNum.setEnabled(false);
            chkViolation.setChecked(false);
        }

        //updates the flag and enables/disables the options
        chkViolation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                lessOrGreaterThanSwitch.setEnabled(true);
                violationNum.setEnabled(true);
                if(lessOrGreaterThanSwitch.isChecked()){
                    txtGreaterThan.setTextColor(Color.BLACK);
                    searchState.setLessOrGreaterThanFlag(true);
                }else{
                    lessOrGreaterThanSwitch.setTextColor(Color.BLACK);
                    searchState.setLessOrGreaterThanFlag(false);
                }
                searchState.setViolationSearchOn(true);
            }
            else{
                txtGreaterThan.setTextColor(Color.LTGRAY);
                lessOrGreaterThanSwitch.setTextColor(Color.LTGRAY);
                lessOrGreaterThanSwitch.setEnabled(false);
                violationNum.setEnabled(false);

                searchState.setViolationSearchOn(false);
            }
        });
    }

    // Enables/disables the hazard search setting
    // perhaps use a boolean in the search state class for this
    private void setupHazardCheckBox() {
        RadioGroup hazardRadioGroup = findViewById(R.id.radioHazSelection);
        CheckBox chkHaz = findViewById(R.id.chkBoxHazard);

        boolean hazardSearchOn = searchState.isHazardSearchOn();

        if(hazardSearchOn){
            for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                hazardRadioGroup.getChildAt(i).setEnabled(true);
                chkHaz.setChecked(true);
            }
        }
        else{
            for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                hazardRadioGroup.getChildAt(i).setEnabled(false);
                chkHaz.setChecked(false);
            }
            searchState.setHazardLevel("none");
        }

        chkHaz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                    hazardRadioGroup.getChildAt(i).setEnabled(true);

                }
                searchState.setHazardSearchOn(true);
            }
            else {
                for (int i = 0; i < hazardRadioGroup.getChildCount(); i++) {
                    hazardRadioGroup.getChildAt(i).setEnabled(false);

                }
                searchState.setHazardSearchOn(false);
                searchState.setHazardLevel("none");
            }
        });

    }

    //lets user input number of critical violations they want to search for
    private void getViolationInput() {
        EditText violationNum = findViewById(R.id.edtxtNumViolations);
        String numCritViolations = violationNum.getText().toString();

        if(numCritViolations.isEmpty()){
            searchState.setNumOfCriticalViolations(0);
            violationNum.setText("0");
        }else {
            int numCriticalViolations = Integer.parseInt(numCritViolations);
            searchState.setNumOfCriticalViolations(numCriticalViolations);
        }
    }

    //toggle between <= and >= for the above method
    private void setupToggleSwitch() {
        Switch lessOrGreaterThanSwitch = findViewById(R.id.valueSwitch);
        TextView txtGreaterThan = findViewById(R.id.txtGreaterThan);

        boolean switchState = searchState.getLessOrGreaterThanFlag();

        if (switchState) {
            lessOrGreaterThanSwitch.setChecked(true);
            lessOrGreaterThanSwitch.setTextColor(Color.LTGRAY);
            txtGreaterThan.setTextColor(Color.BLACK);
        }
        else {
            lessOrGreaterThanSwitch.setChecked(false);
            lessOrGreaterThanSwitch.setTextColor(Color.BLACK);
            txtGreaterThan.setTextColor(Color.LTGRAY);
        }
        lessOrGreaterThanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                lessOrGreaterThanSwitch.setTextColor(Color.LTGRAY);
                txtGreaterThan.setTextColor(Color.BLACK);

                searchState.setLessOrGreaterThanFlag(true);
            }
            else{
                lessOrGreaterThanSwitch.setTextColor(Color.BLACK);
                txtGreaterThan.setTextColor(Color.LTGRAY);

                searchState.setLessOrGreaterThanFlag(false);
            }
        });
    }

    //Search for restaurant name
    private void getSearchBarInput() {
        EditText restaurantSearch = findViewById(R.id.edtxtResName);
        String restaurantName = restaurantSearch.getText().toString();

        searchState.setRestaurantName(restaurantName);
    }

    //allow user to select a hazard level to search for
    private void setupRadioButtons() {
        RadioGroup hazardRadioGroup = findViewById(R.id.radioHazSelection);
        String hazLvl = searchState.getHazardLevel();

        switch(hazLvl){
            case("Low"):
                hazardRadioGroup.check(R.id.radioBtnLow);
                break;
            case("Moderate"):
                hazardRadioGroup.check(R.id.radioBtnMid);

                break;
            case("High"):
                hazardRadioGroup.check(R.id.radioBtnHigh);
                break;
        }

        hazardRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedButton = group.findViewById(checkedId);
            boolean isChecked = checkedButton.isChecked();

            if(isChecked){
                searchState.setHazardLevel(checkedButton.getTag().toString());
            }
        });

    }

    private void setupSearchButton() {
        Button searchBtn = findViewById(R.id.btnSearch);

        searchBtn.setOnClickListener(v -> {
            RestaurantManager manager = RestaurantManager.getInstance(SearchActivity.this);

            manager.clearFilteredList();

            getSearchBarInput();
            getViolationInput();

            //when flag is false, manager returns original data set
            searchState.setActiveSearchStateFlag(false);
            List<Restaurant> allRestaurants = manager.getRestaurants();

            //filter from the entire data set
            FilterLogic filterLogic = new FilterLogic(this, allRestaurants);
            filterLogic.populateFilteredRestaurantList();

            //set flag to true to indicate that the search filter is now active
            // i.e. manager returns the filtered list when the flag is true
            searchState.setActiveSearchStateFlag(true);

            if(manager.getRestaurants().isEmpty()){
                searchState.setActiveSearchStateFlag(false);
                Toast.makeText(this, R.string.srch_no_result, Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });
    }

    private void setupClearButton() {
        Button clearBtn = findViewById(R.id.btnClear);

        clearBtn.setOnClickListener(v -> {
            RestaurantManager manager = RestaurantManager.getInstance(SearchActivity.this);
            manager.clearFilteredList();
            searchState.clearSearchState();
            finish();
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, SearchActivity.class);
    }

}
