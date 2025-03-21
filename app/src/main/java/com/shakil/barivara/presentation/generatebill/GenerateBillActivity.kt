package com.shakil.barivara.presentation.generatebill

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.data.model.generatebill.BillInfo
import com.shakil.barivara.databinding.ActivityGenerateBillBinding
import com.shakil.barivara.presentation.GenericBottomSheet
import com.shakil.barivara.presentation.adapter.RecyclerBillInfoAdapter
import com.shakil.barivara.presentation.generatebill.bottomsheet.MarkAsPaidBottomSheet
import com.shakil.barivara.presentation.generatebill.bottomsheet.NotifyUserBottomSheet
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.WHATS_APP_BUSINESS_ACCOUNT_NO
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class GenerateBillActivity : BaseActivity<ActivityGenerateBillBinding>(),
    RecyclerBillInfoAdapter.GenerateBillCallBacks, MarkAsPaidBottomSheet.MarkAsPaidListener,
    NotifyUserBottomSheet.NotifyUserListener {
    private lateinit var activityBinding: ActivityGenerateBillBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var year: Int = 0
    private var month: Int = 0
    private var validation = Validation(this, hashMap)
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()

    @Inject
    lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX
    private var markAsPaidBottomSheet = MarkAsPaidBottomSheet()
    private var notifyUserBottomSheet = NotifyUserBottomSheet()

    @Inject
    lateinit var prefManager: PrefManager
    private lateinit var recyclerBillInfoAdapter: RecyclerBillInfoAdapter

    private val viewModel by viewModels<GenerateBillViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_generate_bill

    override fun setVariables(dataBinding: ActivityGenerateBillBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewed(ScreenNameConstants.appSreenGenerateBill)
        init()
        binUIWithComponents()
        initObservers()
        setRecyclerAdapter()
    }

    private fun init() {
        ux = UX(this)
    }

    private fun initObservers() {
        viewModel.getBills().observe(this) { tenants ->
            recyclerBillInfoAdapter.setItems(tenants)
        }

        viewModel.getGenerateBillResponse().observe(this) { generateBill ->
            activityBinding.TotalDue.text = getString(R.string.x_d, generateBill.totalDue)
            activityBinding.TotalPaid.text = getString(R.string.x_d, generateBill.totalPaid)
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
                viewModel.generateBill(
                    year,
                    month
                )
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun binUIWithComponents() {
        if ((ContextCompat.checkSelfPermission(
                this@GenerateBillActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(
                this@GenerateBillActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@GenerateBillActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                Constants.REQUEST_CALL_CODE
            )
        }
        activityBinding.toolBar.setNavigationOnClickListener { finish() }

        validation.setSpinnerIsNotEmpty(arrayOf("YearId", "MonthId"))
        spinnerAdapter.setSpinnerAdapter(
            activityBinding.YearId,
            this,
            spinnerData.setYearData()
        )
        setMonthSpinnerAdapter()

        activityBinding.YearId.onItemSelectedListener =
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
                            viewModel.generateBill(
                                year,
                                month
                            )
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityBinding.MonthId.onItemSelectedListener =
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
                        viewModel.generateBill(
                            year,
                            month
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setRecyclerAdapter() {
        recyclerBillInfoAdapter = RecyclerBillInfoAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = recyclerBillInfoAdapter
        recyclerBillInfoAdapter.setGenerateBillCallBacks(this)
    }

    private fun setMonthSpinnerAdapter() {
        spinnerAdapter.setSpinnerAdapter(
            activityBinding.MonthId,
            this,
            spinnerData.setMonthData(year)
        )
    }

    override fun onNotify(billInfo: BillInfo) {
        showNotifyUserBottomSheet(billInfo)
    }

    override fun onMarkAsPaid(billInfo: BillInfo) {
        buttonAction(
            ButtonActionConstants.actionGenerateBillMarkAsPaid, mapOf(
                "user_mobile" to (billInfo.tenantPhone ?: ""),
                "message" to (billInfo.remarks ?: ""),
            )
        )
        showMarkAsPaidBottomSheet(billInfo)
    }

    private fun showNotifyUserBottomSheet(billInfo: BillInfo) {
        val billHistory = BillHistory(
            id = billInfo.id,
            tenantName = billInfo.tenant,
            room = billInfo.room,
            rent = billInfo.rent
        )
        screenViewed(ScreenNameConstants.appScreenGenerateBillMarkAsPaidBottomSheet)
        notifyUserBottomSheet = NotifyUserBottomSheet.newInstance(billHistory)
        notifyUserBottomSheet.show(supportFragmentManager, "MarkAsPaidBottomSheet")
        notifyUserBottomSheet.setNotifyUserListener(this)
    }


    private fun showMarkAsPaidBottomSheet(billInfo: BillInfo) {
        val billHistory = BillHistory(
            id = billInfo.id,
            tenantName = billInfo.tenant,
            room = billInfo.room,
            rent = billInfo.rent,
            month = billInfo.month,
            year = billInfo.year,
        )
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