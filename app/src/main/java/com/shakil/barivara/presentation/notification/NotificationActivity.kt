package com.shakil.barivara.presentation.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.R
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.presentation.adapter.NotificationRecyclerAdapter
import com.shakil.barivara.databinding.ActivityNotificationBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.data.model.notification.Notification
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class NotificationActivity : AppCompatActivity() {
    private lateinit var activityNotificationBinding: ActivityNotificationBinding
    private var notificationList: ArrayList<Notification> = arrayListOf()
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var ux : UX
    private var tools = Tools(this)
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNotificationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification)
        prefManager = PrefManager(this)
        setSupportActionBar(activityNotificationBinding.toolBar)
        activityNotificationBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@NotificationActivity,
                    MainActivity::class.java
                )
            )
        }
        initUI()
        bindUiWithComponents()
    }

    private fun initUI() {
        ux = UX(this)
    }

    private fun bindUiWithComponents() {
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData() {
        ux.getLoadingView()
        firebaseCrudHelper.fetchAllNotification(
            "notification",
            object : FirebaseCrudHelper.onNotificationDataFetch {
                override fun onFetch(objects: ArrayList<Notification?>?) {
                    notificationList = objects.orEmpty() as ArrayList<Notification>
                    if (notificationList.size <= 0) {
                        activityNotificationBinding.mNoDataMessage.visibility = View.VISIBLE
                    } else {
                        activityNotificationBinding.mNoDataMessage.visibility = View.GONE
                    }
                    setNotificationRecycler()
                    ux.removeLoadingView()
                }

            }
        )
    }

    private fun setNotificationRecycler() {
        if (notificationList.isNotEmpty()) {
            val iter = notificationList.iterator()
            while (iter.hasNext()) {
                val value = iter.next()
                if (value.title == null) iter.remove()
            }
        }
        val notificationRecyclerAdapter = NotificationRecyclerAdapter(notificationList)
        activityNotificationBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityNotificationBinding.mRecyclerView.adapter = notificationRecyclerAdapter
        notificationRecyclerAdapter.notifyDataSetChanged()
    }
}