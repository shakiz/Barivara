package com.shakil.barivara.presentation.room

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.data.model.room.RoomStatus
import com.shakil.barivara.databinding.ActivityAddNewRoomBinding
import com.shakil.barivara.presentation.tenant.TenantListActivity
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class RoomActivity : BaseActivity<ActivityAddNewRoomBinding>() {
    private lateinit var activityAddNewRoomBinding: ActivityAddNewRoomBinding
    private var spinnerAdapter = SpinnerAdapter()
    private var roomNameStr: String = ""
    private var TenantId = 0
    private var NoOfRoom = 0
    private var NoOfBathroom = 0
    private var NoOfBalcony = 0
    private var ElectricMeterNo = ""
    private lateinit var selectedRoomStatus: RoomStatus
    private var room = Room()
    private var command = "add"
    private var tenantNames: ArrayList<String> = arrayListOf()
    private var tenantNameSpinnerAdapter: ArrayAdapter<String>? = null
    private var tools = Tools(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager
    private lateinit var ux: UX
    private val viewModel by viewModels<RoomViewModel>()
    override val layoutResourceId: Int
        get() = R.layout.activity_add_new_room

    override fun setVariables(dataBinding: ActivityAddNewRoomBinding) {
        activityAddNewRoomBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager = PrefManager(this)
        ux = UX(this)
        getIntentData()
        setSupportActionBar(activityAddNewRoomBinding.toolBar)
        activityAddNewRoomBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUIWithComponents()
        loadData()
        initListeners()
        initObservers()
        viewModel.getAllTenants(prefManager.getString(mAccessToken))
    }

    private fun initListeners() {
        activityAddNewRoomBinding.AddNoOfRoom.setOnClickListener {
            NoOfRoom++
            activityAddNewRoomBinding.NoOfRoom.text = "$NoOfRoom"
        }
        activityAddNewRoomBinding.DeductNoOfRoom.setOnClickListener {
            if (NoOfRoom > 0) {
                NoOfRoom--
                activityAddNewRoomBinding.NoOfRoom.text = "$NoOfRoom"
            }
        }
        activityAddNewRoomBinding.AddNoOfBalcony.setOnClickListener {
            NoOfBalcony++
            activityAddNewRoomBinding.NoOfBalcony.text = "$NoOfBalcony"
        }
        activityAddNewRoomBinding.DeductNoOfBalcony.setOnClickListener {
            if (NoOfBalcony > 0) {
                NoOfBalcony--
                activityAddNewRoomBinding.NoOfRoom.text = "$NoOfBalcony"
            }
        }
        activityAddNewRoomBinding.AddNoOfBathRoom.setOnClickListener {
            NoOfBathroom++
            activityAddNewRoomBinding.NoOfBathroom.text = "$NoOfBathroom"
        }
        activityAddNewRoomBinding.DeductNoOfBathroom.setOnClickListener {
            if (NoOfBathroom > 0) {
                NoOfBathroom--
                activityAddNewRoomBinding.NoOfBathroom.text = "$NoOfBathroom"
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

        activityAddNewRoomBinding.radioGroupStatus.setOnCheckedChangeListener { group, checkedId ->
            selectedRoomStatus = when (checkedId) {
                R.id.radioOccupied -> RoomStatus.Occupied
                R.id.radioVacant -> RoomStatus.Vacant
                R.id.radioAbandoned -> RoomStatus.Abandoned
                else -> RoomStatus.Unknown
            }
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }

        viewModel.getAddRoomResponse().observe(this) { response ->
            Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RoomActivity, RoomListActivity::class.java))
        }

        viewModel.getAddRoomErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getUpdateRoomResponse().observe(this) { response ->
            Toasty.success(applicationContext, response, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RoomActivity, TenantListActivity::class.java))
        }

        viewModel.getUpdateRoomErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getTenants().observe(this) { tenants ->
            spinnerAdapter.setSpinnerAdapter(
                activityAddNewRoomBinding.TenantNameId,
                this,
                ArrayList(tenants.map { it.name })
            )
        }
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            room =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("room", Room::class.java)!!
                } else {
                    intent.getParcelableExtra("room")!!
                }
        }
    }

    private fun loadData() {
        if (room.id != 0) {
            command = "update"
            activityAddNewRoomBinding.RoomName.setText(room.name)
            NoOfRoom = room.noOfRoom
            NoOfBathroom = room.noOfBathroom
            NoOfBalcony = room.noOfBalcony
            ElectricMeterNo = room.electricityMeterNo ?: ""
            activityAddNewRoomBinding.NoOfRoom.text = "$NoOfRoom"
            activityAddNewRoomBinding.NoOfBalcony.text = "$NoOfBalcony"
            activityAddNewRoomBinding.NoOfBathroom.text = "$NoOfBathroom"
            activityAddNewRoomBinding.ElectricityMeterNo.setText(ElectricMeterNo)
        }
    }

    private fun bindUIWithComponents() {
        customAdManager.generateAd(activityAddNewRoomBinding.adView)
        validation.setEditTextIsNotEmpty(
            arrayOf("RoomName"),
            arrayOf(getString(R.string.room_name_validation))
        )
        validation.setSpinnerIsNotEmpty(arrayOf("StartMonthId"))

        activityAddNewRoomBinding.TenantNameId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.getTenants().value?.let {
                        TenantId = it[position].id
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun saveOrUpdateData() {
        roomNameStr = activityAddNewRoomBinding.RoomName.text.toString()
        room.name = roomNameStr
        room.tenantId = TenantId
        room.noOfRoom = NoOfRoom
        room.noOfBathroom = NoOfBathroom
        room.noOfBalcony = NoOfBalcony
        room.status = selectedRoomStatus.statusToString()
        room.electricityMeterNo = activityAddNewRoomBinding.ElectricityMeterNo.text.toString()
        if (command == "add") {
            viewModel.addRoom(prefManager.getString(mAccessToken), room)
        } else {
            viewModel.updateRoom(prefManager.getString(mAccessToken), room.id, room)
        }
    }
}
