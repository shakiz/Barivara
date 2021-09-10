package com.shakil.barivara.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shakil.barivara.R;

import java.util.Date;
import java.util.regex.Matcher;

import static com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE;
import static com.shakil.barivara.utils.Constants.TAG;
import static com.shakil.barivara.utils.Constants.VALID_EMAIL_ADDRESS_REGEX;
import static com.shakil.barivara.utils.Constants.mAppViewCount;
import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mLanguage;
import static com.shakil.barivara.utils.Constants.mUserEmail;
import static com.shakil.barivara.utils.Constants.mUserFullName;
import static com.shakil.barivara.utils.Constants.mUserId;
import static com.shakil.barivara.utils.Constants.mUserMobile;

public class Tools {
    private final Context context;
    private PrefManager prefManager;

    public Tools(Context context, View view) {
        this.context = context;
        prefManager = new PrefManager(context);
    }

    public Tools(Context context) {
        this.context = context;
    }

    public void askCallPermission(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_CODE);
        }
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

    //region rate app in google play store
    public void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.unable_to_open_play_store), Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region logout action
    public void doPopUpForLogout(Class to){
        Button cancel, logout;
        Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_confirmation_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        cancel = dialog.findViewById(R.id.cancelButton);
        logout = dialog.findViewById(R.id.logoutButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPrefForLogout(to);
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    //endregion

    //region clear login pref
    private void clearPrefForLogout(Class to){
        prefManager.set(mAppViewCount, 0);
        prefManager.set(mIsLoggedIn, false);
        prefManager.set(mUserId, "");
        prefManager.set(mLanguage, "en");
        prefManager.set(mUserFullName, "");
        prefManager.set(mUserEmail, "");
        prefManager.set(mUserMobile, "");
        context.startActivity(new Intent(context, to));
        Toast.makeText(context, context.getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region validate email address
    public boolean validateEmailAddress(String emailAddress){
        boolean isValidEmail = false;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        isValidEmail = matcher.find();
        return isValidEmail;
    }
    //endregion

    //region get app version name
    public String getAppVersionName() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(info.versionName);
    }
    //endregion

    //region internet connection check
    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    //endregion

    //region send message
    public void sendMessage(String number){
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.setData(Uri.parse("sms:" + number));
        context.startActivity(smsIntent);
    }
    //endregion

    //region validate mobile number
    public boolean isValidMobile(String mobileNumber) {
        boolean valid = false;
        if (mobileNumber != null) {
            if (mobileNumber.length() == 11) {
                String[] codes = {"011", "013", "014", "015", "016", "017", "018", "019"};
                String userCode = mobileNumber.substring(0, 3);
                for (String tempCode : codes) {
                    if (tempCode.equals(userCode)) {
                        valid = true;
                        break;
                    }
                }
            } else {
                valid = false;
            }
        }
        return valid;
    }
    //endregion
}
