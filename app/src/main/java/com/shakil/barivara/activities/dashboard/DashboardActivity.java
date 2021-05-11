package com.shakil.barivara.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityDashboardBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.SpinnerAdapter;
import com.shakil.barivara.utils.SpinnerData;

import java.util.ArrayList;

import static com.shakil.barivara.utils.Constants.mAppViewCount;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding activityDashboardBinding;
    private FirebaseCrudHelper firebaseCrudHelper;
    private PrefManager prefManager;
    private SpinnerAdapter spinnerAdapter;
    private SpinnerData spinnerData;

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
        prefManager = new PrefManager(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
    }
    //endregion

    //region init objects
    private void bindUiWithComponents(){
        spinnerAdapter.setSpinnerAdapter(activityDashboardBinding.FilterMonth,this,spinnerData.setMonthData());

        //region set all counts
        firebaseCrudHelper.fetchAllTenant("tenant", new FirebaseCrudHelper.onTenantDataFetch() {
            @Override
            public void onFetch(ArrayList<Tenant> objects) {
                activityDashboardBinding.TotalTenants.setText(""+objects.size());
            }
        });

        firebaseCrudHelper.fetchAllMeter("meter", new FirebaseCrudHelper.onDataFetch() {
            @Override
            public void onFetch(ArrayList<Meter> objects) {
                activityDashboardBinding.TotalMeters.setText(""+objects.size());
            }
        });

        firebaseCrudHelper.fetchAllRoom("room", new FirebaseCrudHelper.onRoomDataFetch() {
            @Override
            public void onFetch(ArrayList<Room> objects) {
                activityDashboardBinding.TotalRooms.setText(""+objects.size());
            }
        });

        firebaseCrudHelper.getAdditionalInfo("rent", "rentAmount", new FirebaseCrudHelper.onAdditionalInfoFetch() {
            @Override
            public void onFetched(double data) {
                activityDashboardBinding.TotalRentAmount.animateText(String.valueOf(data)+" "+getString(R.string.taka));
            }
        });

        firebaseCrudHelper.getAdditionalInfo("electricityBill", "totalBill", new FirebaseCrudHelper.onAdditionalInfoFetch() {
            @Override
            public void onFetched(double data) {
                activityDashboardBinding.TotalElectricityBill.animateText(String.valueOf(data)+" "+getString(R.string.taka));
            }
        });

        activityDashboardBinding.AppVisitCount.setText(String.valueOf(prefManager.getInt(mAppViewCount)));
        //endregion
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
    }

    //endregion
}