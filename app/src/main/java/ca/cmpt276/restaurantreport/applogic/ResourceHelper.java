package ca.cmpt276.restaurantreport.applogic;

import android.content.Context;
import android.content.res.TypedArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restaurantreport.R;

/*
this class retrieve data from multi-dimensional array and store it
in to a class
 */
class ResourceHelper {
    static List<TypedArray> getMultiTypedArray(Context context){

        List<TypedArray> array = new ArrayList<>();

        try {
            Class<R.array> res = R.array.class;
            Field field;
            int counter = 1;

            do {
                field = res.getField("violation" + "_" + counter);
                array.add(context.getResources().obtainTypedArray(field.getInt(null)));
                counter++;
            } while (field != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return array;
        }
    }
}
