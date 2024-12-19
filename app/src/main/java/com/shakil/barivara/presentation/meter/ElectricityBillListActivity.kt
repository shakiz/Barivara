package com.shakil.barivara.presentation.meter

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.meter.ElectricityBill
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.databinding.ActivityElectricityBillListBinding
import com.shakil.barivara.presentation.adapter.RecyclerElectricityBillListAdapter
import com.shakil.barivara.presentation.adapter.RecyclerElectricityBillListAdapter.ElectricityBillBacks
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ElectricityBillListActivity : BaseActivity<ActivityElectricityBillListBinding>(),
    ElectricityBillBacks {
    private lateinit var activityMeterCostListBinding: ActivityElectricityBillListBinding
    private var electricityBills: ArrayList<ElectricityBill> = arrayListOf()
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var ux: UX? = null
    private var tools = Tools(this)

    @Inject
    lateinit var prefManager: PrefManager

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(
                Intent(
                    this@ElectricityBillListActivity,
                    HomeActivity::class.java
                )
            )
        }
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_electricity_bill_list

    override fun setVariables(dataBinding: ActivityElectricityBillListBinding) {
        activityMeterCostListBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityMeterCostListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@ElectricityBillListActivity,
                    HomeActivity::class.java
                )
            )
        }
        init()
        bindUIWithComponents()
    }

    private fun init() {
        ux = UX(this)
    }

    private fun bindUIWithComponents() {
        activityMeterCostListBinding.searchLayout.SearchName.hint =
            getString(R.string.search_room_name)
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
        activityMeterCostListBinding.mAddBillMaster.setOnClickListener {
            startActivity(
                Intent(
                    this@ElectricityBillListActivity,
                    ElectricityBillDetailsActivity::class.java
                )
            )
        }
        activityMeterCostListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityMeterCostListBinding.mRecylerView.visibility = View.VISIBLE
                activityMeterCostListBinding.searchLayout.SearchName.setText("")
                activityMeterCostListBinding.mNoDataMessage.visibility = View.GONE
                Tools.hideKeyboard(this@ElectricityBillListActivity)
                setData()
                Toast.makeText(
                    this@ElectricityBillListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@ElectricityBillListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setData() {
        ux?.getLoadingView()
        firebaseCrudHelper.fetchAllElectricityBills(
            "electricityBill",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onElectricityBillDataFetch {
                override fun onFetch(objects: ArrayList<ElectricityBill?>?) {
                    electricityBills = objects.orEmpty() as ArrayList<ElectricityBill>
                    if (electricityBills.size <= 0) {
                        activityMeterCostListBinding.mNoDataMessage.visibility = View.VISIBLE
                        activityMeterCostListBinding.mNoDataMessage.setText(R.string.no_data_message)
                    }
                    setRecyclerAdapter()
                    ux?.removeLoadingView()
                }
            }
        )
    }

    private fun setRecyclerAdapter() {
        val recyclerBillListAdapter = RecyclerElectricityBillListAdapter(electricityBills)
        activityMeterCostListBinding.mRecylerView.layoutManager = LinearLayoutManager(this)
        activityMeterCostListBinding.mRecylerView.adapter = recyclerBillListAdapter
        recyclerBillListAdapter.setElectricityBillBack(this)
    }

    private fun doPopUpForDeleteConfirmation(electricityBill: ElectricityBill) {
        val dialog = Dialog(this@ElectricityBillListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        val cancel: Button = dialog.findViewById(R.id.cancelButton)
        val delete: Button = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            firebaseCrudHelper.deleteRecord(
                "electricityBill",
                electricityBill.fireBaseKey,
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

    override fun onDelete(electricityBill: ElectricityBill) {
        doPopUpForDeleteConfirmation(electricityBill)
    }

    override fun onItemClick(electricityBill: ElectricityBill) {
        startActivity(
            Intent(
                this@ElectricityBillListActivity,
                ElectricityBillDetailsActivity::class.java
            ).putExtra("electricityBill", electricityBill)
        )
    }
}