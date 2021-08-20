package com.shakil.barivara.utils;

import android.content.Context;
import com.shakil.barivara.R;
import java.util.ArrayList;

public class SpinnerData {

    private final Context context;

    public SpinnerData(Context context) {
        this.context = context;
    }

    public ArrayList<String> setYearData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] yearArray = {context.getString(R.string.select_data_1),"2019","2020","2021","2022","2023","2024","2025"};
        for (int start = 0; start < yearArray.length; start++) {
            spinnerValues.add(yearArray[start]);
        }
        return spinnerValues;
    }


    public ArrayList<String> setMonthData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] monthArray = {context.getString(R.string.select_data_1),context.getString(R.string.january),
                context.getString(R.string.february), context.getString(R.string.march), context.getString(R.string.april),
                context.getString(R.string.may), context.getString(R.string.june), context.getString(R.string.july),
                context.getString(R.string.august), context.getString(R.string.september), context.getString(R.string.october),
                context.getString(R.string.november), context.getString(R.string.december)};
        for (int start = 0; start < monthArray.length; start++) {
            spinnerValues.add(monthArray[start]);
        }
        return spinnerValues;
    }

    public ArrayList<String> setTenantTypeData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] tenantTypeArray = {context.getString(R.string.select_data_1),context.getString(R.string.bachelor),
                context.getString(R.string.family), context.getString(R.string.small_family), context.getString(R.string.female_only),
                context.getString(R.string.male_only)};
        for (int start = 0; start < tenantTypeArray.length; start++) {
            spinnerValues.add(tenantTypeArray[start]);
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

    public ArrayList<String> setSpinnerNoData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] meterTypeArray = {"No Data"};
        for (int start = 0; start < meterTypeArray.length; start++) {
            spinnerValues.add(meterTypeArray[start]);
        }
        return spinnerValues;
    }
}
