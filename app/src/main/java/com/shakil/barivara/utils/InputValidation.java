package com.shakil.barivara.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.shakil.homeapp.R;

public class InputValidation {
    private Context context;
    private View view;

    public InputValidation(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public boolean checkEditTextInput(int resIdArray, String message){
        EditText editText = view.findViewById(resIdArray);
        if (editText.getText().toString().isEmpty()){
            editText.setError(message);
            return false;
        }
        else{
            return true;
        }
    }

    public boolean checkEditTextInput(int[] resIdArray, String message){
        boolean validation = false;
        for(int start = 0;start < resIdArray.length;start++){
            EditText editText = view.findViewById(resIdArray[start]);
            if (editText.getText().toString().isEmpty()){
                editText.setError(message);
                validation = false;
            }
            else{
                validation = true;
            }
        }
        return validation;
    }

    public boolean checkTextInputEditTextInput(int resIdArray, String message){
        TextInputEditText editText = view.findViewById(resIdArray);
        if (editText.getText().toString().isEmpty()){
            editText.setError(message);
            return false;
        }
        else{
            return true;
        }
    }

    public boolean checkTextInputEditTextInput(int[] resIdArray, String message){
        boolean validation = false;
        for(int start = 0;start < resIdArray.length;start++){
            TextInputEditText editText = view.findViewById(resIdArray[start]);
            if (editText.getText().toString().isEmpty()){
                editText.setError(message);
                return false;
            }
            else{
                return true;
            }
        }
        return validation;
    }

    public String checkSpinner(int resId){
        final String[] value = {""};
        Spinner spinner = view.findViewById(resId);
        if (!spinner.getSelectedItem().toString().equals(R.string.select_data)){
            value[0] = spinner.getSelectedItem().toString();
        }
        else{
            value[0] = "Select Data";
            Toast.makeText(context,R.string.warning_message, Toast.LENGTH_SHORT).show();
        }
        Log.v("shakil",""+value[0]);
        return value[0];
    }

    public String checkSpinner(int[] resId){
        final String[] value = {""};
        for (int start=0;start<resId.length;start++){
            Spinner spinner = view.findViewById(resId[start]);
            if (!spinner.getSelectedItem().toString().equals(R.string.select_data)){
                value[0] = spinner.getSelectedItem().toString();
            }
            else{
                value[0] = "Select Data";
            }
        }
        Log.v("shakil",""+value[0]);
        return value[0];
    }
}
