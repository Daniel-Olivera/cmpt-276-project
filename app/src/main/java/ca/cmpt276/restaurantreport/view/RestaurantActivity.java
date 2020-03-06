package ca.cmpt276.restaurantreport.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.restaurantreport.R;
import ca.cmpt276.restaurantreport.model.RestaurantManager;

public class RestaurantActivity extends AppCompatActivity {
    RestaurantManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        manager = RestaurantManager.getInstance(this);

        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText("restaurant name");

        TextView address = findViewById(R.id.address);
        address.setText("restaurant address");

        TextView latitude = findViewById(R.id.latitude);
        latitude.setText("latitude");

        TextView longitude = findViewById(R.id.longtitude);
        longitude.setText("longitude");

        ListView listView;
        String mTitle[]= {"12" ,"22222" , "3333333 ", "444444" ," 5555555"};
        String detail1[]= {"hahahaha", "hihihi" , "hohoho" , "hhehehehehe", "huhuhu"};
        String detail2[]= {"hahahaha", "hihihi" , "hohoho" , "hhehehehehe", "huhuhu"};
        String detail3[]= {"hahahaha", "hihihi" , "hohoho" , "hhehehehehe", "huhuhu"};


        listView = findViewById(R.id.listView);

        MyAdapter adapter = new MyAdapter(this,mTitle,detail1,detail3,detail3);
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
        String detail1[];
        String detail2[];
        String detail3[];


        MyAdapter(Context c, String title[], String detail1[],String detail2[],String detail3[])
        {
            super(c,R.layout.inspection_row,R.id.inspectorDetail1,title);
            this.context= c;
            this.inspection = title;
            this.detail1 = detail1;
            this.detail2 = detail2;
            this.detail3 = detail3;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.inspection_row,parent,false);
            TextView title = row.findViewById(R.id.inspectorDetail1);
            TextView details1 = row.findViewById(R.id.inspectorDetail1);
            TextView details2 = row.findViewById(R.id.inspectorDetail2);
            TextView details3 = row.findViewById(R.id.inspectorDetail3);


            title.setText(inspection[position]);
            details1.setText(detail1[position]);
            details2.setText(detail2[position]);
            details3.setText(detail3[position]);






            return row;
        }
    }


}
