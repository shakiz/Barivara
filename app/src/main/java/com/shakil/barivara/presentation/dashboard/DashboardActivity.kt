package com.shakil.barivara.presentation.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityDashboardBinding
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.data.model.meter.Meter
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData

class DashboardActivity : AppCompatActivity() {
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var prefManager: PrefManager
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var customAdManager = CustomAdManager(this)
    private var totalTenantCurrent = 0
    private var totalTenantLeft = 0

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        prefManager = PrefManager(this)
        bindUiWithComponents()
        setSupportActionBar(activityDashboardBinding.toolBar)
        activityDashboardBinding.toolBar.setNavigationOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
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
        firebaseCrudHelper.fetchAllTenant(
            prefManager.getString(mUserId),
            "tenant",
            object : FirebaseCrudHelper.onTenantDataFetch {
                override fun onFetch(objects: ArrayList<Tenant?>?) {
                    if (objects != null) {
                        for (tenant in objects) {
                            if (tenant?.isActiveValue != null) {
                                if (tenant.isActiveValue == getString(R.string.yes)) {
                                    totalTenantCurrent++
                                } else if (tenant.isActiveValue == getString(R.string.no)) {
                                    totalTenantLeft++
                                }
                            }
                        }
                    }
                    activityDashboardBinding.TotalTenantsCurrent.text = "" + totalTenantCurrent
                    activityDashboardBinding.TotalTenantLeft.text = "" + totalTenantLeft
                }
            })
        firebaseCrudHelper.fetchAllMeter(
            prefManager.getString(mUserId),
            "meter",
            object : FirebaseCrudHelper.onDataFetch {
                override fun onFetch(objects: ArrayList<Meter?>?) {
                    activityDashboardBinding.TotalMeters.text = "" + objects?.size
                }
            })

        firebaseCrudHelper.fetchAllRoom(
            "room",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onRoomDataFetch {
                override fun onFetch(objects: ArrayList<Room?>?) {
                    activityDashboardBinding.TotalRooms.text = "" + objects?.size
                }
            })

        firebaseCrudHelper.getAdditionalInfo(
            "rent",
            prefManager.getString(mUserId),
            "rentAmount",
            object : FirebaseCrudHelper.onAdditionalInfoFetch {
                override fun onFetched(data: Double) {
                    activityDashboardBinding.TotalRentAmount.text =
                        data.toString() + " " + getString(R.string.taka)
                }
            }
        )

        firebaseCrudHelper.getAdditionalInfo(
            "electricityBill",
            prefManager.getString(mUserId),
            "totalBill",
            object : FirebaseCrudHelper.onAdditionalInfoFetch {
                override fun onFetched(data: Double) {
                    activityDashboardBinding.TotalElectricityBill.text =
                        data.toString() + " " + getString(R.string.taka)
                }
            }
        )

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
                        prefManager.getString(mUserId),
                        parent.getItemAtPosition(position).toString(),
                        "RentAmount",
                        object : FirebaseCrudHelper.onDataQueryYearlyRent {
                            override fun onQueryFinished(data: Int) {
                                activityDashboardBinding.TotalRentAmountYearly.text =
                                    data.toString() + " " + getString(R.string.taka)
                            }
                        }
                    )
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
                        prefManager.getString(mUserId),
                        parent.getItemAtPosition(position).toString(),
                        "RentAmount",
                        object : FirebaseCrudHelper.onDataQuery {
                            override fun onQueryFinished(data: Int) {
                                activityDashboardBinding.TotalRentAmountMonthly.text =
                                    data.toString() + " " + getString(R.string.taka)
                            }
                        }
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(Constants.TAG, "FilterMonth Spinner : onNothingSelected")
                }
            }
    }
}