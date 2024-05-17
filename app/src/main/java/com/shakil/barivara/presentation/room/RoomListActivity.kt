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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.R
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.presentation.adapter.RecyclerRoomListAdapter
import com.shakil.barivara.presentation.adapter.RecyclerRoomListAdapter.RoomCallBacks
import com.shakil.barivara.databinding.ActivityRoomListBinding
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.FilterManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class RoomListActivity : AppCompatActivity(), RoomCallBacks {
    private lateinit var activityRoomListBinding: ActivityRoomListBinding
    private var roomList: ArrayList<Room> = arrayListOf()
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
                    this@RoomListActivity,
                    MainActivity::class.java
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRoomListBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_list)
        init()
        setSupportActionBar(activityRoomListBinding.toolBar)

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityRoomListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@RoomListActivity,
                    MainActivity::class.java
                )
            )
        }
        binUiWIthComponents()
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
    }

    private fun binUiWIthComponents() {
        customAdManager.generateAd(activityRoomListBinding.adView)
        activityRoomListBinding.searchLayout.SearchName.hint = getString(R.string.search_room_name)
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
        activityRoomListBinding.mAddRoomMaster.setOnClickListener {
            startActivity(
                Intent(
                    this@RoomListActivity,
                    RoomActivity::class.java
                )
            )
        }
        activityRoomListBinding.searchLayout.searchButton.setOnClickListener {
            if (tools.hasConnection()) {
                if (!TextUtils.isEmpty(activityRoomListBinding.searchLayout.SearchName.text.toString())) {
                    filterManager.onFilterClick(
                        activityRoomListBinding.searchLayout.SearchName.text.toString(),
                        roomList,
                        object : FilterManager.onFilterClick {
                            override fun onClick(objects: ArrayList<Room>) {
                                if (objects.size > 0) {
                                    roomList = objects
                                    setRecyclerAdapter()
                                    Tools.hideKeyboard(this@RoomListActivity)
                                    Toast.makeText(
                                        this@RoomListActivity,
                                        getString(R.string.filterd),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Tools.hideKeyboard(this@RoomListActivity)
                                    activityRoomListBinding.mNoDataMessage.visibility = View.VISIBLE
                                    activityRoomListBinding.mNoDataMessage.setText(R.string.no_data_message)
                                    activityRoomListBinding.mRecylerView.visibility = View.GONE
                                    Toast.makeText(
                                        this@RoomListActivity,
                                        getString(R.string.no_data_message),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                } else {
                    Toast.makeText(
                        this@RoomListActivity,
                        getString(R.string.enter_data_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@RoomListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        activityRoomListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityRoomListBinding.mRecylerView.visibility = View.VISIBLE
                activityRoomListBinding.searchLayout.SearchName.setText("")
                activityRoomListBinding.mNoDataMessage.visibility = View.GONE
                Tools.hideKeyboard(this@RoomListActivity)
                setData()
                Toast.makeText(
                    this@RoomListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@RoomListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setData() {
        ux?.getLoadingView()
        firebaseCrudHelper.fetchAllRoom(
            "room",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onRoomDataFetch {
                override fun onFetch(objects: ArrayList<Room?>?) {
                    roomList = objects.orEmpty() as ArrayList<Room>
                    if (roomList.size <= 0) {
                        activityRoomListBinding.mNoDataMessage.visibility = View.VISIBLE
                        activityRoomListBinding.mNoDataMessage.setText(R.string.no_data_message)
                    }
                    setRecyclerAdapter()
                    ux?.removeLoadingView()
                }
            })
    }

    private fun setRecyclerAdapter() {
        val recyclerRoomListAdapter = RecyclerRoomListAdapter(roomList)
        activityRoomListBinding.mRecylerView.layoutManager = LinearLayoutManager(this)
        activityRoomListBinding.mRecylerView.adapter = recyclerRoomListAdapter
        recyclerRoomListAdapter.setRoomCallBack(this)
    }

    private fun doPopUpForDeleteConfirmation(room: Room) {
        val dialog = Dialog(this@RoomListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        val cancel: Button = dialog.findViewById(R.id.cancelButton)
        val delete: Button = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            firebaseCrudHelper.deleteRecord(
                "room",
                room.fireBaseKey,
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

    override fun onDelete(room: Room) {
        doPopUpForDeleteConfirmation(room)
    }

    override fun onEdit(room: Room) {
        startActivity(
            Intent(this@RoomListActivity, RoomActivity::class.java).putExtra(
                "room",
                room
            )
        )
    }

    override fun onItemClick(room: Room) {
        startActivity(
            Intent(this@RoomListActivity, RoomActivity::class.java).putExtra(
                "room",
                room
            )
        )
    }
}
