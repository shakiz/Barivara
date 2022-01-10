package com.shakil.barivara.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shakil.barivara.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.shakil.barivara.utils.Constants.TAG;

public class UtilsForAll {

    private Context context;
    private View view;

    public UtilsForAll(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public UtilsForAll() {
    }

    public UtilsForAll(Context context) {
        this.context = context;
    }

    public UtilsForAll(View view) {
        this.view = view;
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
            Log.v(TAG,""+ Integer.parseInt(value));
            return Integer.parseInt(value);
        }
        catch (Exception e){
            Log.v(TAG,e.getMessage());
            return 0;
        }
    }

    //region get greetings
    public String setGreetings() {
        String greetings = "";
        Calendar calendar = Calendar.getInstance();
        int timeOfTheDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfTheDay >= 0 && timeOfTheDay < 12) {
            greetings = context.getString(R.string.good_morning);
        } else if (timeOfTheDay >= 12 && timeOfTheDay < 16) {
            greetings = context.getString(R.string.good_noon);
        } else if (timeOfTheDay >= 16 && timeOfTheDay < 18) {
            greetings = context.getString(R.string.good_afternoon);
        } else if (timeOfTheDay >= 18 && timeOfTheDay < 20) {
            greetings = context.getString(R.string.good_evening);
        } else {
            greetings = context.getString(R.string.good_night);
        }
        return greetings;
    }
    //endregion

    //region get dateTime text
    public String getDateTimeText() {
        DateFormat df = new SimpleDateFormat("MMM d, yyyy || EEE");
        String dateTimeText = df.format(new Date());
        return dateTimeText;
    }
    //endregion

    //region get date
    public String getDateTime() {
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String dateTimeText = df.format(new Date());
        return dateTimeText;
    }
    //endregion

    //region get dateTime with PM
    public String getDateTimeWithPM() {
        String  currentDateTimeString = DateFormat.getDateTimeInstance()
                .format(new Date());
        return currentDateTimeString;
    }
    //endregion

    //region get day of the month
    public String getDayOfTheMonth(){
        DateFormat dateFormat = new SimpleDateFormat("EEE");
        String day = dateFormat.format(new Date());
        return day;
    }
    //endregion

    //region mobile number validation
    public boolean isValidMobileNo(String mobileNo){
        boolean isValid = false;
        if (mobileNo.length() == 11){
            isValid = true;
        }
        return isValid;
    }
    //endregion
}
