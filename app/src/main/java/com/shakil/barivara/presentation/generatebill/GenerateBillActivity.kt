package com.shakil.barivara.presentation.generatebill

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.bill.GenerateBill
import com.shakil.barivara.data.model.generatebill.BillInfo
import com.shakil.barivara.databinding.ActivityGenerateBillBinding
import com.shakil.barivara.presentation.GenericBottomSheet
import com.shakil.barivara.presentation.adapter.RecyclerBillInfoAdapter
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.DroidFileManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class GenerateBillActivity : BaseActivity<ActivityGenerateBillBinding>(),
    RecyclerBillInfoAdapter.GenerateBillCallBacks {
    private lateinit var activityBinding: ActivityGenerateBillBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var year: Int = 0
    private var month: Int = 0
    private var validation = Validation(this, hashMap)
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private lateinit var utilsForAll: UtilsForAll
    private lateinit var dialogBill: Dialog
    private lateinit var ux: UX
    private lateinit var prefManager: PrefManager
    private lateinit var recyclerBillInfoAdapter: RecyclerBillInfoAdapter

    private val viewModel by viewModels<GenerateBillViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_generate_bill

    override fun setVariables(dataBinding: ActivityGenerateBillBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        screenViewed(ScreenNameConstants.appSreenGenerateBill)
        init()
        binUIWithComponents()
        initObservers()
        setRecyclerAdapter()
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
        utilsForAll = UtilsForAll(this)
    }

    private fun initObservers() {
        viewModel.getBills().observe(this) { tenants ->
            recyclerBillInfoAdapter.setItems(tenants)
        }

        viewModel.getGenerateBillResponse().observe(this) { generateBill ->
            activityBinding.TotalDue.text = "${generateBill.totalDue}"
            activityBinding.TotalPaid.text = "${generateBill.totalPaid}"
        }

        viewModel.getUpdateRentStatusResponse().observe(this) { rentStatusUpdate ->
            if (rentStatusUpdate.statusCode == 200) {
                Toasty.success(
                    this,
                    getString(R.string.rent_status_updated_successfully),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.generateBill(
                    prefManager.getString(mAccessToken),
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
            activityBinding.MonthId,
            this,
            spinnerData.setMonthData()
        )
        spinnerAdapter.setSpinnerAdapter(
            activityBinding.YearId,
            this,
            spinnerData.setYearData()
        )
        activityBinding.YearId.onItemSelectedListener =
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
        activityBinding.MonthId.onItemSelectedListener =
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
                        month = utilsForAll.getMonthFromMonthName(
                            parent.getItemAtPosition(position).toString()
                        )
                        viewModel.generateBill(
                            prefManager.getString(mAccessToken),
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

    private fun generatePdf(generateBill: GenerateBill) {
        ux?.getLoadingView()
        // create a new document
        val document = PdfDocument()
        // crate a page description
        val pageInfo = PageInfo.Builder(300, 600, 1).create()
        // start a page
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.BLACK
        val name = getString(R.string.tenant_name) + ": " + generateBill.tenantName
        val mobile = getString(R.string.mobile) + ": " + generateBill.mobileNo
        val monthAndYear = getString(R.string.month_and_year) + ": " + generateBill.monthAndYear
        val roomName = getString(R.string.room_name) + ": " + generateBill.associateRoom
        val rentAmount =
            getString(R.string.rent_amount) + ": " + generateBill.rentAmount + " " + getString(R.string.taka)
        val gasBill =
            getString(R.string.gas_bill) + ": " + generateBill.gasBill + " " + getString(R.string.taka)
        val waterBill =
            getString(R.string.water_bill) + ": " + generateBill.waterBill + " " + getString(R.string.taka)
        val electricityBill =
            getString(R.string.electricity_bill) + ": " + generateBill.electricityBill + " " + getString(
                R.string.taka
            )
        val serviceCharge =
            getString(R.string.service_charge) + ": " + generateBill.serviceCharge + " " + getString(
                R.string.taka
            )
        canvas.drawText(name, 40f, 50f, paint)
        canvas.drawText(mobile, 40f, 70f, paint)
        canvas.drawText(monthAndYear, 40f, 90f, paint)
        canvas.drawText(roomName, 40f, 110f, paint)
        canvas.drawText(rentAmount, 40f, 130f, paint)
        canvas.drawText(gasBill, 40f, 150f, paint)
        canvas.drawText(waterBill, 40f, 170f, paint)
        canvas.drawText(electricityBill, 40f, 190f, paint)
        canvas.drawText(serviceCharge, 40f, 2100f, paint)
        document.finishPage(page)
        val directoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/Vara-Adai/"
        val fileName =
            "Vara-Adai_" + generateBill.mobileNo + "-" + System.currentTimeMillis() + ".pdf"
        val targetPdf = directoryPath + fileName
        DroidFileManager().createFolder(directoryPath)
        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            ux.removeLoadingView()
            Toast.makeText(this, getString(R.string.file_save_in_downloads), Toast.LENGTH_LONG)
                .show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            ux.removeLoadingView()
            Toast.makeText(
                this,
                getString(R.string.please_try_again_something_went_wrong),
                Toast.LENGTH_LONG
            ).show()
        }
        document.close()
    }

    private fun doPopUpForBillDetails(generateBill: GenerateBill) {
        dialogBill = Dialog(this, android.R.style.Theme_Dialog)
        dialogBill.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBill.setContentView(R.layout.bill_confirmation_layout)
        dialogBill.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBill.setCanceledOnTouchOutside(true)
        val cancel: Button = dialogBill.findViewById(R.id.cancelButton)
        val sendMessage: Button = dialogBill.findViewById(R.id.sendMessage)
        val tenantName: TextView = dialogBill.findViewById(R.id.TenantName)
        val mobileNo: TextView = dialogBill.findViewById(R.id.MobileNo)
        val roomName: TextView = dialogBill.findViewById(R.id.RoomName)
        val monthAndYear: TextView = dialogBill.findViewById(R.id.MonthAndYear)
        val rentAmount: TextView = dialogBill.findViewById(R.id.RentAmount)
        val gasBill: TextView = dialogBill.findViewById(R.id.GasBill)
        val waterBill: TextView = dialogBill.findViewById(R.id.WaterBill)
        val serviceCharge: TextView = dialogBill.findViewById(R.id.ServiceCharge)
        val electricityBill: TextView = dialogBill.findViewById(R.id.ElectricityBill)
        tenantName.text = generateBill.tenantName
        mobileNo.text = generateBill.mobileNo
        monthAndYear.text = generateBill.monthAndYear
        roomName.text = generateBill.associateRoom
        rentAmount.text = "" + generateBill.rentAmount + " " + getString(R.string.taka)
        gasBill.text = "" + generateBill.gasBill + " " + getString(R.string.taka)
        waterBill.text = "" + generateBill.waterBill + " " + getString(R.string.taka)
        serviceCharge.text = "" + generateBill.serviceCharge + " " + getString(R.string.taka)
        electricityBill.text = "" + generateBill.electricityBill + " " + getString(R.string.taka)
        cancel.setOnClickListener { dialogBill.dismiss() }
        sendMessage.setOnClickListener {
            val message = """Name : ${generateBill.tenantName}
Mobile : ${generateBill.mobileNo}
Month and Year: ${generateBill.monthAndYear}
Room Name : ${generateBill.associateRoom}
Rent Amount : ${generateBill.rentAmount} ${getString(R.string.taka)}
Gas Bill : ${generateBill.gasBill} ${getString(R.string.taka)}
Water Bill : ${generateBill.waterBill} ${getString(R.string.taka)}
Electricity Bill : ${generateBill.electricityBill} ${getString(R.string.taka)}
Service Charge : ${generateBill.serviceCharge} ${getString(R.string.taka)}"""
            sendMessage(message, generateBill.mobileNo)
        }
        dialogBill.setCanceledOnTouchOutside(false)
        dialogBill.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialogBill.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialogBill.show()
    }

    private fun sendMessage(message: String, mobileNo: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        smsIntent.setType("text/plain")
        smsIntent.putExtra("sms_body", message)
        smsIntent.setData(Uri.parse("sms:$mobileNo"))
        startActivity(smsIntent)
        Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
        dialogBill.dismiss()
    }

    override fun onNotify(billInfo: BillInfo) {
        buttonAction(
            ButtonActionConstants.actionGenerateBillNotifyUser, mapOf(
                "user_mobile" to (billInfo.tenantPhone ?: ""),
                "message" to (billInfo.remarks ?: ""),
            )
        )
        sendMessage(billInfo.remarks ?: "", billInfo.tenantPhone ?: "")
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

    private fun showMarkAsPaidBottomSheet(billInfo: BillInfo) {
        val bottomSheet = GenericBottomSheet<View>(
            context = this,
            layoutResId = R.layout.dialog_layout_bill_mark_as_paid,
            onClose = {

            },
            onPrimaryAction = {
                viewModel.updateBillStatus(
                    prefManager.getString(mAccessToken),
                    billId = billInfo.id
                )
            },
            onSecondaryAction = {

            }
        )
        screenViewed(ScreenNameConstants.appScreenGenerateBillMarkAsPaidBottomSheet)
        bottomSheet.show()
    }
}