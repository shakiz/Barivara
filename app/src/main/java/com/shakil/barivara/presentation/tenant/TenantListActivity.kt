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
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.databinding.ActivityTenantListBinding
import com.shakil.barivara.presentation.adapter.RecyclerAdapterTenantList
import com.shakil.barivara.presentation.adapter.RecyclerAdapterTenantList.TenantCallBacks
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.filterList
import com.shakil.barivara.utils.textChanges
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TenantListActivity : BaseActivity<ActivityTenantListBinding>(), TenantCallBacks {
    private lateinit var activityTenantListBinding: ActivityTenantListBinding
    private lateinit var ux: UX
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    private lateinit var recyclerAdapterTenantList: RecyclerAdapterTenantList
    private val viewModel by viewModels<TenantViewModel>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            buttonAction(
                ButtonActionConstants.actionTenantListClose
            )
            startActivity(
                Intent(
                    this@TenantListActivity,
                    HomeActivity::class.java
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
        screenViewed(ScreenNameConstants.appSreenTenantList)
        setSupportActionBar(activityTenantListBinding.toolBar)
        activityTenantListBinding.searchLayout.SearchName.hint =
            getString(R.string.search_tenant_name)
        init()
        setRecyclerAdapter()
        initListeners()
        initObservers()
        viewModel.getAllTenants()
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityTenantListBinding.toolBar.setNavigationOnClickListener {
            buttonAction(
                ButtonActionConstants.actionTenantListClose
            )
            startActivity(
                Intent(
                    this@TenantListActivity,
                    HomeActivity::class.java
                )
            )
        }

        activityTenantListBinding.mAddTenantMaster.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionAddNewTenant
            )
            startActivity(
                Intent(
                    this@TenantListActivity,
                    TenantActivity::class.java
                )
            )
        }

        activityTenantListBinding.searchLayout.refreshButton.setOnClickListener {
            refreshListData()
        }
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
        setupDebouncedSearch()
    }

    private fun setupDebouncedSearch() {
        activityTenantListBinding.searchLayout.SearchName.textChanges()
            .debounce(100)
            .onEach { query ->
                val originalList = viewModel.getTenants().value ?: emptyList()
                // Use the common filter function
                val filteredList =
                    filterList(query.toString().lowercase(), originalList) { tenant, q ->
                        tenant.name.lowercase().contains(q, ignoreCase = true)
                    }

                if (filteredList.isNotEmpty()) {
                    activityTenantListBinding.mRecylerView.visibility = View.VISIBLE
                    activityTenantListBinding.noDataLayout.root.visibility =
                        View.GONE
                    recyclerAdapterTenantList.setItems(filteredList)
                    Tools.hideKeyboard(this@TenantListActivity)
                    Toast.makeText(
                        this@TenantListActivity,
                        getString(R.string.filterd),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Tools.hideKeyboard(this@TenantListActivity)
                    activityTenantListBinding.mRecylerView.visibility = View.GONE
                    activityTenantListBinding.noDataLayout.root.visibility =
                        View.VISIBLE
                    Toast.makeText(
                        this@TenantListActivity,
                        getString(R.string.no_data_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .launchIn(coroutineScope) // Launch in the Coroutine scope
    }


    private fun refreshListData() {
        if (tools.hasConnection()) {
            activityTenantListBinding.mRecylerView.visibility = View.VISIBLE
            activityTenantListBinding.searchLayout.SearchName.setText("")
            activityTenantListBinding.noDataLayout.root.visibility = View.GONE
            Tools.hideKeyboard(this@TenantListActivity)
            viewModel.getAllTenants()
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

    private fun initObservers() {
        viewModel.getTenants().observe(this) { tenants ->
            if (tenants.isEmpty()) {
                activityTenantListBinding.noDataLayout.root.visibility = View.VISIBLE
                activityTenantListBinding.mRecylerView.visibility = View.GONE
            } else {
                recyclerAdapterTenantList.setItems(tenants)
                activityTenantListBinding.mRecylerView.visibility = View.VISIBLE
                activityTenantListBinding.noDataLayout.root.visibility = View.GONE
            }
        }

        viewModel.getUpdateTenantResponse().observe(this) { updateResponse ->
            if (updateResponse.statusCode == 200) {
                viewModel.getAllTenants()
            } else {
                Toasty.warning(this, updateResponse.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getDeleteTenantResponse().observe(this) { deleteResponse ->
            if (deleteResponse.statusCode == 200) {
                viewModel.getAllTenants()
            } else {
                Toasty.warning(this, deleteResponse.message, Toast.LENGTH_SHORT).show()
            }
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
            viewModel.deleteTenant(tenant.id)
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
        buttonAction(
            ButtonActionConstants.actionTenantMakeCall
        )
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
        buttonAction(
            ButtonActionConstants.actionTenantSendMessage
        )
        Toast.makeText(this, getString(R.string.taking_into_message_section), Toast.LENGTH_SHORT)
            .show()
        Tools(this).sendMessage(mobileNo)
    }

    override fun onDelete(tenant: Tenant) {
        buttonAction(
            ButtonActionConstants.actionTenantDelete
        )
        doPopUpForDeleteConfirmation(tenant)
    }

    override fun onEdit(tenant: Tenant) {
        buttonAction(
            ButtonActionConstants.actionTenantUpdate
        )
        startActivity(
            Intent(
                this@TenantListActivity,
                TenantActivity::class.java
            ).putExtra("tenant", tenant)
        )
    }

    override fun onItemClick(tenant: Tenant) {
        buttonAction(
            ButtonActionConstants.actionTenantUpdate
        )
        startActivity(
            Intent(
                this@TenantListActivity,
                TenantActivity::class.java
            ).putExtra("tenant", tenant)
        )
    }
}
