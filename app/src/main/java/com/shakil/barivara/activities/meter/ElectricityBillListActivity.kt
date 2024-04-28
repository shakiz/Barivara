package com.shakil.barivara.activities.meter

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.R
import com.shakil.barivara.adapter.RecyclerElectricityBillListAdapter
import com.shakil.barivara.adapter.RecyclerElectricityBillListAdapter.ElectricityBillBacks
import com.shakil.barivara.databinding.ActivityElectricityBillListBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.meter.ElectricityBill
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class ElectricityBillListActivity : AppCompatActivity(), ElectricityBillBacks {
    private lateinit var activityMeterCostListBinding: ActivityElectricityBillListBinding
    private var electricityBills: ArrayList<ElectricityBill>? = null
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var ux: UX? = null
    private var tools = Tools(this)
    private var filterManager = FilterManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMeterCostListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_electricity_bill_list)
        activityMeterCostListBinding.toolBar.setNavigationOnClickListener { onBackPressed() }
        init()
        bindUIWithComponents()
    }

    private fun init() {
        electricityBills = ArrayList()
        ux = UX(this)
        tools = Tools(this)
        filterManager = FilterManager()
        firebaseCrudHelper = FirebaseCrudHelper(this)
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
        activityMeterCostListBinding.searchLayout.searchButton.setOnClickListener {
            if (tools.hasConnection()) {
                if (!TextUtils.isEmpty(activityMeterCostListBinding.searchLayout.SearchName.text.toString())) {
                    filterManager.onFilterClick(
                        activityMeterCostListBinding.searchLayout.SearchName.text.toString(),
                        electricityBills
                    ) { objects ->
                        if (objects.size > 0) {
                            electricityBills = objects
                            setRecyclerAdapter()
                            Tools.hideKeyboard(this@ElectricityBillListActivity)
                            Toast.makeText(
                                this@ElectricityBillListActivity,
                                getString(R.string.filterd),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Tools.hideKeyboard(this@ElectricityBillListActivity)
                            activityMeterCostListBinding.mNoDataMessage.visibility = View.VISIBLE
                            activityMeterCostListBinding.mNoDataMessage.setText(R.string.no_data_message)
                            activityMeterCostListBinding.mRecylerView.visibility = View.GONE
                            Toast.makeText(
                                this@ElectricityBillListActivity,
                                getString(R.string.no_data_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@ElectricityBillListActivity,
                        getString(R.string.enter_data_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@ElectricityBillListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        firebaseCrudHelper.fetchAllElectricityBills("electricityBill") { objects ->
            electricityBills = objects
            if ((electricityBills?.size ?: 0) <= 0) {
                activityMeterCostListBinding.mNoDataMessage.visibility = View.VISIBLE
                activityMeterCostListBinding.mNoDataMessage.setText(R.string.no_data_message)
            }
            setRecyclerAdapter()
            ux?.removeLoadingView()
        }
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
            firebaseCrudHelper.deleteRecord("electricityBill", electricityBill.fireBaseKey)
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