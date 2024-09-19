package com.shakil.barivara.presentation.tenant

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.databinding.ActivityAddNewTenantBinding
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.CustomAdManager
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
class NewTenantActivity : BaseActivity<ActivityAddNewTenantBinding>() {
    private lateinit var activityAddNewTenantBinding: ActivityAddNewTenantBinding
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private var TenantTypeId = 0
    private var tenantNameStr: String = ""
    private var tenant = Tenant()
    private var command = "add"
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var advancedAmountInt = 0
    private var utilsForAll = UtilsForAll(this)
    private var tools = Tools(this)
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager
    private lateinit var ux: UX

    private val viewModel by viewModels<TenantViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_add_new_tenant

    override fun setVariables(dataBinding: ActivityAddNewTenantBinding) {
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
        loadData()
        initObservers()
    }

    private fun intentData() {
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("tenant") != null) {
                tenant = intent.getParcelableExtra("tenant")!!
            }
        }
    }

    private fun loadData() {
        if (tenant.id != 0 && tenant.id != null) {
            command = "update"
            activityAddNewTenantBinding.IsActiveLayout.visibility = View.VISIBLE
            activityAddNewTenantBinding.TenantName.setText(tenant.name)
            activityAddNewTenantBinding.AssociateRoomId.visibility = View.GONE
            activityAddNewTenantBinding.StartingMonthId.setText(tenant.startDate)
            activityAddNewTenantBinding.NID.setText(tenant.nidNumber)
            activityAddNewTenantBinding.MobileNo.setText(tenant.phone)
            if ((tenant.advancedAmount ?: 0) > 0) {
                activityAddNewTenantBinding.headingAdvanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.AdvanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.AdvanceCheckBox.isChecked = true
                activityAddNewTenantBinding.AdvanceAmount.setText("${tenant.advancedAmount}")
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
        validation.setSpinnerIsNotEmpty(arrayOf("TenantTypeId"))
        spinnerAdapter.setSpinnerAdapter(
            activityAddNewTenantBinding.TenantTypeId,
            this,
            spinnerData.setTenantTypeData()
        )
        activityAddNewTenantBinding.AdvanceCheckBox.setOnCheckedChangeListener { compoundButton, visibilityValue ->
            if (visibilityValue) {
                activityAddNewTenantBinding.headingAdvanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.AdvanceAmount.visibility = View.VISIBLE
                activityAddNewTenantBinding.AdvanceAmount.setText("")
            } else {
                activityAddNewTenantBinding.headingAdvanceAmount.visibility = View.GONE
                activityAddNewTenantBinding.AdvanceAmount.visibility = View.GONE
            }
        }
        activityAddNewTenantBinding.TenantTypeId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    TenantTypeId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityAddNewTenantBinding.mSaveTenantMaster.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    if (utilsForAll.isValidMobileNo(activityAddNewTenantBinding.MobileNo.text.toString())) {
                        if (activityAddNewTenantBinding.headingAdvanceAmount.visibility == View.VISIBLE) {
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
            startActivity(Intent(this@NewTenantActivity, TenantListActivity::class.java))
        }

        viewModel.getAddTenantErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getUpdateTenantResponse().observe(this) { response ->
            Toasty.success(applicationContext, response, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@NewTenantActivity, TenantListActivity::class.java))
        }

        viewModel.getUpdateTenantErrorResponse().observe(this) { errorResponse ->
            Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveOrUpdateData() {
        tenantNameStr = activityAddNewTenantBinding.TenantName.text.toString()
        tenant.name = tenantNameStr
        tenant.nidNumber = activityAddNewTenantBinding.NID.text.toString()
        tenant.phone = activityAddNewTenantBinding.MobileNo.text.toString()
        tenant.startDate = activityAddNewTenantBinding.StartingMonthId.text.toString()
        if (command == "add") {
            viewModel.addTenant(prefManager.getString(mAccessToken), tenant)
        } else {
            viewModel.updateTenant(prefManager.getString(mAccessToken), tenant)
        }
    }
}
