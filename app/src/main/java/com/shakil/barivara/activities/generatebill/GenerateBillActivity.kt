package com.shakil.barivara.activities.generatebill

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityGenerateBillBinding
import com.shakil.barivara.model.bill.GenerateBill
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.DroidFileManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GenerateBillActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityGenerateBillBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var YearStr: String? = null
    private var MonthStr: String? = null
    private var validation = Validation(this, hashMap)
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var tools = Tools(this)
    private lateinit var dialogBill: Dialog
    private var customAdManager = CustomAdManager(this)
    private var ux: UX? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_generate_bill)
        init()
        binUIWithComponents()
    }

    private fun init() {
        ux = UX(this)
    }

    private fun binUIWithComponents() {
        customAdManager.generateAd(activityBinding.adView)
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
        validation.setEditTextIsNotEmpty(
            arrayOf("AssociateRoom", "TenantName", "MobileNo", "RentAmount"), arrayOf(
                getString(R.string.room_name_validation),
                getString(R.string.tenant_name_validation),
                getString(R.string.rent_amount_validation),
                getString(R.string.mobile_validation)
            )
        )
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
                    YearStr = parent.getItemAtPosition(position).toString()
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
                    MonthStr = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityBinding.generateBill.setOnClickListener {
            if (validation.isValid) {
                if (tools.isValidMobile(activityBinding.MobileNo.text.toString())) {
                    try {
                        val generateBill = GenerateBill(
                            activityBinding.TenantName.text.toString(),
                            activityBinding.MobileNo.text.toString(),
                            "$MonthStr $YearStr",
                            activityBinding.AssociateRoom.text.toString(),
                            activityBinding.RentAmount.text.toString().toInt(),
                            activityBinding.GasBill.text.toString().toInt(),
                            activityBinding.WaterBill.text.toString().toInt(),
                            activityBinding.ElectricityBill.text.toString().toInt(),
                            activityBinding.ServiceCharge.text.toString().toInt()
                        )
                        doPopUpForBillDetails(generateBill)
                    } catch (e: Exception) {
                        Log.e(
                            "onClickError: ",
                            e.message ?: " activityBinding.generateBill.setOnClickListener"
                        )
                    }
                } else {
                    Toast.makeText(
                        this@GenerateBillActivity,
                        getString(R.string.mobile_number_not_valid), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        activityBinding.generatePdf.setOnClickListener {
            if (validation.isValid) {
                if (tools.isValidMobile(activityBinding.MobileNo.text.toString())) {
                    val generateBill = GenerateBill(
                        activityBinding.TenantName.text.toString(),
                        activityBinding.MobileNo.text.toString(),
                        "$MonthStr $YearStr",
                        activityBinding.AssociateRoom.text.toString(),
                        activityBinding.RentAmount.text.toString().toInt(),
                        activityBinding.GasBill.text.toString().toInt(),
                        activityBinding.WaterBill.text.toString().toInt(),
                        activityBinding.ElectricityBill.text.toString().toInt(),
                        activityBinding.ServiceCharge.text.toString().toInt()
                    )
                    generatePdf(generateBill)
                }
            }
        }
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
            ux?.removeLoadingView()
            Toast.makeText(this, getString(R.string.file_save_in_downloads), Toast.LENGTH_LONG)
                .show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            ux?.removeLoadingView()
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
}