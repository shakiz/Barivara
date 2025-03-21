package com.shakil.barivara.presentation.generatebill

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.databinding.ActivityGeneratedBillHistoryBinding
import com.shakil.barivara.databinding.AdvanceBillHistorySearchLayoutBinding
import com.shakil.barivara.presentation.adapter.RecyclerBillHistoryAdapter
import com.shakil.barivara.presentation.generatebill.bottomsheet.MarkAsPaidBottomSheet
import com.shakil.barivara.presentation.generatebill.bottomsheet.NotifyUserBottomSheet
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.orFalse
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class GeneratedBillHistoryActivity : BaseActivity<ActivityGeneratedBillHistoryBinding>(),
    RecyclerBillHistoryAdapter.GenerateDBillHistoryCallBacks,
    MarkAsPaidBottomSheet.MarkAsPaidListener, NotifyUserBottomSheet.NotifyUserListener {
    private lateinit var activityBinding: ActivityGeneratedBillHistoryBinding
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var tenantId: Int = 0
    private var rentStatus: String = ""
    private var year: Int = 0
    private var month: Int = 0

    @Inject
    lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX
    private val viewModel by viewModels<GenerateBillViewModel>()

    private var markAsPaidBottomSheet = MarkAsPaidBottomSheet()
    private var notifyUserBottomSheet = NotifyUserBottomSheet()

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
        viewModel.getBillHistory(
            isForSearch = false,
            year = 0,
            month = 0,
            tenantId = null,
            rentStatus = null
        )
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

        viewModel.getBillHistoryFilteredList().observe(this) { billHistory ->
            if (!billHistory.isNullOrEmpty()) {
                activityBinding.noDataLayout.root.visibility = View.GONE
                activityBinding.mRecyclerView.visibility = View.VISIBLE
                recyclerBillHistoryAdapter.setItems(billHistory)
                activityBinding.mRecyclerView.scrollToPosition(0)
            } else {
                activityBinding.noDataLayout.root.visibility = View.VISIBLE
                activityBinding.mRecyclerView.visibility = View.GONE
            }
        }

        viewModel.getUpdateRentStatusResponse().observe(this) { rentStatusUpdate ->
            if (rentStatusUpdate.statusCode == 200) {
                if (markAsPaidBottomSheet.isVisible) {
                    markAsPaidBottomSheet.dismiss()
                }
                Toasty.success(
                    this,
                    getString(R.string.rent_status_updated_successfully),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.getBillHistory(
                    isForSearch = false,
                    year = 0,
                    month = 0,
                    tenantId = null,
                    rentStatus = null
                )
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
        setMonthSpinnerAdapter()
    }

    private fun initListeners() {
        activityBinding.searchLayout.btnApplyFilter.setOnClickListener {
            if (year == 0) {
                Toasty.warning(
                    this,
                    getString(R.string.please_select_filter_item),
                    Toasty.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.getBillHistory(
                isForSearch = true,
                year = year,
                month = month,
                tenantId = null,
                rentStatus = null
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
                        setMonthSpinnerAdapter()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityBinding.searchLayout.monthId.onItemSelectedListener =
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
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityBinding.searchLayout.btnClearFilter.setOnClickListener {
            viewModel.getBillHistory(
                isForSearch = false,
                year = 0,
                month = 0,
                tenantId = null,
                rentStatus = null
            )
            activityBinding.searchLayout.filterYearSpinner.setSelection(0)
            activityBinding.searchLayout.monthId.setSelection(0)
        }

        activityBinding.searchLayout.ivAdvanceFilter.setOnClickListener {
            doPopupDialogForAdvanceFilter()
        }
    }

    private fun setRecyclerAdapter() {
        recyclerBillHistoryAdapter = RecyclerBillHistoryAdapter(utilsForAll)
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillHistoryAdapter
        recyclerBillHistoryAdapter.setGenerateBillCallBacks(this)
    }

    private fun setMonthSpinnerAdapter() {
        spinnerAdapter.setSpinnerAdapter(
            activityBinding.searchLayout.monthId,
            this,
            spinnerData.setMonthData(year)
        )
    }

    private fun doPopupDialogForAdvanceFilter() {
        val dialog = Dialog(this@GeneratedBillHistoryActivity, android.R.style.Theme_Dialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.advance_bill_history_search_layout)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCanceledOnTouchOutside(true)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val binding = AdvanceBillHistorySearchLayoutBinding.bind(dialog.findViewById(R.id.rootView))
        if (viewModel.getTenants().value?.isEmpty().orFalse()) {
            spinnerAdapter.setSpinnerAdapter(
                binding.tenantNameId,
                this,
                spinnerData.setSpinnerNoData()
            )
        } else {
            spinnerAdapter.setSpinnerAdapter(
                binding.tenantNameId,
                this,
                ArrayList(viewModel.getTenants().value.orEmpty().map { it.name })
            )
        }
        spinnerAdapter.setSpinnerAdapter(
            binding.rentStatusSpinner,
            this,
            spinnerData.setRentStatusSpinnerData()
        )

        binding.tenantNameId.onItemSelectedListener =
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

        binding.rentStatusSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    rentStatus = if (parent.getItemAtPosition(position)
                            .toString() == getString(R.string.paid)
                    ) {
                        "paid"
                    } else {
                        "due"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.btnApplyFilter.setOnClickListener {
            viewModel.getBillHistory(
                isForSearch = true,
                year = year,
                month = month,
                tenantId = tenantId,
                rentStatus = rentStatus
            )
            dialog.dismiss()
        }

        binding.btnCancelFilter.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onNotify(billHistory: BillHistory) {
        showNotifyUserBottomSheet(billHistory)
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

    private fun showNotifyUserBottomSheet(billHistory: BillHistory) {
        screenViewed(ScreenNameConstants.appScreenGenerateBillMarkAsPaidBottomSheet)
        notifyUserBottomSheet = NotifyUserBottomSheet.newInstance(billHistory)
        notifyUserBottomSheet.show(supportFragmentManager, "MarkAsPaidBottomSheet")
        notifyUserBottomSheet.setNotifyUserListener(this)
    }

    private fun showMarkAsPaidBottomSheet(billHistory: BillHistory) {
        screenViewed(ScreenNameConstants.appScreenGenerateBillMarkAsPaidBottomSheet)
        markAsPaidBottomSheet = MarkAsPaidBottomSheet.newInstance(billHistory)
        markAsPaidBottomSheet.show(supportFragmentManager, "MarkAsPaidBottomSheet")
        markAsPaidBottomSheet.setMarkAsPaidListener(this)
    }

    override fun onMarkAsPaidSubmitted(remarks: String, billId: Int) {
        viewModel.updateBillStatus(
            billId = billId,
            remarks = remarks
        )
    }


    override fun sentViaDirectMessage(billHistory: BillHistory?) {
        buttonAction(
            ButtonActionConstants.actionGenerateBillNotifyUser, mapOf(
                "user_mobile" to (billHistory?.tenantPhone ?: ""),
                "message" to (billHistory?.remarks ?: ""),
            )
        )
        val message = "${getString(R.string.tenant_name)}: ${billHistory?.tenantName}\n" +
                "${getString(R.string.room_name)}: ${billHistory?.room}\n" +
                "${getString(R.string.total_due_bill)}: ${billHistory?.rent}\n" +
                "${getString(R.string.rent_month)}: ${
                    getString(
                        R.string.d_comma_d,
                        billHistory?.month,
                        billHistory?.year
                    )
                }"
        sendMessage(message, billHistory?.tenantPhone ?: "")
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

    override fun sentViaWhatsapp(billHistory: BillHistory?) {
        val message = "${getString(R.string.tenant_name)}: ${billHistory?.tenantName}\n" +
                "${getString(R.string.room_name)}: ${billHistory?.room}\n" +
                "${getString(R.string.total_due_bill)}: ${billHistory?.rent}\n" +
                "${getString(R.string.rent_month)}: ${
                    getString(
                        R.string.d_comma_d,
                        billHistory?.month,
                        billHistory?.year
                    )
                }"
        val uri = Uri.parse(
            "https://wa.me/${
                billHistory?.tenantPhone?.replace(
                    "+",
                    ""
                )
            }?text=${Uri.encode(message)}"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.whatsapp")

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toasty.warning(this, getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT)
                .show()
        }
    }
}