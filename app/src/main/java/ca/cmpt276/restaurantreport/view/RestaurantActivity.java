package ca.cmpt276.restaurantreport.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.Inspection;
import ca.cmpt276.restaurantreport.model.Restaurant;
import ca.cmpt276.restaurantreport.model.RestaurantListAdapter;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;
    String resName;
    int totalIssues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        manager = RestaurantManager.getInstance(this);

        // get Restaurant name and total issues
        Intent intent = getIntent();
        resName = intent.getStringExtra("resName");
        totalIssues = Integer.parseInt(intent.getStringExtra("totalIssues"));

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
                else
                    continue;
            }
        }
        Restaurant restaurant = manager.get(index);
        // parse out the double quote
        String addr = manager.get(index).getPhysicalAddr().replace("\"", "");
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(resName);
        TextView address = findViewById(R.id.address);
        address.setText("Address: " + addr);
        TextView latitude = findViewById(R.id.latitude);
        latitude.setText("Latitude: " + manager.get(index).getLatitude());
        TextView longitude = findViewById(R.id.longtitude);
        longitude.setText("Longitude: " + manager.get(index).getLongitude());

        List<Inspection> inspectionList = manager.get(index).getInspections();

        String[] title = new String[inspectionList.size()];
        int []critIssue  = new int [inspectionList.size()];
        int []nonCritIssue = new int [inspectionList.size()];
        String[]lastInspec =  new String[inspectionList.size()];



        for(int i = 0 ; i < inspectionList.size(); i++)
        {
            critIssue[i] = inspectionList.get(i).getNumCritIssues();
            nonCritIssue[i] = inspectionList.get(i).getNumNonCritIssues();
            title[i] = "Inspection " + (i+1);
            lastInspec[i]=inspectionList.get(i).dayFromLastInspection();


        }


        ListView listView;


        listView = findViewById(R.id.listView);

        MyAdapter adapter = new MyAdapter(this,title,critIssue,nonCritIssue,lastInspec);
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
        String inspection[];
        int critNum[];
        int nonCritNum[];
        String lastInspec[];


        MyAdapter(Context c, String title[], int critNum[],int nonCritNum[],String lastInspec[])
        {
            super(c,R.layout.inspection_row,R.id.inspectorDetail1,title);
            this.context= c;
            this.inspection = title;
            this.critNum = critNum;
            this.nonCritNum = nonCritNum;
            this.lastInspec = lastInspec;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.inspection_row,parent,false);
            TextView title = row.findViewById(R.id.inspectorReport);
            TextView details1 = row.findViewById(R.id.inspectorDetail1);
            TextView details2 = row.findViewById(R.id.inspectorDetail2);
            TextView details3 = row.findViewById(R.id.inspectorDetail3);


            title.setText(inspection[position]);
            details1.setText("Number of critical issue is: "+ critNum[position]);
            details2.setText("Number of non-critical issue is: " + nonCritNum[position]);
            details3.setText("Date: " + lastInspec[position]);


            return row;
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
