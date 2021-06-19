package com.shakil.barivara.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    Map<String, String[]> validationMap = new HashMap<String, String[]>();
    String[] _msg;
    int[] _length;
    private Context context;

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

    public void setEditTextLength(String[] controls, int[] length) {
        if (controls.length == length.length) {
            validationMap.put("EditTextLength", controls);
            _length = length;
        } else
            Toast.makeText(context,"Missing controls length?", Toast.LENGTH_SHORT).show();
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
                    EditText et = (EditText) ((android.app.Activity) context).findViewById(resID);
                    if(et!=null){
                        if (et.getText().toString().isEmpty()) {
                            et.requestFocus();
                            InputMethodManager imm = (InputMethodManager) ((android.app.Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    CheckBox chkCtl = (CheckBox) ((android.app.Activity) context).findViewById(resID);
                    isChecked = chkCtl.isChecked();
                    chkCtl.requestFocus();

                    if (!isChecked) {
                        //isValid = isChecked;
                        //msg(val[2] + " is important.");
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

                    Spinner sp = (Spinner) ((android.app.Activity) context).findViewById(resID);
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
                    RadioButton chkCtl = (RadioButton) ((android.app.Activity) context).findViewById(resID);
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

    public InputFilter getEditTextFilter() {
        return new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
                Matcher ms = ps.matcher(String.valueOf(c));
                return ms.matches();
            }
        };
    }


    public String toTitleCase(String givenString) {
        String text = "";
        if(!TextUtils.isEmpty(givenString)){
            String[] arr = givenString.split(" ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                sb.append(Character.toUpperCase(arr[i].charAt(0)))
                        .append(arr[i].substring(1)).append(" ");
            }
            text = sb.toString().trim();
        }
        return text;
    }
}
