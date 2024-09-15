package com.shakil.barivara.presentation.meter

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
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.meter.Meter
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.databinding.ActivityMeterListBinding
import com.shakil.barivara.presentation.adapter.RecyclerMeterListAdapter
import com.shakil.barivara.presentation.adapter.RecyclerMeterListAdapter.MeterCallBacks
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class MeterListActivity : BaseActivity<ActivityMeterListBinding>(), MeterCallBacks {
    private lateinit var activityMeterListBinding: ActivityMeterListBinding
    private var meterList: ArrayList<Meter> = arrayListOf()
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var ux: UX? = null
    private var tools = Tools(this)
    private var filterManager = FilterManager()
    private lateinit var prefManager: PrefManager

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(
                Intent(
                    this@MeterListActivity,
                    MainActivity::class.java
                )
            )
        }
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_meter_list

    override fun setVariables(dataBinding: ActivityMeterListBinding) {
        activityMeterListBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setSupportActionBar(activityMeterListBinding.toolBar)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityMeterListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@MeterListActivity,
                    MainActivity::class.java
                )
            )
        }
        binUiWIthComponents()
    }

    private fun init() {
        meterList = ArrayList()
        ux = UX(this)
        prefManager = PrefManager(this)
    }


    private fun binUiWIthComponents() {
        activityMeterListBinding.searchLayout.SearchName.hint =
            getString(R.string.search_meter_name)
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
        activityMeterListBinding.mAddMeterMaster.setOnClickListener {
            startActivity(
                Intent(
                    this@MeterListActivity,
                    NewMeterActivity::class.java
                )
            )
        }
        activityMeterListBinding.searchLayout.searchButton.setOnClickListener {
            if (tools.hasConnection()) {
                if (!TextUtils.isEmpty(activityMeterListBinding.searchLayout.SearchName.text.toString())) {
                    filterManager.onFilterClick(
                        activityMeterListBinding.searchLayout.SearchName.text.toString(),
                        meterList,
                        object : FilterManager.onMeterFilterClick {
                            override fun onClick(objects: ArrayList<Meter>) {
                                if (objects.size > 0) {
                                    meterList = objects
                                    setRecyclerAdapter()
                                    Tools.hideKeyboard(this@MeterListActivity)
                                    Toast.makeText(
                                        this@MeterListActivity,
                                        getString(R.string.filterd),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Tools.hideKeyboard(this@MeterListActivity)
                                    activityMeterListBinding.mNoDataMessage.visibility =
                                        View.VISIBLE
                                    activityMeterListBinding.mNoDataMessage.setText(R.string.no_data_message)
                                    activityMeterListBinding.mRecylerView.visibility = View.GONE
                                    Toast.makeText(
                                        this@MeterListActivity,
                                        getString(R.string.no_data_message),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    )
                } else {
                    Toast.makeText(
                        this@MeterListActivity,
                        getString(R.string.enter_data_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@MeterListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        activityMeterListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityMeterListBinding.mRecylerView.visibility = View.VISIBLE
                activityMeterListBinding.searchLayout.SearchName.setText("")
                activityMeterListBinding.mNoDataMessage.visibility = View.GONE
                Tools.hideKeyboard(this@MeterListActivity)
                setData()
                Toast.makeText(
                    this@MeterListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MeterListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setRecyclerAdapter() {
        val recyclerMeterListAdapter = RecyclerMeterListAdapter(meterList)
        activityMeterListBinding.mRecylerView.layoutManager = LinearLayoutManager(this)
        activityMeterListBinding.mRecylerView.adapter = recyclerMeterListAdapter
        recyclerMeterListAdapter.setMeterCallBack(this)
    }

    private fun setData() {
        ux?.getLoadingView()
        firebaseCrudHelper.fetchAllMeter(
            "meter",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onDataFetch {
                override fun onFetch(objects: ArrayList<Meter?>?) {
                    meterList = objects.orEmpty() as ArrayList<Meter>
                    setRecyclerAdapter()
                    ux?.removeLoadingView()
                    if (meterList.size <= 0) {
                        activityMeterListBinding.mNoDataMessage.visibility = View.VISIBLE
                        activityMeterListBinding.mNoDataMessage.setText(R.string.no_data_message)
                    }
                }
            })
    }

    private fun doPopUpForDeleteConfirmation(meter: Meter) {
        val dialog = Dialog(this@MeterListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        val cancel: Button = dialog.findViewById(R.id.cancelButton)
        val delete: Button = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            firebaseCrudHelper.deleteRecord(
                "meter",
                meter.fireBaseKey,
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

    override fun onDelete(meter: Meter) {
        doPopUpForDeleteConfirmation(meter)
    }

    override fun onEdit(meter: Meter) {
        startActivity(
            Intent(this@MeterListActivity, NewMeterActivity::class.java).putExtra(
                "meter",
                meter
            )
        )
    }

    override fun onItemClick(meter: Meter) {
        startActivity(
            Intent(this@MeterListActivity, NewMeterActivity::class.java).putExtra(
                "meter",
                meter
            )
        )
    }
}
