package com.shakil.barivara.presentation.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.anychart.AnyChart
import com.anychart.AnyChartView
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
    private var selectedYearForYearly: Int = 0
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
            activityDashboardBinding.TotalRooms.text =
                getString(R.string.x_d, dashboardInfo.totalActiveRoom)
            activityDashboardBinding.TotalTenantsCurrent.text =
                getString(R.string.x_d, dashboardInfo.totalActiveTenant)
            activityDashboardBinding.TotalRentAmount.text =
                getString(R.string.x_d, dashboardInfo.totalCollectedRent)
        }

        viewModel.getRentDataByYearAndMonth().observe(this) { dashboardInfo ->
            dashboardInfo?.let {
                activityDashboardBinding.TotalRentAmountByYearAndMonth.text =
                    getString(R.string.x_taka, dashboardInfo.totalPaid.orZero())
                activityDashboardBinding.TotalDueAmountByYearAndMonth.text =
                    getString(R.string.x_taka, dashboardInfo.totalDue.orZero())
            }
        }

        viewModel.getRentDataByYear().observe(this) { yearlyRentData ->
            val monthNames = listOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )

            val data: MutableList<DataEntry> = ArrayList()
            for (month in 1..12) {
                val totalPaid = yearlyRentData[month] ?: 0
                data.add(ValueDataEntry(monthNames[month - 1], totalPaid))
            }

            // 🔥 Completely recreate AnyChartView
            val newChartView = AnyChartView(this)
            newChartView.layoutParams = activityDashboardBinding.monthlyRentView.layoutParams

            val cartesian: Cartesian = AnyChart.column()
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
            cartesian.title(getString(R.string.monthly_rent_collection_x, selectedYearForYearly))
            cartesian.yScale().minimum(0.0)
            cartesian.yAxis(0).labels().format("৳{%Value}{groupsSeparator: }")
            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            newChartView.setChart(cartesian)

            // 🌟 Replace the existing chart in the layout
            activityDashboardBinding.monthlyRentView.removeAllViews()
            activityDashboardBinding.monthlyRentView.addView(newChartView)
        }

    }

    private fun bindUiWithComponents() {
        spinnerAdapter.setSpinnerAdapter(
            activityDashboardBinding.FilterYear,
            this,
            spinnerData.setYearData()
        )
        // FOr yearly rent data only, Check if the first element is "Select Data" and remove it
        val yearList = spinnerData.setYearData()
        if (yearList.isNotEmpty() && yearList[0] == getString(R.string.select_data_1)) {
            yearList.removeAt(0)
        }

        spinnerAdapter.setSpinnerAdapter(
            activityDashboardBinding.yearId,
            this,
            yearList
        )
        setMonthSpinnerAdapter()

        activityDashboardBinding.yearId.onItemSelectedListener =
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
                        selectedYearForYearly =
                            parent.getItemAtPosition(position).toString().toInt()
                        viewModel.getRentDataByYear(selectedYearForYearly)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(Constants.TAG, "FilterYear Spinner : onNothingSelected")
                }
            }

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