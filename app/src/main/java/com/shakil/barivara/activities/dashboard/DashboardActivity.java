package com.shakil.barivara.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityDashboardBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.CustomAdManager;
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
    private CustomAdManager customAdManager;
    private int totalTenantCurrent = 0, totalTenantLeft = 0;

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

    private void init() {
        prefManager = new PrefManager(this);
        customAdManager = new CustomAdManager(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        spinnerData = new SpinnerData(this);
        spinnerAdapter = new SpinnerAdapter();
    }

    private void bindUiWithComponents() {
        spinnerAdapter.setSpinnerAdapter(activityDashboardBinding.FilterYear, this, spinnerData.setYearData());
        spinnerAdapter.setSpinnerAdapter(activityDashboardBinding.FilterMonth, this, spinnerData.setMonthData());

        customAdManager.generateAd(activityDashboardBinding.adView);
        customAdManager.generateAd(activityDashboardBinding.adViewSecond);

        firebaseCrudHelper.fetchAllTenant("tenant", new FirebaseCrudHelper.onTenantDataFetch() {
            @Override
            public void onFetch(ArrayList<Tenant> objects) {
                for (Tenant tenant : objects) {
                    if (tenant.getIsActiveValue() != null) {
                        if (tenant.getIsActiveValue().equals(getString(R.string.yes))) {
                            totalTenantCurrent++;
                        } else if (tenant.getIsActiveValue().equals(getString(R.string.no))) {
                            totalTenantLeft++;
                        }
                    }
                }
                activityDashboardBinding.TotalTenantsCurrent.setText("" + totalTenantCurrent);
                activityDashboardBinding.TotalTenantLeft.setText("" + totalTenantLeft);
            }
        });

        firebaseCrudHelper.fetchAllMeter("meter", new FirebaseCrudHelper.onDataFetch() {
            @Override
            public void onFetch(ArrayList<Meter> objects) {
                activityDashboardBinding.TotalMeters.setText("" + objects.size());
            }
        });

        firebaseCrudHelper.fetchAllRoom("room", new FirebaseCrudHelper.onRoomDataFetch() {
            @Override
            public void onFetch(ArrayList<Room> objects) {
                activityDashboardBinding.TotalRooms.setText("" + objects.size());
            }
        });

        firebaseCrudHelper.getAdditionalInfo("rent", "rentAmount", new FirebaseCrudHelper.onAdditionalInfoFetch() {
            @Override
            public void onFetched(double data) {
                activityDashboardBinding.TotalRentAmount.setText(data + " " + getString(R.string.taka));
            }
        });

        firebaseCrudHelper.getAdditionalInfo("electricityBill", "totalBill", new FirebaseCrudHelper.onAdditionalInfoFetch() {
            @Override
            public void onFetched(double data) {
                activityDashboardBinding.TotalElectricityBill.setText(data + " " + getString(R.string.taka));
            }

        });

        activityDashboardBinding.AppVisitCount.setText(String.valueOf(prefManager.getInt(mAppViewCount)));

        activityDashboardBinding.FilterYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firebaseCrudHelper.getRentDataByYear("rent", String.valueOf(parent.getItemAtPosition(position))
                        , "RentAmount", new FirebaseCrudHelper.onDataQueryYearlyRent() {
                            @Override
                            public void onQueryFinished(int data) {
                                activityDashboardBinding.TotalRentAmountYearly.setText(data + " " + getString(R.string.taka));
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(Constants.TAG, "FilterYear Spinner : onNothingSelected");
            }
        });

        activityDashboardBinding.FilterMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firebaseCrudHelper.getRentDataByMonth("rent", String.valueOf(parent.getItemAtPosition(position))
                        , "RentAmount", new FirebaseCrudHelper.onDataQuery() {
                            @Override
                            public void onQueryFinished(int data) {
                                activityDashboardBinding.TotalRentAmountMonthly.setText(data + " " + getString(R.string.taka));
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(Constants.TAG, "FilterMonth Spinner : onNothingSelected");
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}