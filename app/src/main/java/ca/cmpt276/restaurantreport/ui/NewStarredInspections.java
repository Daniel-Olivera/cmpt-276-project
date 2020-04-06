package ca.cmpt276.restaurantreport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.adapter.RestaurantListAdapter;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

public class NewStarredInspections extends DialogFragment {

    private Context context;
    private RestaurantManager manager;

    NewStarredInspections(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_new_starred_inspections, container, false);

        manager = RestaurantManager.getInstance(context);
        List<Restaurant> allRestaurantsList = manager.getUpdatedFavouriteRestaurantList();

        Collections.sort(allRestaurantsList, (firstRes, nextRes) -> firstRes.getName().compareTo(nextRes.getName()));

        String[] restTitles = new String[allRestaurantsList.size()];

        //the adapter constructor requires a string array to work
        for(int i = 0; i < restTitles.length; i++){
            restTitles[i] = allRestaurantsList.get(i).getName();
        }

        RestaurantListAdapter adapter = new RestaurantListAdapter(context, allRestaurantsList, restTitles);
        ListView listView = dialog.findViewById(R.id.listStarred);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String trackingNum = allRestaurantsList.get(position).getTrackingNum();

            Intent intent = RestaurantActivity.makeIntent(context, trackingNum);
            startActivity(intent);

        });

        return dialog;
    }
}
