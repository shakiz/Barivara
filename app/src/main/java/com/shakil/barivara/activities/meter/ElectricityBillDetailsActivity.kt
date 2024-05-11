package com.shakil.barivara.activities.meter

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityElectricityBillDetailsBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.meter.ElectricityBill
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import java.util.UUID

class ElectricityBillDetailsActivity : AppCompatActivity() {
    private lateinit var activityMeterCostDetailsBinding: ActivityElectricityBillDetailsBinding
    private var meterNameStr: String = ""
    private var roomNameStr: String = ""
    private var totalUnitInt = 0
    private var previousMonthUnitInt = 0
    private var presentMonthUnitInt = 0
    private var unitPriceInt = 0
    private var totalElectricityBillInt = 0
    private var utilsForAll = UtilsForAll(this)
    private var electricityBill: ElectricityBill = ElectricityBill()
    private var command = "add"
    private var AssociateMeterId = 0
    private var AssociateRoomId = 0
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var roomNames: ArrayList<String> = arrayListOf()
    private var meterNames: ArrayList<String> = arrayListOf()
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    private var spinnerData = SpinnerData(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMeterCostDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_electricity_bill_details)
        prefManager = PrefManager(this)
        getIntentData()
        setSupportActionBar(activityMeterCostDetailsBinding.toolBar)
        activityMeterCostDetailsBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUiWithComponents()
        loadData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("electricityBill") != null) {
                electricityBill = intent.getParcelableExtra("electricityBill")!!
            }
        }
    }

    private fun loadData() {
        if (electricityBill.billId != null) {
            command = "update"
            activityMeterCostDetailsBinding.RoomId.setSelection(electricityBill.roomId, true)
            activityMeterCostDetailsBinding.UnitPrice.setText("" + electricityBill.unitPrice)
            activityMeterCostDetailsBinding.PresentUnit.setText("" + electricityBill.presentUnit)
            activityMeterCostDetailsBinding.PastUnit.setText("" + electricityBill.pastUnit)
            activityMeterCostDetailsBinding.TotalUnit.text = "" + electricityBill.totalUnit
            activityMeterCostDetailsBinding.TotalAmount.text =
                "" + electricityBill.totalUnit * electricityBill.unitPrice
        }
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("PastUnit", "PresentUnit", "UnitPrice"), arrayOf(
                getString(R.string.past_unit_validation),
                getString(R.string.present_unit_validation),
                getString(R.string.unit_price_validation)
            )
        )
        activityMeterCostDetailsBinding.MeterId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    meterNameStr = parent.getItemAtPosition(position).toString()
                    AssociateMeterId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityMeterCostDetailsBinding.RoomId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    roomNameStr = parent.getItemAtPosition(position).toString()
                    AssociateRoomId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "meter",
                prefManager.getString(mUserId),
                "meterName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        meterNames = nameList.orEmpty() as ArrayList<String>
                        setMeterAdapter()
                    }
                }
            )
        } else {
            meterNames = spinnerData.setSpinnerNoData()
            setMeterAdapter()
        }
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "room",
                prefManager.getString(mUserId),
                "roomName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        roomNames = nameList.orEmpty() as ArrayList<String>
                        setRoomAdapter()
                    }
                }
            )
        } else {
            roomNames = spinnerData.setSpinnerNoData()
            setRoomAdapter()
        }
        activityMeterCostDetailsBinding.mAddMeterDetailsMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    electricityBill.meterId = AssociateMeterId
                    electricityBill.roomId = AssociateRoomId
                    electricityBill.meterName = meterNameStr
                    electricityBill.roomName = roomNameStr
                    electricityBill.unitPrice =
                        activityMeterCostDetailsBinding.UnitPrice.text.toString()
                            .trim { it <= ' ' }
                            .toDouble()
                    electricityBill.presentUnit =
                        activityMeterCostDetailsBinding.PresentUnit.text.toString()
                            .trim { it <= ' ' }
                            .toInt()
                    electricityBill.pastUnit =
                        activityMeterCostDetailsBinding.PastUnit.text.toString()
                            .trim { it <= ' ' }
                            .toInt()
                    electricityBill.totalUnit =
                        activityMeterCostDetailsBinding.TotalUnit.text.toString()
                            .trim { it <= ' ' }
                            .toDouble()
                    electricityBill.totalBill =
                        activityMeterCostDetailsBinding.TotalAmount.text.toString()
                            .trim { it <= ' ' }
                            .toDouble()
                    if (command == "add") {
                        electricityBill.billId = UUID.randomUUID().toString()
                        firebaseCrudHelper.add(
                            electricityBill,
                            "electricityBill",
                            prefManager.getString(mUserId)
                        )
                    } else {
                        firebaseCrudHelper.update(
                            electricityBill,
                            electricityBill.fireBaseKey,
                            "electricityBill",
                            prefManager.getString(mUserId)
                        )
                    }
                    Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            this@ElectricityBillDetailsActivity,
                            ElectricityBillListActivity::class.java
                        )
                    )
                } else {
                    Toast.makeText(
                        this@ElectricityBillDetailsActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        activityMeterCostDetailsBinding.PastUnit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                previousMonthUnitInt = utilsForAll.toIntValue(charSequence.toString())
                presentMonthUnitInt =
                    utilsForAll.toIntValue(activityMeterCostDetailsBinding.PresentUnit.text.toString())
                checkUnitLimit(previousMonthUnitInt, presentMonthUnitInt)
                unitPriceInt =
                    utilsForAll.toIntValue(activityMeterCostDetailsBinding.UnitPrice.text.toString())
                totalElectricityBillInt = unitPriceInt * totalUnitInt
                activityMeterCostDetailsBinding.TotalAmount.text =
                    totalElectricityBillInt.toString()
                Log.v(Constants.TAG, "previousMonthUnitInt : $previousMonthUnitInt")
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        activityMeterCostDetailsBinding.PresentUnit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                presentMonthUnitInt = utilsForAll.toIntValue(charSequence.toString())
                previousMonthUnitInt =
                    utilsForAll.toIntValue(activityMeterCostDetailsBinding.PastUnit.text.toString())
                checkUnitLimit(previousMonthUnitInt, presentMonthUnitInt)
                unitPriceInt =
                    utilsForAll.toIntValue(activityMeterCostDetailsBinding.UnitPrice.text.toString())
                totalElectricityBillInt = unitPriceInt * totalUnitInt
                activityMeterCostDetailsBinding.TotalAmount.text =
                    totalElectricityBillInt.toString()
                Log.v(Constants.TAG, "presentMonthUnitInt : $presentMonthUnitInt")
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        activityMeterCostDetailsBinding.UnitPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                unitPriceInt = utilsForAll.toIntValue(charSequence.toString())
                checkUnitLimit(previousMonthUnitInt, presentMonthUnitInt)
                totalElectricityBillInt = unitPriceInt * totalUnitInt
                activityMeterCostDetailsBinding.TotalAmount.text =
                    totalElectricityBillInt.toString()
                Log.v(Constants.TAG, "totalElectricityBillInt : $totalElectricityBillInt")
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun setMeterAdapter() {
        val meterNameSpinnerAdapter =
            ArrayAdapter(this@ElectricityBillDetailsActivity, R.layout.spinner_drop, meterNames)
        meterNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityMeterCostDetailsBinding.MeterId.adapter = meterNameSpinnerAdapter
    }

    private fun setRoomAdapter() {
        val roomNameSpinnerAdapter =
            ArrayAdapter(this@ElectricityBillDetailsActivity, R.layout.spinner_drop, roomNames)
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityMeterCostDetailsBinding.RoomId.adapter = roomNameSpinnerAdapter
    }

    private fun checkUnitLimit(previousMonthUnitValue: Int, presentMonthUnitValue: Int) {
        if (presentMonthUnitValue > previousMonthUnitValue) {
            calculateUnit(presentMonthUnitValue, previousMonthUnitValue)
        } else {
            Toast.makeText(applicationContext, R.string.warning_message_unit, Toast.LENGTH_SHORT)
                .show()
            totalUnitInt = 0
            activityMeterCostDetailsBinding.TotalUnit.text = totalUnitInt.toString()
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(resources.getColor(R.color.md_red_400))
        }
    }

    private fun calculateUnit(presentMonthUnitValue: Int, previousMonthUnitValue: Int) {
        if (presentMonthUnitValue > 0) {
            totalUnitInt = presentMonthUnitValue - previousMonthUnitValue
            activityMeterCostDetailsBinding.TotalUnit.text = totalUnitInt.toString()
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(resources.getColor(R.color.md_grey_800))
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.please_check_unit_values),
                Toast.LENGTH_SHORT
            ).show()
            totalUnitInt = 0
            activityMeterCostDetailsBinding.TotalUnit.text = totalUnitInt.toString()
            activityMeterCostDetailsBinding.TotalUnit.setTextColor(resources.getColor(R.color.md_red_400))
        }
    }
}
