package com.shakil.barivara.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shakil.barivara.R;

import java.util.ArrayList;

public class SpinnerAdapter {

    public void setSpinnerAdapter(Spinner spinner, Context context, ArrayList<String> spinnerValue){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, R.layout.spinner_drop, spinnerValue);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }
}
