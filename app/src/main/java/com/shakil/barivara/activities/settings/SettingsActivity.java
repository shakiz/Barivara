package com.shakil.barivara.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivitySettingsBinding;
import com.shakil.barivara.utils.LanguageManager;
import com.shakil.barivara.utils.PrefManager;

import java.util.HashMap;

import static com.shakil.barivara.utils.Constants.mLanguage;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding activitySettingsBinding;
    public HashMap<String, String> languageMap = new HashMap<String, String>();
    private LanguageManager languageManager;
    private PrefManager prefManager;

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

        activitySettingsBinding.language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageManager.setLanguage(new LanguageManager.onSetLanguageListener() {
                    @Override
                    public void onSet() {
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    }
                });
            }
        });
    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    //endregion
}