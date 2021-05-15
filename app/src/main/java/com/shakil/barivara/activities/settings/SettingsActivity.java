package com.shakil.barivara.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivitySettingsBinding;
import com.shakil.barivara.utils.LanguageManager;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding activitySettingsBinding;
    private LanguageManager languageManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        setSupportActionBar(activitySettingsBinding.toolBar);

        languageManager = new LanguageManager(this);

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

    }
    //endregion

    //region perform all UI operations
    private void bindUiWithComponents() {
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