package com.shakil.barivara.presentation.room

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
import com.shakil.barivara.databinding.ActivityAddNewRoomBinding
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.Validation
import java.util.HashMap
import java.util.UUID

class RoomActivity : AppCompatActivity() {
    private lateinit var activityAddNewRoomBinding: ActivityAddNewRoomBinding
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var roomNameStr: String = ""
    private var startMonthStr: String = ""
    private var associateMeterStr: String = ""
    private var tenantNameStr: String = ""
    private var StartMonthId = 0
    private var AssociateMeterId = 0
    private var TenantId = 0
    private var NoOfRoom = 0
    private var NoOfBathroom = 0
    private var NoOfBalcony = 0
    private var room: Room = Room()
    private var command = "add"
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var meterNames: ArrayList<String> = arrayListOf()
    private var tenantNames: ArrayList<String> = arrayListOf()
    private var meterNameSpinnerAdapter: ArrayAdapter<String>? = null
    private var tenantNameSpinnerAdapter: ArrayAdapter<String>? = null
    private var tools = Tools(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddNewRoomBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_new_room)
        prefManager = PrefManager(this)
        getIntentData()
        setSupportActionBar(activityAddNewRoomBinding.toolBar)
        activityAddNewRoomBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUIWithComponents()
        loadData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("room") != null) {
                room = intent.getParcelableExtra("room")!!
            }
        }
    }

    private fun loadData() {
        if (room.roomId != null) {
            command = "update"
            activityAddNewRoomBinding.RoomName.setText(room.roomName)
            NoOfRoom = room.noOfRoom
            NoOfBathroom = room.noOfBathroom
            NoOfBalcony = room.noOfBalcony
            activityAddNewRoomBinding.NoOfRoom.text = "" + NoOfRoom
            activityAddNewRoomBinding.NoOfBalcony.text = "" + NoOfBalcony
            activityAddNewRoomBinding.NoOfBathroom.text = "" + NoOfBathroom
            activityAddNewRoomBinding.StartMonthId.setSelection(room.startMonthId, true)
        }
    }

    private fun bindUIWithComponents() {
        customAdManager.generateAd(activityAddNewRoomBinding.adView)
        spinnerAdapter.setSpinnerAdapter(
            activityAddNewRoomBinding.StartMonthId,
            this,
            spinnerData.setMonthData()
        )
        validation.setEditTextIsNotEmpty(
            arrayOf("RoomName"),
            arrayOf(getString(R.string.room_name_validation))
        )
        validation.setSpinnerIsNotEmpty(arrayOf("StartMonthId"))
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "meter",
                prefManager.getString(mUserId),
                "meterName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        meterNames = nameList.orEmpty() as ArrayList<String>
                        setMeterSpinner()
                        if (room.roomId != null) {
                            activityAddNewRoomBinding.AssociateMeterId.setSelection(
                                room.associateMeterId,
                                true
                            )
                        }
                    }
                }
            )
        } else {
            meterNames = spinnerData.setSpinnerNoData()
            setMeterSpinner()
        }
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "tenant",
                prefManager.getString(mUserId),
                "tenantName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        tenantNames = nameList.orEmpty() as ArrayList<String>
                        setTenantSpinner()
                    }
                }
            )
        } else {
            tenantNames = spinnerData.setSpinnerNoData()
            setTenantSpinner()
        }
        activityAddNewRoomBinding.StartMonthId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    startMonthStr = parent.getItemAtPosition(position).toString()
                    StartMonthId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityAddNewRoomBinding.AssociateMeterId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    associateMeterStr = parent.getItemAtPosition(position).toString()
                    AssociateMeterId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityAddNewRoomBinding.TenantNameId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    tenantNameStr = parent.getItemAtPosition(position).toString()
                    TenantId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityAddNewRoomBinding.AddNoOfRoom.setOnClickListener {
            NoOfRoom++
            activityAddNewRoomBinding.NoOfRoom.text = "" + NoOfRoom
        }
        activityAddNewRoomBinding.DeductNoOfRoom.setOnClickListener {
            if (NoOfRoom > 0) {
                NoOfRoom--
                activityAddNewRoomBinding.NoOfRoom.text = "" + NoOfRoom
            }
        }
        activityAddNewRoomBinding.AddNoOfBalcony.setOnClickListener {
            NoOfBalcony++
            activityAddNewRoomBinding.NoOfBalcony.text = "" + NoOfBalcony
        }
        activityAddNewRoomBinding.DeductNoOfBalcony.setOnClickListener {
            if (NoOfBalcony > 0) {
                NoOfBalcony--
                activityAddNewRoomBinding.NoOfRoom.text = "" + NoOfBalcony
            }
        }
        activityAddNewRoomBinding.AddNoOfBathRoom.setOnClickListener {
            NoOfBathroom++
            activityAddNewRoomBinding.NoOfBathroom.text = "" + NoOfBathroom
        }
        activityAddNewRoomBinding.DeductNoOfBathroom.setOnClickListener {
            if (NoOfBathroom > 0) {
                NoOfBathroom--
                activityAddNewRoomBinding.NoOfBathroom.text = "" + NoOfBathroom
            }
        }
        activityAddNewRoomBinding.mSaveRoomMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    saveOrUpdateData()
                } else {
                    Toast.makeText(
                        this@RoomActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setTenantSpinner() {
        tenantNameSpinnerAdapter =
            ArrayAdapter(this@RoomActivity, R.layout.spinner_drop, tenantNames)
        tenantNameSpinnerAdapter?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityAddNewRoomBinding.TenantNameId.adapter = tenantNameSpinnerAdapter
    }

    private fun setMeterSpinner() {
        meterNameSpinnerAdapter = ArrayAdapter(this@RoomActivity, R.layout.spinner_drop, meterNames)
        meterNameSpinnerAdapter?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityAddNewRoomBinding.AssociateMeterId.adapter = meterNameSpinnerAdapter
    }

    private fun saveOrUpdateData() {
        roomNameStr = activityAddNewRoomBinding.RoomName.text.toString()
        room.roomName = roomNameStr
        room.startMonthName = startMonthStr
        room.startMonthId = StartMonthId
        room.associateMeterName = associateMeterStr
        room.associateMeterId = AssociateMeterId
        room.tenantName = tenantNameStr
        room.tenantNameId = TenantId
        room.noOfRoom = NoOfRoom
        room.noOfBathroom = NoOfBathroom
        room.noOfBalcony = NoOfBalcony
        if (command == "add") {
            room.roomId = UUID.randomUUID().toString()
            firebaseCrudHelper.add(room, "room", prefManager.getString(mUserId))
        } else {
            firebaseCrudHelper.update(
                room,
                room.fireBaseKey,
                "room",
                prefManager.getString(mUserId)
            )
        }
        Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@RoomActivity, RoomListActivity::class.java))
    }
}
