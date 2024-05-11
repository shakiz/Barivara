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
import com.shakil.barivara.databinding.ActivityNewRentDetailsBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.room.Rent
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.Validation
import java.util.UUID

class RentDetailsActivity : AppCompatActivity() {
    private lateinit var activityNewRentDetailsBinding: ActivityNewRentDetailsBinding
    private var spinnerData = SpinnerData(this)
    private var spinnerAdapter = SpinnerAdapter()
    private var rent: Rent = Rent()
    private var command = "add"
    private var MonthId = 0
    private var YearId = 0
    private var AssociateRoomId = 0
    private var MonthStr = ""
    private var YearName = ""
    private var AssociateRoomStr = ""
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var roomNames: ArrayList<String> = arrayListOf()
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewRentDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_new_rent_details)
        prefManager = PrefManager(this)
        getIntentData()
        setSupportActionBar(activityNewRentDetailsBinding.toolBar)
        activityNewRentDetailsBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUiWithComponents()
        loadData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("rent") != null) {
                rent = intent.getParcelableExtra("rent")!!
            }
        }
    }

    private fun loadData() {
        if (rent.rentId != null) {
            command = "update"
            activityNewRentDetailsBinding.RentAmount.setText(rent.rentAmount.toString())
            activityNewRentDetailsBinding.MonthId.setSelection(rent.monthId, true)
            activityNewRentDetailsBinding.YearId.setSelection(rent.yearId, true)
            activityNewRentDetailsBinding.Note.setText(rent.note)
        }
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("RentAmount"),
            arrayOf(getString(R.string.rent_amount_validation))
        )
        validation.setSpinnerIsNotEmpty(arrayOf("YearId", "MonthId"))
        spinnerAdapter.setSpinnerAdapter(
            activityNewRentDetailsBinding.MonthId,
            this,
            spinnerData.setMonthData()
        )
        spinnerAdapter.setSpinnerAdapter(
            activityNewRentDetailsBinding.YearId,
            this,
            spinnerData.setYearData()
        )
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "room",
                prefManager.getString(mUserId),
                "roomName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        roomNames = nameList.orEmpty() as ArrayList<String>
                        setRoomSpinner()
                    }
                }
            )
        } else {
            roomNames = spinnerData.setSpinnerNoData()
            setRoomSpinner()
        }
        activityNewRentDetailsBinding.MonthId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    MonthId = position
                    MonthStr = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityNewRentDetailsBinding.YearId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    YearId = position
                    YearName = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityNewRentDetailsBinding.AssociateRoomId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    AssociateRoomId = position
                    AssociateRoomStr = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityNewRentDetailsBinding.mAddRentMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    rent.monthId = MonthId
                    rent.monthName = MonthStr
                    rent.yearId = YearId
                    rent.yearName = YearName
                    rent.associateRoomId = AssociateRoomId
                    rent.associateRoomName = AssociateRoomStr
                    rent.note = activityNewRentDetailsBinding.Note.text.toString()
                    rent.rentAmount =
                        activityNewRentDetailsBinding.RentAmount.text.toString().toInt()
                    if (command == "add") {
                        rent.rentId = UUID.randomUUID().toString()
                        firebaseCrudHelper.add(rent, "rent", prefManager.getString(mUserId))
                    } else {
                        firebaseCrudHelper.update(
                            rent,
                            rent.fireBaseKey,
                            "rent",
                            prefManager.getString(mUserId)
                        )
                    }
                    Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RentDetailsActivity, RentListActivity::class.java))
                } else {
                    Toast.makeText(
                        this@RentDetailsActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setRoomSpinner() {
        val roomNameSpinnerAdapter =
            ArrayAdapter(this@RentDetailsActivity, R.layout.spinner_drop, roomNames)
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityNewRentDetailsBinding.AssociateRoomId.adapter = roomNameSpinnerAdapter
    }
}
