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
import com.shakil.barivara.utils.DatePickerManager
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
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var year: Int = 0
    private var month: Int = 0
    private var tenantId: Int = 0
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()

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
        viewModel.getBillHistory()
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

        viewModel.getBillHistoryList().observe(this) { billHistory ->
            if (!billHistory.isNullOrEmpty()) {
                recyclerBillHistoryAdapter.setItems(billHistory)
            } else {

            }
        }
    }

    private fun binUIWithComponents() {
        activityBinding.toolBar.setNavigationOnClickListener { finish() }
    }

    private fun initListeners() {
        activityBinding.searchLayout.startingMonthId.setOnClickListener {
            DatePickerManager().showDatePicker(this, object : DatePickerManager.DatePickerCallback {
                override fun onDateSelected(date: String) {
                    activityBinding.searchLayout.startingMonthId.setText(date)
                }
            })
        }

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
                            tenantId = it[position].id
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setRecyclerAdapter() {
        recyclerBillHistoryAdapter = RecyclerBillHistoryAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillHistoryAdapter
    }
}