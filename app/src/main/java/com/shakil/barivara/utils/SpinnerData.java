package com.shakil.barivara.utils;

import android.content.Context;

import com.shakil.barivara.dbhelper.DbHelperParent;


public class SpinnerData {

    private Context context;
    private DbHelperParent dbHelperParent;

    public SpinnerData(Context context) {
        this.context = context;
        dbHelperParent = new DbHelperParent(context);
    }

    public String[] setMonthData(){
        String[] spinnerValues = {"Select Data","January","February","March","April","May","June","July","August","September","October","November","December"};
        return spinnerValues;
    }

    public String[] setMeterData(){
        String[] spinnerValues = dbHelperParent.getMeterNames().toArray(new String[dbHelperParent.getMeterNames().size()]);
        return spinnerValues;
    }

    public String[] setMeterTypeData(){
        String[] spinnerValues = {"Select Data","Main Meter","Sub Meter"};
        return spinnerValues;
    }

    public String[] setRoomData(){
        String[] spinnerValues = dbHelperParent.getRoomNames().toArray(new String[dbHelperParent.getMeterNames().size()]);
        return spinnerValues;
    }
}
