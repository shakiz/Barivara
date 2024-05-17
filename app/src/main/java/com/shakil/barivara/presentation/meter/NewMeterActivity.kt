package com.shakil.barivara.presentation.meter

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityNewMeterBinding
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.data.model.meter.Meter
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.Validation
import java.util.HashMap
import java.util.UUID

class NewMeterActivity : AppCompatActivity() {
    private lateinit var activityNewMeterBinding: ActivityNewMeterBinding
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var meterNameStr: String = ""
    private var roomNameStr: String = ""
    private var meterTypeStr: String = ""
    private var AssociateRoomId = 0
    private var MeterTypeId = 0
    private var meter = Meter()
    private var command = "add"
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var roomNames = arrayListOf<String>()
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewMeterBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_meter)
        prefManager = PrefManager(this)
        getIntentData()
        setSupportActionBar(activityNewMeterBinding.toolBar)
        activityNewMeterBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUIWithComponents()
        loadData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("meter") != null) {
                meter = intent.getParcelableExtra("meter")!!
            }
        }
    }

    private fun loadData() {
        if (meter.meterId != null) {
            command = "update"
            activityNewMeterBinding.MeterName.setText(meter.meterName)
            activityNewMeterBinding.MeterTypeId.setSelection(meter.meterTypeId, true)
        }
    }

    private fun bindUIWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("MeterName"),
            arrayOf(getString(R.string.meter_name_validation))
        )
        validation.setSpinnerIsNotEmpty(arrayOf("MeterTypeId"))
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "room",
                prefManager.getString(mUserId),
                "roomName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        roomNames = nameList.orEmpty() as ArrayList<String>
                        setRoomNameSpinner()
                    }
                }
            )
        } else {
            roomNames = spinnerData.setSpinnerNoData()
            setRoomNameSpinner()
        }
        spinnerAdapter.setSpinnerAdapter(
            activityNewMeterBinding.MeterTypeId,
            this,
            spinnerData.setMeterTypeData()
        )
        activityNewMeterBinding.AssociateRoomId.onItemSelectedListener =
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
        activityNewMeterBinding.MeterTypeId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    meterTypeStr = parent.getItemAtPosition(position).toString()
                    MeterTypeId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityNewMeterBinding.mSaveMeterMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    meterNameStr = activityNewMeterBinding.MeterName.text.toString()
                    meter.meterName = meterNameStr
                    meter.associateRoom = roomNameStr
                    meter.associateRoomId = AssociateRoomId
                    meter.meterTypeName = meterTypeStr
                    meter.meterTypeId = MeterTypeId
                    if (command == "add") {
                        meter.meterId = UUID.randomUUID().toString()
                        firebaseCrudHelper.add(meter, "meter", prefManager.getString(mUserId))
                    } else {
                        firebaseCrudHelper.update(
                            meter,
                            meter.fireBaseKey,
                            "meter",
                            prefManager.getString(mUserId)
                        )
                    }
                    Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@NewMeterActivity, MeterListActivity::class.java))
                } else {
                    Toast.makeText(
                        this@NewMeterActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setRoomNameSpinner() {
        val roomNameSpinnerAdapter =
            ArrayAdapter(this@NewMeterActivity, R.layout.spinner_drop, roomNames)
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityNewMeterBinding.AssociateRoomId.adapter = roomNameSpinnerAdapter
    }
}
