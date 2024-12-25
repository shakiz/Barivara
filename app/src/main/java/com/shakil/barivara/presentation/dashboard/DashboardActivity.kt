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
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.orZero
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {
    private lateinit var activityDashboardBinding: ActivityDashboardBinding

    @Inject
    lateinit var prefManager: PrefManager
    private lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var year: Int = 0
    private var month: Int = 0
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
        viewModel.getDashboardInfo(prefManager.getString(mAccessToken))
    }

    private fun init() {
        utilsForAll = UtilsForAll(this)
        ux = UX(this)
    }

    private fun initListeners() {
        activityDashboardBinding.toolBar.setNavigationOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }

        viewModel.getDashboardInfo().observe(this) { dashboardInfo ->
            activityDashboardBinding.TotalRooms.text = "${dashboardInfo.totalActiveRoom}"
            activityDashboardBinding.TotalTenantsCurrent.text = "${dashboardInfo.totalActiveTenant}"
            activityDashboardBinding.TotalRentAmount.text = "${dashboardInfo.totalCollectedRent}"
        }

        viewModel.getRentDataByYearAndMonth().observe(this) { dashboardInfo ->
            dashboardInfo?.let {
                activityDashboardBinding.TotalRentAmountByYearAndMonth.text =
                    getString(R.string.x_taka, dashboardInfo.totalPaid.orZero())
                activityDashboardBinding.TotalDueAmountByYearAndMonth.text =
                    getString(R.string.x_taka, dashboardInfo.totalDue.orZero())
            }
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

        activityDashboardBinding.FilterYear.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (parent.getItemAtPosition(position)
                            .toString() != getString(R.string.select_data_1)
                    ) {
                        year = parent.getItemAtPosition(position).toString().toInt()
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
                    if (parent.getItemAtPosition(position)
                            .toString() != getString(R.string.select_data_1)
                    ) {
                        month = utilsForAll.getMonthFromMonthName(
                            parent.getItemAtPosition(position).toString()
                        )
                        viewModel.getRentDataByYearAndMonth(
                            prefManager.getString(mAccessToken),
                            year,
                            month
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(Constants.TAG, "FilterMonth Spinner : onNothingSelected")
                }
            }
    }
}