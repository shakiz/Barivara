package com.shakil.barivara.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.shakil.barivara.R;

import java.util.Date;

public class Tools {
    private Context context;
    private View view;

    public Tools(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static Date loadDate(Cursor cursor) {
        return new Date(cursor.getLong(cursor.getColumnIndex("")));
    }

    public void exitApp(){
        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
        exitIntent.addCategory(Intent.CATEGORY_HOME);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(exitIntent);
    }

    public void setCustomDesignTextView(int resId){
        TextView textView = view.findViewById(resId);
        textView.setTextColor(view.getResources().getColor(R.color.md_blue_grey_800));
        textView.setSingleLine();
    }

    public int toIntValue(String value){
        try{
            Log.v("shakil",""+ Integer.parseInt(value));
            return Integer.parseInt(value);
        }
        catch (Exception e){
            Log.v("shakil",e.getMessage());
            return 0;
        }
    }

    //region hide soft keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //endregion
}
