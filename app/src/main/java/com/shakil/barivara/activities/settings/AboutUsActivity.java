package com.shakil.barivara.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends AppCompatActivity {
    private ActivityAboutUsBinding activityBinding;

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

    }
    //endregion

    //region perform all UI interactions
    private void bindUIWithComponents() {

    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
    }

    //endregion
}