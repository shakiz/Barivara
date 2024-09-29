package com.shakil.barivara.presentation.tenant

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.databinding.ActivityTenantBinding
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.DatePickerManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.SpinnerAdapter
import com.shakil.barivara.utils.SpinnerData
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class TenantActivity : BaseActivity<ActivityTenantBinding>() {
    private lateinit var activityAddNewTenantBinding: ActivityTenantBinding
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var tenantTypeId: Int = 0
    private var tenantNameStr: String = ""
    private var tenant = Tenant()
    private var command = "add"
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var advancedAmountInt: Int = 0
    private var utilsForAll = UtilsForAll(this)
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    private lateinit var ux: UX

    private val viewModel by viewModels<TenantViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_tenant

    override fun setVariables(dataBinding: ActivityTenantBinding) {
        activityAddNewTenantBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager = PrefManager(this)
        ux = UX(this)
        intentData()
        setSupportActionBar(activityAddNewTenantBinding.toolBar)
        activityAddNewTenantBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUiWithComponents()
        initListeners()
        loadData()
        initObservers()
    }

    private fun intentData() {
        if (intent.extras != null) {
            tenant =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("tenant", Tenant::class.java)!!
                } else {
                    intent.getParcelableExtra("tenant")!!
                }
        }
    }

    private fun initListeners() {
        activityAddNewTenantBinding.startingMonthId.setOnClickListener {
            DatePickerManager().showDatePicker(this, object : DatePickerManager.DatePickerCallback {
                override fun onDateSelected(date: String) {
                    activityAddNewTenantBinding.startingMonthId.setText(date)
                }
            })
        }

        activityAddNewTenantBinding.endMonthId.setOnClickListener {
            DatePickerManager().showDatePicker(this, object : DatePickerManager.DatePickerCallback {
                override fun onDateSelected(date: String) {
                    activityAddNewTenantBinding.endMonthId.setText(date)
                }
            })
        }
    }

    private fun loadData() {
        if (tenant.id != 0) {
            command = "update"
            activityAddNewTenantBinding.TenantName.setText(tenant.name)
            activityAddNewTenantBinding.AssociateRoomId.visibility = View.GONE
            activityAddNewTenantBinding.startingMonthId.setText(tenant.startDate)
            activityAddNewTenantBinding.nid.setText(tenant.nidNumber)
            activityAddNewTenantBinding.mobileNo.setText(tenant.phone)
            if ((tenant.advancedAmount ?: 0) > 0) {
                activityAddNewTenantBinding.headingAdvanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.advanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.advanceCheckBox.isChecked = true
                activityAddNewTenantBinding.advanceAmount.setText("${tenant.advancedAmount}")
                activityAddNewTenantBinding.advanceCheckBox.isEnabled = false
                activityAddNewTenantBinding.advanceAmount.isEnabled = false
            }
        }
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("TenantName", "NID", "MobileNo"), arrayOf(
                getString(R.string.tenant_amount_validation),
                getString(R.string.nid_number_hint),
                getString(R.string.mobile_number_hint)
            )
        )
        validation.setSpinnerIsNotEmpty(arrayOf("TenantTypeId"))
        spinnerAdapter.setSpinnerAdapter(
            activityAddNewTenantBinding.tenantTypeId,
            this,
            spinnerData.setTenantTypeData()
        )
        activityAddNewTenantBinding.advanceCheckBox.setOnCheckedChangeListener { compoundButton, visibilityValue ->
            if (visibilityValue) {
                activityAddNewTenantBinding.headingAdvanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.advanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.advanceAmount.setText("")
            } else {
                activityAddNewTenantBinding.headingAdvanceAmount.visibility = View.GONE
                activityAddNewTenantBinding.advanceAmount.visibility = View.GONE
            }
        }
        activityAddNewTenantBinding.tenantTypeId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    tenantTypeId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityAddNewTenantBinding.mSaveTenantMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    if (utilsForAll.isValidMobileNo(activityAddNewTenantBinding.mobileNo.text.toString())) {
                        if (activityAddNewTenantBinding.headingAdvanceAmount.visibility == View.VISIBLE) {
                            if (!TextUtils.isEmpty(activityAddNewTenantBinding.advanceAmount.text.toString())) {
                                advancedAmountInt =
                                    activityAddNewTenantBinding.advanceAmount.text.toString()
                                        .toInt()
                                tenant.advancedAmount = advancedAmountInt
                                saveOrUpdateData()
                            } else {
                                Toast.makeText(
                                    this@TenantActivity,
                                    getString(R.string.advance_amount_validation),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            saveOrUpdateData()
                        }
                    } else {
                        Toast.makeText(
                            this@TenantActivity,
                            getString(R.string.validation_mobile_number_length),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@TenantActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

        viewModel.getAddTenantResponse().observe(this) { response ->
            Toasty.success(applicationContext, response, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@TenantActivity, TenantListActivity::class.java))
        }

        viewModel.getAddTenantErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getUpdateTenantResponse().observe(this) { response ->
            Toasty.success(applicationContext, response, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@TenantActivity, TenantListActivity::class.java))
        }

        viewModel.getUpdateTenantErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveOrUpdateData() {
        tenantNameStr = activityAddNewTenantBinding.TenantName.text.toString()
        tenant.name = tenantNameStr
        tenant.nidNumber = activityAddNewTenantBinding.nid.text.toString()
        tenant.phone = activityAddNewTenantBinding.mobileNo.text.toString()
        tenant.startDate = activityAddNewTenantBinding.startingMonthId.text.toString()
        if (command == "add") {
            viewModel.addTenant(prefManager.getString(mAccessToken), tenant)
        } else {
            viewModel.updateTenant(prefManager.getString(mAccessToken), tenant)
        }
    }
}