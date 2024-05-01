package com.shakil.barivara.utils;

import android.content.Context;
import com.shakil.barivara.R;
import java.util.ArrayList;
import java.util.Collections;

public class SpinnerData {

    private final Context context;

    public SpinnerData(Context context) {
        this.context = context;
    }

    public ArrayList<String> setYearData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] yearArray = {context.getString(R.string.select_data_1), context.getString(R.string.twenty_fiften),
                context.getString(R.string.twenty_sixteen),
                context.getString(R.string.twenty_seventeen),
                context.getString(R.string.twenty_eightteen),
                context.getString(R.string.twenty_nineteen),
                context.getString(R.string.twenty_twenty_one),
                context.getString(R.string.twenty_twenty_two),
                context.getString(R.string.twenty_twenty_three),
                context.getString(R.string.twenty_twenty_four),
                context.getString(R.string.twenty_twenty_five),
                context.getString(R.string.twenty_twenty_six),
                context.getString(R.string.twenty_twenty_seven),
        };
        Collections.addAll(spinnerValues, yearArray);
        return spinnerValues;
    }


    public ArrayList<String> setMonthData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] monthArray = {context.getString(R.string.select_data_1),context.getString(R.string.january),
                context.getString(R.string.february), context.getString(R.string.march), context.getString(R.string.april),
                context.getString(R.string.may), context.getString(R.string.june), context.getString(R.string.july),
                context.getString(R.string.august), context.getString(R.string.september), context.getString(R.string.october),
                context.getString(R.string.november), context.getString(R.string.december)};
        Collections.addAll(spinnerValues, monthArray);
        return spinnerValues;
    }

    public ArrayList<String> setTenantTypeData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] tenantTypeArray = {context.getString(R.string.select_data_1),context.getString(R.string.bachelor),
                context.getString(R.string.family), context.getString(R.string.small_family), context.getString(R.string.female_only),
                context.getString(R.string.male_only)};
        Collections.addAll(spinnerValues, tenantTypeArray);
        return spinnerValues;
    }

    public ArrayList<String> setMeterTypeData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] meterTypeArray = {"Select Data","Main Meter","Sub Meter"};
        Collections.addAll(spinnerValues, meterTypeArray);
        return spinnerValues;
    }

    public ArrayList<String> setSpinnerNoData(){
        ArrayList<String> spinnerValues = new ArrayList<>();
        String[] meterTypeArray = {"No Data"};
        Collections.addAll(spinnerValues, meterTypeArray);
        return spinnerValues;
    }
}
