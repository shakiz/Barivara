package com.shakil.barivara.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shakil.homeapp.R;

public class SpinnerAdapter {
    private ArrayAdapter<String> spinnerAdapter;

    public void setSpinnerAdapter(Spinner spinner, String[] spinnerValue, Context context){
        spinnerAdapter = new ArrayAdapter<>(context, R.layout.spinner_drop,spinnerValue);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }
}
