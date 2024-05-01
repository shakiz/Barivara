package com.shakil.barivara.utils;

import static com.shakil.barivara.utils.Constants.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shakil.barivara.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilsForAll {

    private final Context context;

    public UtilsForAll(Context context) {
        this.context = context;
    }

    public void exitApp(){
        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
        exitIntent.addCategory(Intent.CATEGORY_HOME);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(exitIntent);
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

    public String getDateTime() {
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String dateTimeText = df.format(new Date());
        return dateTimeText;
    }

    public String getDateTimeWithPM() {
        String  currentDateTimeString = DateFormat.getDateTimeInstance()
                .format(new Date());
        return currentDateTimeString;
    }

    public String getDayOfTheMonth(){
        DateFormat dateFormat = new SimpleDateFormat("EEE");
        String day = dateFormat.format(new Date());
        return day;
    }

    public boolean isValidMobileNo(String mobileNo){
        boolean isValid = mobileNo.length() == 11;
        return isValid;
    }
}
