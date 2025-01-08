package com.shakil.barivara.presentation.generatebill

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityGeneratedBillHistoryBinding
import com.shakil.barivara.presentation.adapter.RecyclerBillHistoryAdapter
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeneratedBillHistoryActivity : BaseActivity<ActivityGeneratedBillHistoryBinding>() {
    private lateinit var activityBinding: ActivityGeneratedBillHistoryBinding
    private var tenantId: Int = 0
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var year: Int = 0

    @Inject
    lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX
    private val viewModel by viewModels<GenerateBillViewModel>()

    @Inject
    lateinit var prefManager: PrefManager
    private lateinit var recyclerBillHistoryAdapter: RecyclerBillHistoryAdapter

    override val layoutResourceId: Int
        get() = R.layout.activity_generated_bill_history

    override fun setVariables(dataBinding: ActivityGeneratedBillHistoryBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewed(ScreenNameConstants.appSreenGenerateBill)
        init()
        binUIWithComponents()
        initListeners()
        initObservers()
        setRecyclerAdapter()
        viewModel.getAllTenants()
        viewModel.getBillHistory(isForSearch = false, tenantId = 0, year = 0)
    }

    private fun init() {
        ux = UX(this)
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }

        viewModel.getTenants().observe(this) { tenants ->
            if (tenants.isEmpty()) {
                spinnerAdapter.setSpinnerAdapter(
                    activityBinding.searchLayout.tenantNameId,
                    this,
                    spinnerData.setSpinnerNoData()
                )
            } else {
                val tenantList = mutableListOf<String>()
                tenantList.add(0, getString(R.string.select_data_1))
                tenantList.addAll(ArrayList(tenants.map { it.name }))
                spinnerAdapter.setSpinnerAdapter(
                    activityBinding.searchLayout.tenantNameId,
                    this,
                    ArrayList(tenantList)
                )
            }
        }

        viewModel.getBillHistoryFilteredList().observe(this) { billHistory ->
            if (!billHistory.isNullOrEmpty()) {
                activityBinding.noDataLayout.root.visibility = View.GONE
                activityBinding.mRecyclerView.visibility = View.VISIBLE
                recyclerBillHistoryAdapter.setItems(billHistory)
            } else {
                activityBinding.noDataLayout.root.visibility = View.VISIBLE
                activityBinding.mRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun binUIWithComponents() {
        activityBinding.toolBar.setNavigationOnClickListener { finish() }

        spinnerAdapter.setSpinnerAdapter(
            activityBinding.searchLayout.filterYearSpinner,
            this,
            spinnerData.setYearData()
        )
    }

    private fun initListeners() {
        activityBinding.searchLayout.tenantNameId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!viewModel.getTenants().value.isNullOrEmpty()) {
                        viewModel.getTenants().value?.let {
                            //This is due to the first item is Select Data
                            if (position > 0) {
                                tenantId = it[position - 1].id
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityBinding.searchLayout.btnApplyFilter.setOnClickListener {
            viewModel.getBillHistory(
                isForSearch = true,
                tenantId = if (tenantId > 0) tenantId else null,
                year = year
            )
        }

        activityBinding.searchLayout.filterYearSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (parent.getItemAtPosition(position)
                            .toString() != getString(R.string.select_data)
                    ) {
                        year = parent.getItemAtPosition(position).toString().toInt()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityBinding.searchLayout.btnClearFilter.setOnClickListener {
            viewModel.getBillHistory(isForSearch = false, tenantId = 0, year = 0)
            activityBinding.searchLayout.filterYearSpinner.setSelection(0)
            activityBinding.searchLayout.tenantNameId.setSelection(0)
        }
    }

    private fun setRecyclerAdapter() {
        recyclerBillHistoryAdapter = RecyclerBillHistoryAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillHistoryAdapter
    }
}