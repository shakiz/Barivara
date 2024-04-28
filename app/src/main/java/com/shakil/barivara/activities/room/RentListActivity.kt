package com.shakil.barivara.activities.room

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
import com.shakil.barivara.adapter.RecyclerRentListAdapter
import com.shakil.barivara.adapter.RecyclerRentListAdapter.RentCallBacks
import com.shakil.barivara.databinding.ActivityRentListBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.room.Rent
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class RentListActivity : AppCompatActivity(), RentCallBacks {
    private lateinit var activityRentListBinding: ActivityRentListBinding
    private var rentList: ArrayList<Rent> = arrayListOf()
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var ux: UX? = null
    private var tools = Tools(this)
    private var filterManager = FilterManager()
    private var customAdManager = CustomAdManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRentListBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_list)
        activityRentListBinding.toolBar.setNavigationOnClickListener { onBackPressed() }
        init()
        bindUIWithComponents()
    }

    private fun init() {
        ux = UX(this)
    }

    private fun bindUIWithComponents() {
        activityRentListBinding.searchLayout.SearchName.hint = getString(R.string.search_month_name)
        customAdManager.generateAd(activityRentListBinding.adView)
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
        activityRentListBinding.mAddMaster.setOnClickListener {
            startActivity(
                Intent(
                    this@RentListActivity,
                    RentDetailsActivity::class.java
                )
            )
        }
        activityRentListBinding.searchLayout.searchButton.setOnClickListener {
            if (tools.hasConnection()) {
                if (!TextUtils.isEmpty(activityRentListBinding.searchLayout.SearchName.text.toString())) {
                    filterManager.onFilterClick(
                        activityRentListBinding.searchLayout.SearchName.text.toString(),
                        rentList
                    ) { objects ->
                        if (objects.size > 0) {
                            rentList = objects
                            setRecyclerAdapter()
                            Tools.hideKeyboard(this@RentListActivity)
                            Toast.makeText(
                                this@RentListActivity,
                                getString(R.string.filterd),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Tools.hideKeyboard(this@RentListActivity)
                            activityRentListBinding.mNoDataMessage.visibility = View.VISIBLE
                            activityRentListBinding.mNoDataMessage.setText(R.string.no_data_message)
                            activityRentListBinding.mRecylerView.visibility = View.GONE
                            Toast.makeText(
                                this@RentListActivity,
                                getString(R.string.no_data_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RentListActivity,
                        getString(R.string.enter_data_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@RentListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        activityRentListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityRentListBinding.mRecylerView.visibility = View.VISIBLE
                activityRentListBinding.searchLayout.SearchName.setText("")
                activityRentListBinding.mNoDataMessage.visibility = View.GONE
                Tools.hideKeyboard(this@RentListActivity)
                setData()
                Toast.makeText(
                    this@RentListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@RentListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setData() {
        ux?.getLoadingView()
        firebaseCrudHelper.fetchAllRent("rent") { objects ->
            rentList = objects
            if (rentList.size <= 0) {
                activityRentListBinding.mNoDataMessage.visibility = View.VISIBLE
                activityRentListBinding.mNoDataMessage.setText(R.string.no_data_message)
            }
            setRecyclerAdapter()
            ux?.removeLoadingView()
        }
    }

    private fun setRecyclerAdapter() {
        val recyclerMeterListAdapter = RecyclerRentListAdapter(rentList)
        activityRentListBinding.mRecylerView.layoutManager = LinearLayoutManager(this)
        activityRentListBinding.mRecylerView.adapter = recyclerMeterListAdapter
        recyclerMeterListAdapter.setRentCallBack(this)
    }

    private fun doPopUpForDeleteConfirmation(rent: Rent) {
        val dialog = Dialog(this@RentListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        val cancel: Button = dialog.findViewById(R.id.cancelButton)
        val delete: Button = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            firebaseCrudHelper.deleteRecord("rent", rent.fireBaseKey)
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

    override fun onDelete(rent: Rent) {
        doPopUpForDeleteConfirmation(rent)
    }

    override fun onEdit(rent: Rent) {
        startActivity(
            Intent(
                this@RentListActivity,
                RentDetailsActivity::class.java
            ).putExtra("rent", rent)
        )
    }

    override fun onItemClick(rent: Rent) {
        startActivity(
            Intent(
                this@RentListActivity,
                RentDetailsActivity::class.java
            ).putExtra("rent", rent)
        )
    }
}