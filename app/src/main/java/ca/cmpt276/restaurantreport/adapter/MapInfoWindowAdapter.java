package ca.cmpt276.restaurantreport.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.applogic.Restaurant;
import ca.cmpt276.restaurantreport.applogic.RestaurantManager;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private RestaurantManager manager;

    public MapInfoWindowAdapter(Context context){
        this.context = context;
        this.manager = RestaurantManager.getInstance(context);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        List<Restaurant> restaurants = manager.getRestaurants();


        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.map_info_window, null);

        TextView txtName = view.findViewById(R.id.txtInfoViewName);
        TextView txtAddress = view.findViewById(R.id.txtInfoViewAddr);
        TextView txtHaz = view.findViewById(R.id.txtInfoHaz);
        ImageView imgHaz = view.findViewById(R.id.imgInfoHazIcon);

        String trackingNum = marker.getSnippet();

        for(int i = 0; i < restaurants.size(); i++){
            if(trackingNum.equals(restaurants.get(i).getTrackingNum())){
                String restaurantName = restaurants.get(i).getName();
                String address = restaurants.get(i).getPhysicalAddr();
                String hazardLvl = restaurants.get(i).getLatestInspectionHazard(context);

                txtName.setText(restaurantName);
                txtAddress.setText(address);
                //txtHaz.setText(hazardLvl);
                manager.setHazardLevelText(txtHaz,hazardLvl);
                manager.getHazardIcon(hazardLvl, imgHaz);
                break;
            }
        }


        return view;
    }

}
