package com.shakil.barivara.presentation.tenant

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.R
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.adapter.RecyclerTenantListAdapter
import com.shakil.barivara.adapter.RecyclerTenantListAdapter.TenantCallBacks
import com.shakil.barivara.databinding.ActivityTenantListBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.tenant.Tenant
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class TenantListActivity : AppCompatActivity(), TenantCallBacks {
    private lateinit var activityTenantListBinding: ActivityTenantListBinding
    private lateinit var tenantList: ArrayList<Tenant>
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var ux : UX
    private var tools = Tools(this)
    private var filterManager = FilterManager()
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        activityTenantListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_tenant_list)
        setSupportActionBar(activityTenantListBinding.toolBar)

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityTenantListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@TenantListActivity,
                    MainActivity::class.java
                )
            )
        }
        binUiWithComponents()
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
    }

    private fun binUiWithComponents() {
        customAdManager.generateAd(activityTenantListBinding.adView)
        activityTenantListBinding.searchLayout.SearchName.hint = getString(R.string.search_tenant_name)
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
        activityTenantListBinding.mAddTenantMaster.setOnClickListener {
            startActivity(
                Intent(
                    this@TenantListActivity,
                    NewTenantActivity::class.java
                )
            )
        }
        activityTenantListBinding.searchLayout.searchButton.setOnClickListener {
            if (tools.hasConnection()) {
                if (!TextUtils.isEmpty(activityTenantListBinding.searchLayout.SearchName.text.toString())) {
                    filterManager.onFilterClick(
                        activityTenantListBinding.searchLayout.SearchName.text.toString(),
                        tenantList,
                        object : FilterManager.onTenantFilterClick {
                            override fun onClick(objects: ArrayList<Tenant>) {
                                if (objects.size > 0) {
                                    tenantList = objects
                                    setRecyclerAdapter()
                                    Tools.hideKeyboard(this@TenantListActivity)
                                    Toast.makeText(
                                        this@TenantListActivity,
                                        getString(R.string.filterd),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Tools.hideKeyboard(this@TenantListActivity)
                                    activityTenantListBinding.mNoDataMessage.visibility =
                                        View.VISIBLE
                                    activityTenantListBinding.mNoDataMessage.setText(R.string.no_data_message)
                                    activityTenantListBinding.mRecylerView.visibility = View.GONE
                                    Toast.makeText(
                                        this@TenantListActivity,
                                        getString(R.string.no_data_message),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    )
                } else {
                    Toast.makeText(
                        this@TenantListActivity,
                        getString(R.string.enter_data_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@TenantListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        activityTenantListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityTenantListBinding.mRecylerView.visibility = View.VISIBLE
                activityTenantListBinding.searchLayout.SearchName.setText("")
                activityTenantListBinding.mNoDataMessage.visibility = View.GONE
                Tools.hideKeyboard(this@TenantListActivity)
                setData()
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
    }

    private fun setData() {
        ux.getLoadingView()
        firebaseCrudHelper.fetchAllTenant(
            "tenant",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onTenantDataFetch {
                override fun onFetch(objects: ArrayList<Tenant?>?) {
                    tenantList = objects.orEmpty() as ArrayList<Tenant>
                    if (tenantList.size <= 0) {
                        activityTenantListBinding.mNoDataMessage.visibility = View.VISIBLE
                        activityTenantListBinding.mNoDataMessage.setText(R.string.no_data_message)
                    }
                    setRecyclerAdapter()
                    ux.removeLoadingView()
                }
            })
    }

    private fun setRecyclerAdapter() {
        val recyclerTenantListAdapter = RecyclerTenantListAdapter(tenantList)
        activityTenantListBinding.mRecylerView.layoutManager = LinearLayoutManager(this)
        activityTenantListBinding.mRecylerView.adapter = recyclerTenantListAdapter
        recyclerTenantListAdapter.setOnTenantCallback(this)
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
            setData()
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

    override fun onDelete(tenant: Tenant) {
        doPopUpForDeleteConfirmation(tenant)
    }

    override fun onEdit(tenant: Tenant) {
        startActivity(
            Intent(
                this@TenantListActivity,
                NewTenantActivity::class.java
            ).putExtra("tenant", tenant)
        )
    }

    override fun onItemClick(tenant: Tenant) {
        startActivity(
            Intent(
                this@TenantListActivity,
                NewTenantActivity::class.java
            ).putExtra("tenant", tenant)
        )
    }
}
