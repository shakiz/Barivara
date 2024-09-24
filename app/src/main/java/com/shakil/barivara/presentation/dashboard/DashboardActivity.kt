package com.shakil.barivara.presentation.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityDashboardBinding
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var prefManager: PrefManager
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var customAdManager = CustomAdManager(this)
    private val viewModel by viewModels<DashboardViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_dashboard

    override fun setVariables(dataBinding: ActivityDashboardBinding) {
        activityDashboardBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        bindUiWithComponents()
        setSupportActionBar(activityDashboardBinding.toolBar)
        initListeners()
        initObservers()
        viewModel.getDashboardInfo(prefManager.getString(Constants.mAccessToken))
    }

    private fun init() {
        prefManager = PrefManager(this)
    }

    private fun initListeners() {
        activityDashboardBinding.toolBar.setNavigationOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun initObservers() {
        viewModel.getDashboardInfo().observe(this) { dashboardInfo ->
            activityDashboardBinding.TotalRooms.text = "${dashboardInfo.totalActiveRoom}"
            activityDashboardBinding.TotalTenantsCurrent.text = "${dashboardInfo.totalActiveTenant}"
            activityDashboardBinding.TotalTenantLeft.text = "${dashboardInfo.totalCollectedRent}"
        }
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
                    //TODO add filter api here
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
                    //TODO add filter api here
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(Constants.TAG, "FilterMonth Spinner : onNothingSelected")
                }
            }
    }
}