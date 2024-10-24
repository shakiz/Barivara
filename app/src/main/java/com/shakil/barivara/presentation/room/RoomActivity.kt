package com.shakil.barivara.presentation.room

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.data.model.room.RoomStatus
import com.shakil.barivara.databinding.ActivityAddNewRoomBinding
import com.shakil.barivara.utils.Constants.mAccessToken
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
    private var tenantId: Int = 0
    private var noOfRoom: Int = 0
    private var noOfBathroom: Int = 0
    private var noOfBalcony: Int = 0
    private var electricMeterNo: String = ""
    private lateinit var selectedRoomStatus: RoomStatus
    private var room = Room()
    private var command = "add"
    private var tools = Tools(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
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
        activityAddNewRoomBinding.addNoOfRoom.setOnClickListener {
            noOfRoom++
            activityAddNewRoomBinding.noOfRoom.text = "$noOfRoom"
        }
        activityAddNewRoomBinding.deductNoOfRoom.setOnClickListener {
            if (noOfRoom > 0) {
                noOfRoom--
                activityAddNewRoomBinding.noOfRoom.text = "$noOfRoom"
            }
        }
        activityAddNewRoomBinding.addNoOfBalcony.setOnClickListener {
            noOfBalcony++
            activityAddNewRoomBinding.noOfBalcony.text = "$noOfBalcony"
        }
        activityAddNewRoomBinding.deductNoOfBalcony.setOnClickListener {
            if (noOfBalcony > 0) {
                noOfBalcony--
                activityAddNewRoomBinding.noOfBalcony.text = "$noOfBalcony"
            }
        }
        activityAddNewRoomBinding.addNoOfBathRoom.setOnClickListener {
            noOfBathroom++
            activityAddNewRoomBinding.noOfBathroom.text = "$noOfBathroom"
        }
        activityAddNewRoomBinding.deductNoOfBathroom.setOnClickListener {
            if (noOfBathroom > 0) {
                noOfBathroom--
                activityAddNewRoomBinding.noOfBathroom.text = "$noOfBathroom"
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
            if (response.statusCode == 200) {
                Toasty.success(
                    applicationContext,
                    getString(R.string.added_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@RoomActivity, RoomListActivity::class.java))
            } else {
                Toasty.warning(applicationContext, response.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getAddRoomErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getUpdateRoomResponse().observe(this) { response ->
            if (response.statusCode == 200) {
                Toasty.success(
                    applicationContext,
                    getString(R.string.updated_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@RoomActivity, RoomListActivity::class.java))
            } else {
                Toasty.warning(applicationContext, response.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getUpdateRoomErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getTenants().observe(this) { tenants ->
            spinnerAdapter.setSpinnerAdapter(
                activityAddNewRoomBinding.tenantNameId,
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
            activityAddNewRoomBinding.roomName.setText(room.name)
            noOfRoom = room.noOfRoom
            noOfBathroom = room.noOfBathroom
            noOfBalcony = room.noOfBalcony
            electricMeterNo = room.electricityMeterNo ?: ""
            selectedRoomStatus =
                RoomStatus.from(room.status ?: RoomStatus.Unknown.value) ?: RoomStatus.Unknown
            setRoomStatusRadioSelection()
            activityAddNewRoomBinding.noOfRoom.text = "$noOfRoom"
            activityAddNewRoomBinding.noOfBalcony.text = "$noOfBalcony"
            activityAddNewRoomBinding.noOfBathroom.text = "$noOfBathroom"
            activityAddNewRoomBinding.electricityMeterNo.setText(electricMeterNo)
            activityAddNewRoomBinding.radioGroupStatus.isSelected = true
        }
    }

    private fun setRoomStatusRadioSelection() {
        when (selectedRoomStatus) {
            RoomStatus.Occupied -> activityAddNewRoomBinding.radioOccupied.isChecked = true
            RoomStatus.Vacant -> activityAddNewRoomBinding.radioVacant.isChecked = true
            RoomStatus.Abandoned -> activityAddNewRoomBinding.radioAbandoned.isChecked = true
            else -> activityAddNewRoomBinding.radioGroupStatus.clearCheck()
        }
    }

    private fun bindUIWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("roomName", "electricityMeterNo"),
            arrayOf(
                getString(R.string.room_name_validation),
                getString(R.string.meter_name_validation)
            )
        )
        validation.setSpinnerIsNotEmpty(arrayOf("tenantNameId"))

        activityAddNewRoomBinding.tenantNameId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.getTenants().value?.let {
                        tenantId = it[position].id
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun saveOrUpdateData() {
        roomNameStr = activityAddNewRoomBinding.roomName.text.toString()
        room.name = roomNameStr
        room.tenantId = tenantId
        room.noOfRoom = noOfRoom
        room.noOfBathroom = noOfBathroom
        room.noOfBalcony = noOfBalcony
        room.status = selectedRoomStatus.statusToString()
        room.electricityMeterNo = activityAddNewRoomBinding.electricityMeterNo.text.toString()
        if (command == "add") {
            viewModel.addRoom(prefManager.getString(mAccessToken), room)
        } else {
            viewModel.updateRoom(prefManager.getString(mAccessToken), room)
        }
    }
}
