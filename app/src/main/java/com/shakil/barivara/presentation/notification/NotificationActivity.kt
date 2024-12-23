package com.shakil.barivara.presentation.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.notification.Notification
import com.shakil.barivara.data.remote.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.databinding.ActivityNotificationBinding
import com.shakil.barivara.presentation.adapter.NotificationRecyclerAdapter
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {
    private lateinit var activityNotificationBinding: ActivityNotificationBinding
    private var notificationList: ArrayList<Notification> = arrayListOf()
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var ux : UX
    private var tools = Tools(this)
    override val layoutResourceId: Int
        get() = R.layout.activity_notification

    override fun setVariables(dataBinding: ActivityNotificationBinding) {
        activityNotificationBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(activityNotificationBinding.toolBar)
        activityNotificationBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@NotificationActivity,
                    HomeActivity::class.java
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