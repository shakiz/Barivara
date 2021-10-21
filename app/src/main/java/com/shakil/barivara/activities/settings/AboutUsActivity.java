package com.shakil.barivara.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityAboutUsBinding;
import com.shakil.barivara.utils.Tools;

import es.dmoral.toasty.Toasty;

public class AboutUsActivity extends AppCompatActivity {
    private ActivityAboutUsBinding activityBinding;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);

        //region toolbar
        setSupportActionBar(activityBinding.toolBar);

        activityBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
            }
        });
        //endregion

        //region init UI and perform all UI interactions
        initUI();
        bindUIWithComponents();
        //endregion
    }

    //region init UI
    private void initUI() {
        tools = new Tools(this);
    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents() {
        //region launch apps
        activityBinding.giLibApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.launchAppByPackageName("app.com.gitlib");
            }
        });
        activityBinding.sagorKonnyaApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.launchAppByPackageName("com.shakil.tourdekuakata");
            }
        });
        //endregion

        //region social click listeners
        activityBinding.fbContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(getApplicationContext(),getString(R.string.opening_facebook), Toasty.LENGTH_LONG).show();
                tools.launchUrl("https://www.facebook.com/varaadai");
            }
        });
        activityBinding.mailContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.launchGmail();
            }
        });
        activityBinding.messageContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.sendMessage("01688499299");
            }
        });
        //endregion
    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
    }

    //endregion
}