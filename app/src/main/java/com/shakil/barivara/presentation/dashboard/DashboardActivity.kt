package com.shakil.barivara.presentation.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
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

    @Inject
    lateinit var utilsForAll: UtilsForAll
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

        val cartesian: Cartesian = AnyChart.column()

        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Jan", 80540))
        data.add(ValueDataEntry("Feb", 94190))
        data.add(ValueDataEntry("Mar", 102610))
        data.add(ValueDataEntry("Apr", 110430))
        data.add(ValueDataEntry("May", 128000))
        data.add(ValueDataEntry("Jun", 143760))
        data.add(ValueDataEntry("Jul", 170670))
        data.add(ValueDataEntry("Aug", 213210))
        data.add(ValueDataEntry("Sep", 29980))
        data.add(ValueDataEntry("Oct", 259980))
        data.add(ValueDataEntry("Nov", 269980))
        data.add(ValueDataEntry("Dec", 79980))
        val column: Column = cartesian.column(data)
        column.tooltip()
            .titleFormat("{%X}")
            .position(Position.LEFT_CENTER)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("৳{%Value}{groupsSeparator: }")

        cartesian.credits().enabled(false)
        cartesian.xAxis(0).labels().enabled(true)
        cartesian.xAxis(0).labels().rotation(-45)
        cartesian.xAxis(0).staggerMode(true)
        cartesian.xAxis(0).staggerMaxLines(2)
        cartesian.animation(true)
        cartesian.title("Monthly Rent Collection")
        cartesian.yScale().minimum(0.0)
        cartesian.yAxis(0).labels().format("৳{%Value}{groupsSeparator: }")
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)

        activityDashboardBinding.monthlyRentView.setChart(cartesian)
    }

    private fun init() {
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
        setMonthSpinnerAdapter()

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
                        setMonthSpinnerAdapter()
                        if (month != 0) {
                            viewModel.getRentDataByYearAndMonth(
                                year,
                                month
                            )
                        }
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

    private fun setMonthSpinnerAdapter() {
        spinnerAdapter.setSpinnerAdapter(
            activityDashboardBinding.FilterMonth,
            this,
            spinnerData.setMonthData(year)
        )
    }
}