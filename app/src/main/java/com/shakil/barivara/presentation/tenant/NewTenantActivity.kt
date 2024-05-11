package com.shakil.barivara.presentation.tenant

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityAddNewTenantBinding
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import java.util.UUID

class NewTenantActivity : AppCompatActivity() {
    private lateinit var activityAddNewTenantBinding: ActivityAddNewTenantBinding
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this);
    private var AssociateRoomId = 0
    private var StartingMonthId = 0
    private var TenantTypeId = 0
    private var tenantNameStr: String = ""
    private var gender: String = ""
    private var AssociateRoomStr: String = ""
    private var StartingMonthStr: String = ""
    private var TenantTypeStr: String = ""
    private var IsActiveValue: String = ""
    private var tenant = Tenant()
    private var command = "add"
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var roomNames: ArrayList<String> = arrayListOf()
    private lateinit var roomNameSpinnerAdapter: ArrayAdapter<String>
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var advancedAmountInt = 0
    private var isActiveId = 0
    private var utilsForAll = UtilsForAll(this)
    private var tools = Tools(this)
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddNewTenantBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_new_tenant)
        prefManager = PrefManager(this)
        intentData()
        setSupportActionBar(activityAddNewTenantBinding.toolBar)
        activityAddNewTenantBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUiWithComponents()
        loadData()
    }

    private fun intentData(){
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("tenant") != null) {
                tenant = intent.getParcelableExtra("tenant")!!
            }
        }
    }

    private fun loadData() {
        if (tenant?.tenantId != null) {
            command = "update"
            if (tenant?.gender != null && !TextUtils.isEmpty(tenant.gender)) {
                changeGender(tenant.gender.orEmpty())
            }
            activityAddNewTenantBinding.IsActiveLayout.visibility = View.VISIBLE
            activityAddNewTenantBinding.IsActive.check(tenant.isActiveId)
            activityAddNewTenantBinding.TenantName.setText(tenant.tenantName)
            activityAddNewTenantBinding.AssociateRoomId.visibility = View.GONE
            activityAddNewTenantBinding.StartingMonthId.setSelection(
                tenant.startingMonthId,
                true
            )
            activityAddNewTenantBinding.TenantTypeId.setSelection(tenant.tenantTypeId, true)
            activityAddNewTenantBinding.NID.setText(tenant.nID)
            activityAddNewTenantBinding.MobileNo.setText(tenant.mobileNo)
            activityAddNewTenantBinding.NumberOfPerson.setText("" + tenant.numberOfPerson)
            if (tenant.advancedAmount > 0) {
                activityAddNewTenantBinding.advanceAmountLayout.visibility = View.VISIBLE
                activityAddNewTenantBinding.AdvanceCheckBox.isChecked = true
                activityAddNewTenantBinding.AdvanceAmount.setText("" + tenant.advancedAmount)
                activityAddNewTenantBinding.AdvanceCheckBox.isEnabled = false
                activityAddNewTenantBinding.AdvanceAmount.isEnabled = false
            }
        }
    }

    private fun bindUiWithComponents() {
        customAdManager.generateAd(activityAddNewTenantBinding.adView)
        validation.setEditTextIsNotEmpty(
            arrayOf("TenantName", "NID", "MobileNo"), arrayOf(
                getString(R.string.tenant_amount_validation),
                getString(R.string.nid_number_hint),
                getString(R.string.mobile_number_hint)
            )
        )
        validation.setSpinnerIsNotEmpty(arrayOf("TenantTypeId", "StartingMonthId"))
        spinnerAdapter.setSpinnerAdapter(
            activityAddNewTenantBinding.StartingMonthId,
            this,
            spinnerData.setMonthData()
        )
        spinnerAdapter.setSpinnerAdapter(
            activityAddNewTenantBinding.TenantTypeId,
            this,
            spinnerData.setTenantTypeData()
        )
        activityAddNewTenantBinding.maleLayout.setOnClickListener {
            gender = getString(R.string.male)
            changeGender(getString(R.string.male))
        }
        activityAddNewTenantBinding.femaleLayout.setOnClickListener {
            gender = getString(R.string.female)
            changeGender(getString(R.string.female))
        }
        activityAddNewTenantBinding.AdvanceCheckBox.setOnCheckedChangeListener { compoundButton, visibilityValue ->
            if (visibilityValue) {
                activityAddNewTenantBinding.advanceAmountLayout.visibility = View.VISIBLE
                activityAddNewTenantBinding.AdvanceAmount.setText("")
            } else {
                activityAddNewTenantBinding.advanceAmountLayout.visibility = View.GONE
            }
        }
        if (tools.hasConnection()) {
            firebaseCrudHelper.getAllName(
                "room",
                prefManager.getString(mUserId),
                "roomName",
                object : FirebaseCrudHelper.onNameFetch {
                    override fun onFetched(nameList: ArrayList<String?>?) {
                        roomNames = nameList.orEmpty() as ArrayList<String>
                        setRoomSpinner()
                        if (tenant.tenantId != null) {
                            if (tenant.associateRoomId > 0) {
                                activityAddNewTenantBinding.AssociateRoomId.setSelection(
                                    tenant.associateRoomId,
                                    true
                                )
                            }
                        }
                    }
                }
            )
        } else {
            roomNames = spinnerData.setSpinnerNoData()
            setRoomSpinner()
        }
        activityAddNewTenantBinding.TenantTypeId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    TenantTypeStr = parent.getItemAtPosition(position).toString()
                    TenantTypeId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityAddNewTenantBinding.StartingMonthId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    StartingMonthStr = parent.getItemAtPosition(position).toString()
                    StartingMonthId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityAddNewTenantBinding.AssociateRoomId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    AssociateRoomStr = parent.getItemAtPosition(position).toString()
                    AssociateRoomId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        activityAddNewTenantBinding.IsActive.setOnCheckedChangeListener { group, checkedId ->
            var radioButton: RadioButton? = null
            try {
                radioButton = findViewById(checkedId)
            } catch (e: Exception) {
                Log.i(Constants.TAG, "Radio Button Error : " + e.message)
            }
            isActiveId = checkedId
            if (radioButton != null) {
                IsActiveValue = radioButton.tag.toString()
            }
        }
        activityAddNewTenantBinding.mSaveTenantMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    if (utilsForAll.isValidMobileNo(activityAddNewTenantBinding.MobileNo.text.toString())) {
                        if (activityAddNewTenantBinding.advanceAmountLayout.visibility == View.VISIBLE) {
                            if (!TextUtils.isEmpty(activityAddNewTenantBinding.AdvanceAmount.text.toString())) {
                                advancedAmountInt =
                                    activityAddNewTenantBinding.AdvanceAmount.text.toString()
                                        .toInt()
                                tenant.advancedAmount = advancedAmountInt
                                saveOrUpdateData()
                            } else {
                                Toast.makeText(
                                    this@NewTenantActivity,
                                    getString(R.string.advance_amount_validation),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            saveOrUpdateData()
                        }
                    } else {
                        Toast.makeText(
                            this@NewTenantActivity,
                            getString(R.string.validation_mobile_number_length),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@NewTenantActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setRoomSpinner() {
        roomNameSpinnerAdapter =
            ArrayAdapter(this@NewTenantActivity, R.layout.spinner_drop, roomNames)
        roomNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        activityAddNewTenantBinding.AssociateRoomId.adapter = roomNameSpinnerAdapter
    }

    private fun saveOrUpdateData() {
        tenantNameStr = activityAddNewTenantBinding.TenantName.text.toString()
        tenant.tenantName = tenantNameStr
        tenant.gender = gender
        tenant.nID = activityAddNewTenantBinding.NID.text.toString()
        tenant.mobileNo = activityAddNewTenantBinding.MobileNo.text.toString()
        tenant.numberOfPerson =
            utilsForAll.toIntValue(activityAddNewTenantBinding.NumberOfPerson.text.toString())
        tenant.tenantName = tenantNameStr
        tenant.associateRoom = AssociateRoomStr
        tenant.associateRoomId = AssociateRoomId
        tenant.tenantTypeStr = TenantTypeStr
        tenant.tenantTypeId = TenantTypeId
        tenant.startingMonthId = StartingMonthId
        tenant.startingMonth = StartingMonthStr
        tenant.isActiveId = isActiveId
        tenant.isActiveValue = IsActiveValue
        if (command == "add") {
            tenant.tenantId = UUID.randomUUID().toString()
            firebaseCrudHelper.add(tenant, "tenant", prefManager.getString(mUserId))
        } else {
            firebaseCrudHelper.update(
                tenant,
                tenant.fireBaseKey,
                "tenant",
                prefManager.getString(mUserId)
            )
        }
        Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@NewTenantActivity, TenantListActivity::class.java))
    }

    private fun changeGender(gender: String) {
        if (gender == getString(R.string.male)) {
            activityAddNewTenantBinding.maleLayout.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_background_filled_gender)
            activityAddNewTenantBinding.Male.setBackgroundResource(0)
            activityAddNewTenantBinding.Male.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                )
            )
            activityAddNewTenantBinding.femaleLayout.setBackgroundResource(0)
            activityAddNewTenantBinding.Female.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_green_600
                )
            )
            activityAddNewTenantBinding.MaleIcon.setImageResource(R.drawable.ic_male_white)
            activityAddNewTenantBinding.FemaleIcon.setImageResource(R.drawable.ic_female_green)
        } else {
            activityAddNewTenantBinding.femaleLayout.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_background_filled_gender)
            activityAddNewTenantBinding.Female.setBackgroundResource(0)
            activityAddNewTenantBinding.Female.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                )
            )
            activityAddNewTenantBinding.maleLayout.setBackgroundResource(0)
            activityAddNewTenantBinding.Male.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_green_600
                )
            )
            activityAddNewTenantBinding.MaleIcon.setImageResource(R.drawable.ic_male_green)
            activityAddNewTenantBinding.FemaleIcon.setImageResource(R.drawable.ic_female_white)
        }
    }
}
