package com.shakil.barivara.activities.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.R
import com.shakil.barivara.activities.onboard.MainActivity
import com.shakil.barivara.adapter.NotificationRecyclerAdapter
import com.shakil.barivara.databinding.ActivityNotificationBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.notification.Notification
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class NotificationActivity : AppCompatActivity() {
    private lateinit var activityNotificationBinding: ActivityNotificationBinding
    private var notificationList: ArrayList<Notification>? = null
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var ux : UX
    private var tools = Tools(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNotificationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification)
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
        firebaseCrudHelper.fetchAllNotification("notification") { objects ->
            notificationList = objects
            if ((notificationList?.size ?: 0) <= 0) {
                activityNotificationBinding.mNoDataMessage.visibility = View.VISIBLE
            } else {
                activityNotificationBinding.mNoDataMessage.visibility = View.GONE
            }
            setNotificationRecycler()
            ux.removeLoadingView()
        }
    }

    private fun setNotificationRecycler() {
        if (notificationList != null) {
            val iter = notificationList?.iterator()
            while (iter?.hasNext() == true) {
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