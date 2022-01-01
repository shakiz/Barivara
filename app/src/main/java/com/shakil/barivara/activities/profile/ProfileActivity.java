package com.shakil.barivara.activities.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(activitySettingsBinding.toolBar);

        activitySettingsBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //region init objects and perform all UI operations
        initUI();
        bindUiWithComponents();
        //endregion
    }

    //region init objects
    private void initUI() {

    }
    //endregion

    //region perform all UI operations
    private void bindUiWithComponents() {

    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
    //endregion
}