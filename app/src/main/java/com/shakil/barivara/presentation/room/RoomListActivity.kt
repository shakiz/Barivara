package com.shakil.barivara.presentation.room

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
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.databinding.ActivityRoomListBinding
import com.shakil.barivara.presentation.adapter.RecyclerRoomListAdapter
import com.shakil.barivara.presentation.adapter.RecyclerRoomListAdapter.RoomCallBacks
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants.mAccessToken
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
class RoomListActivity : BaseActivity<ActivityRoomListBinding>(), RoomCallBacks {
    private lateinit var activityRoomListBinding: ActivityRoomListBinding
    private lateinit var ux: UX
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    private val viewModel by viewModels<RoomViewModel>()
    private lateinit var recyclerRoomListAdapter: RecyclerRoomListAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            buttonAction(
                ButtonActionConstants.actionRoomListClose
            )
            startActivity(
                Intent(
                    this@RoomListActivity,
                    HomeActivity::class.java
                )
            )
        }
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_room_list

    override fun setVariables(dataBinding: ActivityRoomListBinding) {
        activityRoomListBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewed(ScreenNameConstants.appSreenRoomList)
        setSupportActionBar(activityRoomListBinding.toolBar)
        activityRoomListBinding.searchLayout.SearchName.hint = getString(R.string.search_room_name)

        init()
        initListeners()
        initObservers()
        setRecyclerAdapter()
        viewModel.getAllRooms(prefManager.getString(mAccessToken))
    }

    private fun init() {
        ux = UX(this)
        prefManager = PrefManager(this)
        setupDebouncedSearch()
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        activityRoomListBinding.toolBar.setNavigationOnClickListener {
            buttonAction(
                ButtonActionConstants.actionRoomListClose
            )
            startActivity(
                Intent(
                    this@RoomListActivity,
                    HomeActivity::class.java
                )
            )
        }

        activityRoomListBinding.mAddRoomMaster.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionAddNewRoom
            )
            startActivity(
                Intent(
                    this@RoomListActivity,
                    RoomActivity::class.java
                )
            )
        }

        activityRoomListBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityRoomListBinding.mRecylerView.visibility = View.VISIBLE
                activityRoomListBinding.searchLayout.SearchName.setText("")
                activityRoomListBinding.noDataLayout.root.visibility = View.GONE
                Tools.hideKeyboard(this@RoomListActivity)
                viewModel.getAllRooms(prefManager.getString(mAccessToken))
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

    private fun setupDebouncedSearch() {
        activityRoomListBinding.searchLayout.SearchName.textChanges()
            .debounce(100)
            .onEach { query ->
                val originalList = viewModel.getRooms().value ?: emptyList()
                // Use the common filter function
                val filteredList =
                    filterList(query.toString().lowercase(), originalList) { room, q ->
                        room.name.lowercase().contains(q, ignoreCase = true)
                    }

                if (filteredList.isNotEmpty()) {
                    activityRoomListBinding.mRecylerView.visibility = View.VISIBLE
                    activityRoomListBinding.noDataLayout.root.visibility =
                        View.GONE
                    recyclerRoomListAdapter.setItems(filteredList)
                    Tools.hideKeyboard(this@RoomListActivity)
                    Toast.makeText(
                        this@RoomListActivity,
                        getString(R.string.filterd),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Tools.hideKeyboard(this@RoomListActivity)
                    activityRoomListBinding.mRecylerView.visibility = View.GONE
                    activityRoomListBinding.noDataLayout.root.visibility =
                        View.VISIBLE
                    Toast.makeText(
                        this@RoomListActivity,
                        getString(R.string.no_data_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .launchIn(coroutineScope) // Launch in the Coroutine scope
    }

    private fun initObservers() {
        viewModel.getRooms().observe(this) { rooms ->
            if (rooms.isEmpty()) {
                activityRoomListBinding.noDataLayout.root.visibility = View.VISIBLE
                activityRoomListBinding.mRecylerView.visibility = View.GONE
            } else {
                recyclerRoomListAdapter.setItems(rooms)
                activityRoomListBinding.mRecylerView.visibility = View.VISIBLE
                activityRoomListBinding.noDataLayout.root.visibility = View.GONE
            }
        }

        viewModel.getDeleteRoomResponse().observe(this) { deleteResponse ->
            if (deleteResponse.statusCode == 200) {
                viewModel.getAllRooms(prefManager.getString(mAccessToken))
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
        recyclerRoomListAdapter = RecyclerRoomListAdapter()
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
            viewModel.deleteRoom(prefManager.getString(mAccessToken), room.id)
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

    override fun onDelete(room: Room) {
        buttonAction(
            ButtonActionConstants.actionRoomDelete
        )
        doPopUpForDeleteConfirmation(room)
    }

    override fun onEdit(room: Room) {
        buttonAction(
            ButtonActionConstants.actionRoomUpdate
        )
        startActivity(
            Intent(this@RoomListActivity, RoomActivity::class.java).putExtra(
                "room",
                room
            )
        )
    }

    override fun onItemClick(room: Room) {
        buttonAction(
            ButtonActionConstants.actionRoomUpdate
        )
        startActivity(
            Intent(this@RoomListActivity, RoomActivity::class.java).putExtra(
                "room",
                room
            )
        )
    }
}
