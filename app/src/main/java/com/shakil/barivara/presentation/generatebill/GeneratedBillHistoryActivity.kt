package com.shakil.barivara.presentation.generatebill

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.databinding.ActivityGeneratedBillHistoryBinding
import com.shakil.barivara.presentation.GenericBottomSheet
import com.shakil.barivara.presentation.adapter.RecyclerBillHistoryAdapter
import com.shakil.barivara.presentation.generatebill.bottomsheet.MarkAsPaidBottomSheet
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.orZero
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class GeneratedBillHistoryActivity : BaseActivity<ActivityGeneratedBillHistoryBinding>(),
    RecyclerBillHistoryAdapter.GenerateDBillHistoryCallBacks,
    MarkAsPaidBottomSheet.MarkAsPaidListener {
    private lateinit var activityBinding: ActivityGeneratedBillHistoryBinding
    private var tenantId: Int = 0
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var year: Int = 0

    @Inject
    lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX
    private val viewModel by viewModels<GenerateBillViewModel>()
    private val markAsPaidBottomSheet = MarkAsPaidBottomSheet()

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


        viewModel.getUpdateRentStatusResponse().observe(this) { rentStatusUpdate ->
            if (rentStatusUpdate.statusCode == 200) {
                Toasty.success(
                    this,
                    getString(R.string.rent_status_updated_successfully),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.getBillHistory(isForSearch = false, tenantId = 0, year = 0)
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
                            .toString() != getString(R.string.select_data_1)
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
        recyclerBillHistoryAdapter = RecyclerBillHistoryAdapter(utilsForAll)
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillHistoryAdapter
        recyclerBillHistoryAdapter.setGenerateBillCallBacks(this)
    }

    override fun onNotify(billHistory: BillHistory) {
        buttonAction(
            ButtonActionConstants.actionGenerateBillNotifyUser, mapOf(
                "user_mobile" to (billHistory.tenantPhone ?: ""),
                "message" to (billHistory.remarks ?: ""),
            )
        )
        sendMessage(billHistory.remarks ?: "", billHistory.tenantPhone ?: "")
    }


    private fun sendMessage(message: String, mobileNo: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        smsIntent.setType("text/plain")
        smsIntent.putExtra("sms_body", message)
        smsIntent.setData(Uri.parse("sms:$mobileNo"))
        startActivity(smsIntent)
        Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
    }

    override fun onMarkAsPaid(billHistory: BillHistory) {
        buttonAction(
            ButtonActionConstants.actionGenerateBillMarkAsPaid, mapOf(
                "user_mobile" to (billHistory.tenantPhone ?: ""),
                "message" to (billHistory.remarks ?: ""),
            )
        )
        showMarkAsPaidBottomSheet(billHistory)
    }

    private fun showMarkAsPaidBottomSheet(billHistory: BillHistory) {
        screenViewed(ScreenNameConstants.appScreenGenerateBillMarkAsPaidBottomSheet)
        markAsPaidBottomSheet.show(supportFragmentManager, "MarkAsPaidBottomSheet")
        markAsPaidBottomSheet.setMarkAsPaidListener(billHistory.id.orZero(), this)
    }

    override fun onMarkAsPaidSubmitted(remarks: String, billId: Int) {
        viewModel.updateBillStatus(
            billId = billId
        )
    }
}