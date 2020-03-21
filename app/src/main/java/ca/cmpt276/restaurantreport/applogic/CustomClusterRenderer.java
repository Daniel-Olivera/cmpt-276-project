package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import ca.cmpt276.restaurantreport.R;

/*
Changes how the clusters behave
i.e. change the icons of each marker in the cluster
     or how many items are allowed in a cluster
 */
public class CustomClusterRenderer extends DefaultClusterRenderer<Restaurant> {

    private static final int MARKER_DIMENSION = 50;
    private Context context;
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<Restaurant> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;

        iconGenerator = new IconGenerator(context);
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));

        iconGenerator.setContentView(markerImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(Restaurant item, MarkerOptions markerOptions) {
       setMarkerHazardIcon(item, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Restaurant> cluster) {
        //start clustering if at least 3 items overlap
        return cluster.getSize() > 2;
    }

    private void setMarkerHazardIcon(Restaurant currentRestaurant, MarkerOptions markerOptions) {

        String hazardString = currentRestaurant.getLatestInspectionHazard();
        BitmapDescriptor markerBitmapDescriptor;
        Bitmap icon;

        //Assign's the appropriate icon and scales it
        switch (hazardString) {
            case ("Mid"):
            case ("Moderate"): {
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_med);
                Bitmap smallMarker = Bitmap.createScaledBitmap(icon,75,75,false);
                markerBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
                break;
            }
            case ("High"): {
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_high);
                Bitmap smallMarker = Bitmap.createScaledBitmap(icon,75,75,false);
                markerBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
                break;
            }
            case ("Low"):
            default: {
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_low);
                Bitmap smallMarker = Bitmap.createScaledBitmap(icon,75,75,false);
                markerBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
                break;
            }
        }
        //sets the icon
        markerOptions.icon(markerBitmapDescriptor);
    }
}
