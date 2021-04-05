package com.shakil.barivara.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityDashboardBinding;
import com.shakil.barivara.dbhelper.DbHelperParent;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding activityDashboardBinding;
    private DbHelperParent dbHelperParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        init();
        bindUiWithComponents();
        setSupportActionBar(activityDashboardBinding.toolBar);

        activityDashboardBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //region init objects
    private void init(){
        dbHelperParent = new DbHelperParent(this);
    }
    //endregion

    //region init objects
    private void bindUiWithComponents(){
        activityDashboardBinding.TotalMeters.animateText(String.valueOf(dbHelperParent.getTotalMeterRows()));
        activityDashboardBinding.TotalRooms.animateText(String.valueOf(dbHelperParent.getTotalRoomRows()));
        activityDashboardBinding.TotalTenants.animateText(String.valueOf(dbHelperParent.getTotalTenantRows()));
        activityDashboardBinding.TotalRentAmount.animateText(String.valueOf(dbHelperParent.getTotalRentAmount())+" "+getString(R.string.taka));
        activityDashboardBinding.TotalElectricityBill.animateText(String.valueOf(dbHelperParent.getTotalElectricityBillCollection())+" "+getString(R.string.taka));
        activityDashboardBinding.TotalUnit.animateText(String.valueOf(dbHelperParent.getTotalUnit())+" "+getString(R.string.units));
    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelperParent.close();
    }

    //endregion
}