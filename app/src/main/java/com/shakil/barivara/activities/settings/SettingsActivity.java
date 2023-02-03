package com.shakil.barivara.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivitySettingsBinding;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.LanguageManager;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.Tools;

import java.util.HashMap;

import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mLanguage;
import static com.shakil.barivara.utils.Constants.mUserEmail;
import static com.shakil.barivara.utils.Constants.mUserFullName;
import static com.shakil.barivara.utils.Constants.mUserMobile;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding activitySettingsBinding;
    public HashMap<String, String> languageMap = new HashMap<String, String>();
    private LanguageManager languageManager;
    private PrefManager prefManager;
    private Tools tools;
    private CustomAdManager customAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        setSupportActionBar(activitySettingsBinding.toolBar);

        activitySettingsBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        init();
        bindUiWithComponents();
    }

    //region init objects
    private void init() {
        languageManager = new LanguageManager(this);
        prefManager = new PrefManager(this);
        tools = new Tools(this);
        customAdManager = new CustomAdManager(this);
        setupLanguage();
    }
    //endregion

    private void setupLanguage(){
        languageMap.clear();
        languageMap.put("bn", getString(R.string.bengali));
        languageMap.put("en", getString(R.string.english));
    }

    //region perform all UI operations
    private void bindUiWithComponents() {

        //region for ad
        customAdManager.generateAd(activitySettingsBinding.adView);
        //endregion

        //region account section
        if (!TextUtils.isEmpty(prefManager.getString(mUserFullName))){
            activitySettingsBinding.UserFullName.setText(getString(R.string.username)+":"+prefManager.getString(mUserFullName));
        }
        else{
            activitySettingsBinding.UserFullName.setText(getString(R.string.username_not_found));
        }
        if (!TextUtils.isEmpty(prefManager.getString(mUserEmail))){
            activitySettingsBinding.Email.setText(getString(R.string.email)+":"+prefManager.getString(mUserEmail));
        }
        else{
            activitySettingsBinding.Email.setText(getString(R.string.email_not_found));
        }
        if (!TextUtils.isEmpty(prefManager.getString(mUserMobile))){
            activitySettingsBinding.Mobile.setText(getString(R.string.mobile)+":"+prefManager.getString(mUserMobile));
        }
        else{
            activitySettingsBinding.Mobile.setText(getString(R.string.mobile_not_found));
        }
        //endregion

        //region language part
        String language = prefManager.getString(mLanguage);
        if(language != null){
            if(!TextUtils.isEmpty(language)){
                activitySettingsBinding.currentAppLanguage.setText(languageMap.get(language));
            } else {
                activitySettingsBinding.currentAppLanguage.setText(getString(R.string.english));
            }
        } else {
            activitySettingsBinding.currentAppLanguage.setText(getString(R.string.english));
        }
        //endregion

        //region logout click listener
        activitySettingsBinding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.doPopUpForLogout(LoginActivity.class);
            }
        });
        //endregion

        //region language and login click listeners
        activitySettingsBinding.language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageManager.setLanguage(MainActivity.class);
            }
        });

        activitySettingsBinding.googleLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefManager.getBoolean(mIsLoggedIn)) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.already_logged_in), Toast.LENGTH_SHORT).show();
                    activitySettingsBinding.googleLoginLayout.setClickable(false);
                    activitySettingsBinding.googleLoginLayout.setActivated(false);
                } else {
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                }
            }
        });
        //endregion
    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    //endregion
}