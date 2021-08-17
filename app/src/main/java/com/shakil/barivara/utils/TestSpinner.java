package com.shakil.barivara.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shakil.barivara.R;
import com.shakil.barivara.model.TestSpinnerData;

import java.util.HashMap;

public class TestSpinner {
    Spinner spinner;
    Context context;
    HashMap<String, TestSpinnerData[]> spinnerValueMap = new HashMap<>();

    public TestSpinner(Context context, HashMap<String, TestSpinnerData[]> spinnerValueMap) {
        this.context = context;
        this.spinnerValueMap = spinnerValueMap;
    }

    private void setSpinnerOnLoad(String sFieldName, String sFieldValue) {
        if (!TextUtils.isEmpty(sFieldValue))
            try {
                spinner.setSelection(getSpinnerValueIndex(sFieldValue, spinnerValueMap.get(sFieldName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private int getSpinnerValueIndex(String val, TestSpinnerData[] spinnerValue) {
        for (int i = 0; i < spinnerValue.length; i++) {
            if (spinnerValue[i].getValue().equals(val)) {
                return i;
            }
        }
        return -1;
    }

    public Spinner bind(Spinner spinner, String fieldName, TestSpinnerData[] spinnerValue) {
        this.spinner = spinner;

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context
                , R.layout.spinner_drop, spinnerValue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinnerValueMap.put(fieldName, spinnerValue);
        return spinner;
    }
}
