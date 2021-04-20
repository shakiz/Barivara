package com.shakil.barivara.utils;

import android.content.Context;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;

import java.util.ArrayList;


public class SpinnerData {

    private Context context;
    private FirebaseCrudHelper firebaseCrudHelper;

    public SpinnerData(Context context) {
        this.context = context;
        firebaseCrudHelper = new FirebaseCrudHelper(context);
    }

    public ArrayList<String>  setMonthData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] monthArray = {"Select Data","January","February","March","April","May",
                "June","July","August","September","October","November","December"};
        for (int start = 0; start < monthArray.length; start++) {
            spinnerValues.add(monthArray[start]);
        }
        return spinnerValues;
    }

    public ArrayList<String> setMeterTypeData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] meterTypeArray = {"Select Data","Main Meter","Sub Meter"};
        for (int start = 0; start < meterTypeArray.length; start++) {
            spinnerValues.add(meterTypeArray[start]);
        }
        return spinnerValues;
    }
}
