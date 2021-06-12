package com.shakil.barivara.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shakil.barivara.R;

import java.util.Date;
import java.util.regex.Matcher;

import static com.shakil.barivara.utils.Constants.VALID_EMAIL_ADDRESS_REGEX;
import static com.shakil.barivara.utils.Constants.mAppViewCount;
import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mLanguage;
import static com.shakil.barivara.utils.Constants.mUserEmail;
import static com.shakil.barivara.utils.Constants.mUserFullName;
import static com.shakil.barivara.utils.Constants.mUserId;
import static com.shakil.barivara.utils.Constants.mUserMobile;

public class Tools {
    private Context context;
    private View view;
    private PrefManager prefManager;

    public Tools(Context context, View view) {
        this.context = context;
        this.view = view;
        prefManager = new PrefManager(context);
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
}
