package com.shakil.barivara.activities.settings;

import static com.shakil.barivara.utils.Constants.GIT_LIB_PACKAGE_NAME;
import static com.shakil.barivara.utils.Constants.MY_CONTACT_NO;
import static com.shakil.barivara.utils.Constants.SAGORKONNA_PACKAGE_NAME;
import static com.shakil.barivara.utils.Constants.VARA_ADAI_FB_PAGE_LINK;

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

        setSupportActionBar(activityBinding.toolBar);
        activityBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
            }
        });

        initUI();
        bindUIWithComponents();
    }

    private void initUI() {
        tools = new Tools(this);
    }

    private void bindUIWithComponents() {
        activityBinding.giLibApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.launchAppByPackageName(GIT_LIB_PACKAGE_NAME);
            }
        });
        activityBinding.sagorKonnyaApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.launchAppByPackageName(SAGORKONNA_PACKAGE_NAME);
            }
        });

        activityBinding.fbContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(getApplicationContext(),getString(R.string.opening_facebook), Toasty.LENGTH_LONG).show();
                tools.launchUrl(VARA_ADAI_FB_PAGE_LINK);
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
                tools.sendMessage(MY_CONTACT_NO);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
    }
}