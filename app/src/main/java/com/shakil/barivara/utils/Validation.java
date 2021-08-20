package com.shakil.barivara.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shakil.barivara.R;

import java.util.HashMap;
import java.util.Map;

public class Validation {
    Map<String, String[]> validationMap = new HashMap<String, String[]>();
    String[] _msg;
    private final Context context;

    public Validation(Context context, Map<String, String[]> validationMap) {
        this.context = context;
        this.validationMap = validationMap;
    }

    public void setEditTextIsNotEmpty(String[] controls, String[] msgs) {
        if (msgs == null) {
            validationMap.put("EditText", controls);
            return;
        }
        if (controls.length == msgs.length) {
            validationMap.put("EditText", controls);
            _msg = msgs;
        } else
            Toast.makeText(context,"Validation messages and mapping controls are not equal", Toast.LENGTH_SHORT).show();
    }

    public void setSpinnerIsNotEmpty(String[] controls) {
        validationMap.put("Spinner", controls);
    }

    public void setRadioButtonIsNotEmpty(String prefix, int count, String fieldName) {
        String[] val = new String[]{prefix, Integer.toString(count), fieldName};
        validationMap.put("RadioButton", val);
    }

    public boolean isValid() {
        boolean isValid = true;
        for (Map.Entry<String, String[]> entry : validationMap.entrySet()) {

            //region edit text empty validation
            if (entry.getKey() == "EditText") {
                int iMsg = 0;
                for (String controlName : entry.getValue()) {
                    int resID = context.getResources().getIdentifier(controlName, "id", context.getPackageName());
                    EditText et = ((android.app.Activity) context).findViewById(resID);
                    if(et!=null){
                        if (et.getText().toString().isEmpty()) {
                            et.requestFocus();
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                            if (_msg != null)
                                et.setError(_msg[iMsg]);
                            else et.setError("This value is important.");
                            if(et.getTag() != null){
                                Toast.makeText(context,
                                        context.getString(R.string.missing_data) + "  " + et.getTag().toString(),
                                        Toast.LENGTH_SHORT)
                                .show();
                            }
                            isValid = false;
                            break;
                        }
                        iMsg++;
                    }
                }
            }
            //endregion

            //region single checkbox validation
            if (entry.getKey() == "CheckBox") {
                String[] val = entry.getValue();
                boolean isChecked = false;
                for (int i = 1; i <= Integer.parseInt(val[1]); i++) {
                    String ctlName = val[0] + i;
                    int resID = context.getResources().getIdentifier(ctlName, "id", context.getPackageName());
                    CheckBox chkCtl = ((android.app.Activity) context).findViewById(resID);
                    isChecked = chkCtl.isChecked();
                    chkCtl.requestFocus();

                    if (!isChecked) {
                        chkCtl.setError(val[2] + " is important.");
                        break;
                    }
                }
                isValid = isChecked;
            }
            //endregion

            //region spinner validation
            if (entry.getKey() == "Spinner") {
                for (String controlName : entry.getValue()) {
                    String viewName = controlName;
                    if (controlName.contains("-")) {
                        String[] splitString = controlName.split("-");
                        viewName = splitString[0];
                    }
                    int resID = context.getResources().getIdentifier(viewName, "id", context.getPackageName());

                    Spinner sp = ((android.app.Activity) context).findViewById(resID);
                    if(sp!=null){
                        String spinnerValue = (String) sp.getSelectedItem();
                        if (spinnerValue.equals(context.getString(R.string.select_data))) {
                            sp.requestFocus();
                            TextView errorText = (TextView) sp.getSelectedView();
                            errorText.setError("");
                            if(sp.getTag() != null){
                                Toast.makeText(context, context.getString(R.string.missing_data) + "  " + sp.getTag().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            errorText.setTextColor(Color.RED);
                            errorText.setText(context.getString(R.string.select_correct_data));
                            isValid = false;
                            break;
                        }
                    }
                }
            }
            //endregion

            //region radio button validation
            if (entry.getKey() == "RadioButton") {
                String[] val = entry.getValue();
                boolean isChecked = false;
                for (int i = 1; i <= Integer.parseInt(val[1]); i++) {
                    String ctlName = val[0] + i;
                    int resID = context.getResources().getIdentifier(ctlName, "id", context.getPackageName());
                    RadioButton chkCtl = ((android.app.Activity) context).findViewById(resID);
                    if (chkCtl.isChecked() == true) {
                        isChecked = true;
                    }
                }
                if (!isChecked) {
                    isValid = isChecked;
                    Toast.makeText(context, val[2] + " is important.", Toast.LENGTH_SHORT).show();
                    break;
                }
                isValid = isChecked;
            }
            //endregion
        }
        return isValid;
    }
}
