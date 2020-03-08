package ca.cmpt276.restaurantreport.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Inspection;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;
    String resName;
    int totalIssues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        manager = RestaurantManager.getInstance(this);

        // get Restaurant name and total issues
        Intent intent = getIntent();
        resName = intent.getStringExtra("resName");
        totalIssues = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("totalIssues")));

        //get restaurant details
        List<Restaurant> listRes = manager.getRestaurants();
        int index = 0;
        for (int i = 0 ; i < listRes.size();i++)
        {
            if (listRes.get(i).getName().replace("\"", "").equals(resName))
            {
                int issueCount = 0;
                List<Inspection> insp = listRes.get(i).getInspections();
                for (int y = 0; y < insp.size(); y++) {
                    issueCount += insp.get(y).getTotalIssues();
                }
                if (issueCount == totalIssues) {
                    index = i;
                    break;
                }
            }
        }
        Restaurant restaurant = manager.get(index);
        DecimalFormat decimalFormat = new DecimalFormat("0",DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        decimalFormat.setMaximumFractionDigits(340);
        // parse out the double quote
        String addr = manager.get(index).getPhysicalAddr().replace("\"", "");
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(resName);
        TextView address = findViewById(R.id.txtAddress);
        address.setText(getString(R.string.rest_addr_prefix, addr));
        TextView latitude = findViewById(R.id.txtLattitude);
        latitude.setText(getString(R.string.rest_lat_prefix, Double.toString(manager.get(index).getLatitude())));
        TextView longitude = findViewById(R.id.txtLongtitude);
        longitude.setText(getString(R.string.rest_long_prefix, Double.toString(manager.get(index).getLongitude())));
        TextView inspection = findViewById(R.id.txtInspHeader);
        inspection.setText(R.string.rest_insp_prefix);

        List<Inspection> inspectionList = manager.get(index).getInspections();

        //Sort the inspection list according to date
        Collections.sort(inspectionList,Collections.reverseOrder());

        // Adding values to appropriate
        String[] title = new String[inspectionList.size()];
        int []critIssue  = new int [inspectionList.size()];
        int []nonCritIssue = new int [inspectionList.size()];
        String[]lastInspec =  new String[inspectionList.size()];
        String[]hazardLevel = new String[inspectionList.size()];





        for(int i = 0 ; i < inspectionList.size(); i++)
        {
            critIssue[i] = inspectionList.get(i).getNumCritIssues();
            nonCritIssue[i] = inspectionList.get(i).getNumNonCritIssues();
            title[i] = "Inspection " + (i+1);
            lastInspec[i]=inspectionList.get(i).dayFromLastInspection();
            hazardLevel[i]=inspectionList.get(i).getHazardRating();


        }
        // Setting hazard level




        ListView listView;


        listView = findViewById(R.id.listView);

        MyAdapter adapter = new MyAdapter(this,title,critIssue,nonCritIssue,lastInspec,hazardLevel);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"clickedd hahahaha",Toast.LENGTH_SHORT).show();
                }
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"123 hahahaha",Toast.LENGTH_SHORT).show();
                }
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"456  hahahaha",Toast.LENGTH_SHORT).show();
                }
                if(position ==0)
                {
                    Toast.makeText(RestaurantActivity.this,"789 hahahaha",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        String[] inspection;
        int[] critNum;
        int[] nonCritNum;
        String[] lastInspec;
        String[] hazardLevels;


        MyAdapter(Context c, String[] title, int[] critNum, int[] nonCritNum, String[] lastInspec, String[] hazardLevel)
        {
            super(c,R.layout.inspection_row,R.id.txtInspCritNum,title);
            this.context= c;
            this.inspection = title;
            this.critNum = critNum;
            this.nonCritNum = nonCritNum;
            this.lastInspec = lastInspec;
            this.hazardLevels = hazardLevel;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.inspection_row,parent,false);
            TextView title = row.findViewById(R.id.txtInspName);
            TextView details1 = row.findViewById(R.id.txtInspCritNum);
            TextView details2 = row.findViewById(R.id.txtInspNCrtiNum);
            TextView hazard = row.findViewById(R.id.txtInspHazLvl);

            ImageView hazardLevel = row.findViewById(R.id.imgInspHazIcon);


            title.setText(getString(R.string.insp_date_prefix, lastInspec[position]));
            if(critNum[position] == 1){
                details1.setText(getString(R.string.insp_crit_postfix,Integer.toString(critNum[position])));
            } else {
                details1.setText(getString(R.string.insp_crit_postfix_s,Integer.toString(critNum[position])));
            }
            if(nonCritNum[position] == 1){
                details2.setText(getString(R.string.insp_non_crit_postfix,Integer.toString(nonCritNum[position])));
            } else {
                details2.setText(getString(R.string.insp_non_crit_postfix_s,Integer.toString(nonCritNum[position])));
            }
            hazard.setText(hazardLevels[position].replace("\"", ""));

            getHazardIcon(hazardLevels[position],hazardLevel);

            return row;
        }
    }

    private void getHazardIcon(String hazardLevel, ImageView icon){
        switch(hazardLevel){
            case("\"Low\""):
            default:{
                icon.setImageResource(R.drawable.low);
                break;
            }
            case("\"Moderate\""):{
                icon.setImageResource(R.drawable.medium);
                break;
            }
            case("\"High\""):{
                icon.setImageResource(R.drawable.high);
                break;
            }
        }
    }
    public static Intent makeIntent(Context context, String resName, String totalIssues)
    {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("resName", resName);
        intent.putExtra("totalIssues", totalIssues);
        return intent;
    }



}
