package com.shakil.barivara.utils;

import static com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE;
import static com.shakil.barivara.utils.Constants.TAG;
import static com.shakil.barivara.utils.Constants.VALID_EMAIL_ADDRESS_REGEX;
import static com.shakil.barivara.utils.Constants.mAppViewCount;
import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mLanguage;
import static com.shakil.barivara.utils.Constants.mOldUser;
import static com.shakil.barivara.utils.Constants.mUserEmail;
import static com.shakil.barivara.utils.Constants.mUserFullName;
import static com.shakil.barivara.utils.Constants.mUserId;
import static com.shakil.barivara.utils.Constants.mUserMobile;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.activities.onboard.SplashActivity;
import com.shakil.barivara.activities.onboard.WelcomeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import es.dmoral.toasty.Toasty;

public class Tools {
    private final Context context;
    private final PrefManager prefManager;

    public Tools(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);
    }

    public void askCallPermission(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_CODE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.unable_to_open_play_store), Toast.LENGTH_LONG).show();
        }
    }

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
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void doPopUpForExitApp(){
        Button cancel, exit;
        Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_app_confirmation_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        cancel = dialog.findViewById(R.id.cancelButton);
        exit = dialog.findViewById(R.id.exitButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new UtilsForAll(context).exitApp();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

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

    public boolean validateEmailAddress(String emailAddress){
        boolean isValidEmail = false;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        isValidEmail = matcher.find();
        return isValidEmail;
    }

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

    public void sendMessage(String number){
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.setData(Uri.parse("sms:" + number));
        context.startActivity(smsIntent);
    }

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

    public void checkLogin(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (prefManager.getBoolean(mOldUser)){
                    if (prefManager.getBoolean(mIsLoggedIn)) {
                        intent = new Intent(context, MainActivity.class);
                    } else {
                        intent = new Intent(context, LoginActivity.class);
                    }
                }
                else{
                    intent = new Intent(context, WelcomeActivity.class);
                }
                context.startActivity(intent);
            }
        }, 1000);
    }

    public void generatePDF(String content){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PdfDocument myPdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

            Paint myPaint = new Paint();
            String myString = content;
            int x = 10, y=25;

            for (String line:myString.split("\n")){
                myPage.getCanvas().drawText(line, x, y, myPaint);
                y+=myPaint.descent()-myPaint.ascent();
            }

            myPdfDocument.finishPage(myPage);

            String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/myPDFFile.pdf";
            File myFile = new File(myFilePath);
            try {
                myPdfDocument.writeTo(new FileOutputStream(myFile));
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, "ERROR!!!!", Toast.LENGTH_SHORT).show();
            }

            myPdfDocument.close();
        }
        else{
            Toast.makeText(context, context.getString(R.string.your_device_does_not_support_this_feature), Toast.LENGTH_LONG).show();
        }
    }

    public void setLoginPrefs(@NonNull Task<AuthResult> task){
        prefManager.set(mIsLoggedIn, true);
        if (task.getResult() != null){
            prefManager.set(mUserId, task.getResult().getUser().getUid());
            prefManager.set(mUserFullName, task.getResult().getUser().getDisplayName());
            prefManager.set(mUserEmail, task.getResult().getUser().getEmail());
            prefManager.set(mUserMobile, task.getResult().getUser().getPhoneNumber());
        }
    }

    public void launchAppByPackageName(String appPackageName){
        try {
            Toasty.info(context,context.getString(R.string.redirecting_to_play_store), Toasty.LENGTH_LONG).show();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void launchUrl(String url){
        if (!url.contains("https")){
            url = "https://"+url;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public void launchGmail(){
        Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts(
                "mailto", "shakil.play335@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is my subject text");
        context.startActivity(Intent.createChooser(emailIntent, null));
    }

    public boolean isFirstDayOfMonth(Calendar calendar){
        if (calendar == null) {
            throw new IllegalArgumentException("Calendar cannot be null.");
        }
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i(TAG+"-Current Day","::"+dayOfMonth);
        //return dayOfMonth == 1;
        return true;
    }

    public static void sendNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                1001,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, "1001")
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
