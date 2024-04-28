package com.shakil.barivara.activities.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityDashboardBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData

class DashboardActivity : AppCompatActivity() {
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var prefManager = PrefManager(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var customAdManager = CustomAdManager(this)
    private var totalTenantCurrent = 0
    private var totalTenantLeft = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        bindUiWithComponents()
        setSupportActionBar(activityDashboardBinding.toolBar)
        activityDashboardBinding.toolBar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun bindUiWithComponents() {
        spinnerAdapter.setSpinnerAdapter(
            activityDashboardBinding.FilterYear,
            this,
            spinnerData.setYearData()
        )
        spinnerAdapter.setSpinnerAdapter(
            activityDashboardBinding.FilterMonth,
            this,
            spinnerData.setMonthData()
        )
        customAdManager.generateAd(activityDashboardBinding.adView)
        customAdManager.generateAd(activityDashboardBinding.adViewSecond)
        firebaseCrudHelper.fetchAllTenant("tenant") { objects ->
            for (tenant in objects) {
                if (tenant.isActiveValue != null) {
                    if (tenant.isActiveValue == getString(R.string.yes)) {
                        totalTenantCurrent++
                    } else if (tenant.isActiveValue == getString(R.string.no)) {
                        totalTenantLeft++
                    }
                }
            }
            activityDashboardBinding.TotalTenantsCurrent.text = "" + totalTenantCurrent
            activityDashboardBinding.TotalTenantLeft.text = "" + totalTenantLeft
        }
        firebaseCrudHelper.fetchAllMeter("meter") { objects ->
            activityDashboardBinding.TotalMeters.text = "" + objects.size
        }
        firebaseCrudHelper.fetchAllRoom("room") { objects ->
            activityDashboardBinding.TotalRooms.text = "" + objects.size
        }
        firebaseCrudHelper.getAdditionalInfo(
            "rent",
            "rentAmount"
        ) { data ->
            activityDashboardBinding.TotalRentAmount.text =
                data.toString() + " " + getString(R.string.taka)
        }
        firebaseCrudHelper.getAdditionalInfo(
            "electricityBill",
            "totalBill"
        ) { data ->
            activityDashboardBinding.TotalElectricityBill.text =
                data.toString() + " " + getString(R.string.taka)
        }
        activityDashboardBinding.AppVisitCount.text =
            prefManager.getInt(Constants.mAppViewCount).toString()
        activityDashboardBinding.FilterYear.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    firebaseCrudHelper.getRentDataByYear(
                        "rent",
                        parent.getItemAtPosition(position).toString(),
                        "RentAmount"
                    ) { data ->
                        activityDashboardBinding.TotalRentAmountYearly.text =
                            data.toString() + " " + getString(R.string.taka)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(Constants.TAG, "FilterYear Spinner : onNothingSelected")
                }
            }
        activityDashboardBinding.FilterMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    firebaseCrudHelper.getRentDataByMonth(
                        "rent",
                        parent.getItemAtPosition(position).toString(),
                        "RentAmount"
                    ) { data ->
                        activityDashboardBinding.TotalRentAmountMonthly.text =
                            data.toString() + " " + getString(R.string.taka)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(Constants.TAG, "FilterMonth Spinner : onNothingSelected")
                }
            }
    }
}