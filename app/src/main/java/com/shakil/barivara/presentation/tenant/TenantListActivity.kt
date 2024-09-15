package com.shakil.barivara.presentation.tenant

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.tenant.NewTenant
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.databinding.ActivityTenantListBinding
import com.shakil.barivara.presentation.adapter.RecyclerAdapterTenantList
import com.shakil.barivara.presentation.adapter.RecyclerAdapterTenantList.TenantCallBacks
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TenantListActivity : BaseActivity<ActivityTenantListBinding>(), TenantCallBacks {
    private lateinit var activityTenantListBinding: ActivityTenantListBinding
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var ux: UX
    private var tools = Tools(this)
    private var filterManager = FilterManager()
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager
    private lateinit var recyclerAdapterTenantList: RecyclerAdapterTenantList
    private val viewModel by viewModels<TenantViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(
                Intent(
                    this@TenantListActivity,
                    MainActivity::class.java
                )
            )
        }
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_tenant_list

    override fun setVariables(dataBinding: ActivityTenantListBinding) {
        activityTenantListBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(activityTenantListBinding.toolBar)
        customAdManager.generateAd(activityTenantListBinding.adView)
        activityTenantListBinding.searchLayout.SearchName.hint =
            getString(R.string.search_tenant_name)
        init()
        setRecyclerAdapter()
        initListeners()
        initObservers()
        viewModel.getAllTenants(prefManager.getString(mAccessToken))
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityTenantListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@TenantListActivity,
                    MainActivity::class.java
                )
            )
        }

        activityTenantListBinding.mAddTenantMaster.setOnClickListener {
            startActivity(
                Intent(
                    this@TenantListActivity,
                    NewTenantActivity::class.java
                )
            )
        }

        activityTenantListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityTenantListBinding.mRecylerView.visibility = View.VISIBLE
                activityTenantListBinding.searchLayout.SearchName.setText("")
                activityTenantListBinding.mNoDataMessage.visibility = View.GONE
                Tools.hideKeyboard(this@TenantListActivity)
                viewModel.getAllTenants(prefManager.getString(mAccessToken))
                Toast.makeText(
                    this@TenantListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@TenantListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //        activityTenantListBinding.searchLayout.searchButton.setOnClickListener {
//            if (tools.hasConnection()) {
//                if (!TextUtils.isEmpty(activityTenantListBinding.searchLayout.SearchName.text.toString())) {
//                    filterManager.onFilterClick(
//                        activityTenantListBinding.searchLayout.SearchName.text.toString(),
//                        tenantList,
//                        object : FilterManager.onTenantFilterClick {
//                            override fun onClick(objects: ArrayList<Tenant>) {
//                                if (objects.size > 0) {
//                                    tenantList = objects
//                                    setRecyclerAdapter()
//                                    Tools.hideKeyboard(this@TenantListActivity)
//                                    Toast.makeText(
//                                        this@TenantListActivity,
//                                        getString(R.string.filterd),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    Tools.hideKeyboard(this@TenantListActivity)
//                                    activityTenantListBinding.mNoDataMessage.visibility =
//                                        View.VISIBLE
//                                    activityTenantListBinding.mNoDataMessage.setText(R.string.no_data_message)
//                                    activityTenantListBinding.mRecylerView.visibility = View.GONE
//                                    Toast.makeText(
//                                        this@TenantListActivity,
//                                        getString(R.string.no_data_message),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//
//                        }
//                    )
//                } else {
//                    Toast.makeText(
//                        this@TenantListActivity,
//                        getString(R.string.enter_data_validation),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } else {
//                Toast.makeText(
//                    this@TenantListActivity,
//                    getString(R.string.no_internet_title),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
    }

    private fun initObservers() {
        viewModel.getTenants().observe(this) { tenants ->
            recyclerAdapterTenantList.setItems(tenants)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun setRecyclerAdapter() {
        recyclerAdapterTenantList = RecyclerAdapterTenantList()
        activityTenantListBinding.mRecylerView.layoutManager = LinearLayoutManager(this)
        activityTenantListBinding.mRecylerView.adapter = recyclerAdapterTenantList
        recyclerAdapterTenantList.setOnTenantCallback(this)
    }

    private fun doPopUpForDeleteConfirmation(tenant: Tenant) {
        val cancel: Button
        val delete: Button
        val dialog = Dialog(this@TenantListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        cancel = dialog.findViewById(R.id.cancelButton)
        delete = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            firebaseCrudHelper.deleteRecord(
                "tenant",
                tenant.fireBaseKey,
                prefManager.getString(mUserId)
            )
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    override fun onCallClicked(mobileNo: String, tenantName: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val uri = "tel:$mobileNo"
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.setData(Uri.parse(uri))
            Toast.makeText(
                this,
                getString(R.string.calling) + " " + tenantName + "....",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(callIntent)
        } else {
            Toast.makeText(
                this,
                getString(R.string.please_allow_call_permission),
                Toast.LENGTH_SHORT
            ).show()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    Constants.REQUEST_CALL_CODE
                )
            }
        }
    }

    override fun onMessageClicked(mobileNo: String) {
        Toast.makeText(this, getString(R.string.taking_into_message_section), Toast.LENGTH_SHORT)
            .show()
        Tools(this).sendMessage(mobileNo)
    }

    override fun onDelete(tenant: NewTenant) {
        //doPopUpForDeleteConfirmation(tenant)
    }

    override fun onEdit(tenant: NewTenant) {
//        startActivity(
//            Intent(
//                this@TenantListActivity,
//                NewTenantActivity::class.java
//            ).putExtra("tenant", tenant)
//        )
    }

    override fun onItemClick(tenant: NewTenant) {
//        startActivity(
//            Intent(
//                this@TenantListActivity,
//                NewTenantActivity::class.java
//            ).putExtra("tenant", tenant)
//        )
    }
}
