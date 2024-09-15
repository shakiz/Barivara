package com.shakil.barivara.presentation.room

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
import com.shakil.barivara.data.model.room.Rent
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.databinding.ActivityRentListBinding
import com.shakil.barivara.presentation.adapter.RecyclerRentListAdapter
import com.shakil.barivara.presentation.adapter.RecyclerRentListAdapter.RentCallBacks
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class RentListActivity : BaseActivity<ActivityRentListBinding>(), RentCallBacks {
    private lateinit var activityRentListBinding: ActivityRentListBinding
    private var rentList: ArrayList<Rent> = arrayListOf()
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var ux: UX? = null
    private var tools = Tools(this)
    private var filterManager = FilterManager()
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(
                Intent(
                    this@RentListActivity,
                    MainActivity::class.java
                )
            )
        }
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_rent_list

    override fun setVariables(dataBinding: ActivityRentListBinding) {
        activityRentListBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityRentListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@RentListActivity,
                    MainActivity::class.java
                )
            )
        }
        init()
        bindUIWithComponents()
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
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
                        rentList,
                        object : FilterManager.onRentFilterClick {
                            override fun onClick(objects: ArrayList<Rent>) {
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

                        }
                    )
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
        firebaseCrudHelper.fetchAllRent(
            "rent",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onRentDataFetch {
                override fun onFetch(objects: ArrayList<Rent?>?) {
                    rentList = objects.orEmpty() as ArrayList<Rent>
                    if (rentList.size <= 0) {
                        activityRentListBinding.mNoDataMessage.visibility = View.VISIBLE
                        activityRentListBinding.mNoDataMessage.setText(R.string.no_data_message)
                    }
                    setRecyclerAdapter()
                    ux?.removeLoadingView()
                }
            })
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
            firebaseCrudHelper.deleteRecord(
                "rent",
                rent.fireBaseKey,
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