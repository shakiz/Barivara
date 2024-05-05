package com.shakil.barivara.servies

import android.text.TextUtils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.notification.Notification
import com.shakil.barivara.utils.Constants.TAG
import com.shakil.barivara.utils.Tools.Companion.sendNotification
import com.shakil.barivara.utils.UtilsForAll
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.from)
        sendNotification(
            this, remoteMessage.notification?.title,
            remoteMessage.notification?.body
        )
        val firebaseCrudHelper = FirebaseCrudHelper(this)
        val utilsForAll = UtilsForAll(this)
        if (remoteMessage.notification?.title != null && remoteMessage.notification?.body != null &&
            !TextUtils.isEmpty(remoteMessage.notification?.title) &&
            !TextUtils.isEmpty(remoteMessage.notification?.body)
        ) {
            val notification = Notification(
                UUID.randomUUID().toString(),
                remoteMessage.notification?.title, remoteMessage.notification?.body,
                utilsForAll.dateTimeWithPM, 0
            )
            firebaseCrudHelper.addNotification(notification, "notification")
            Log.i(TAG, "Notification data saved")
        }
    }

    override fun onNewToken(s: String) {
        Log.d("onNewToken:$TAG", "New Token: $s")
        super.onNewToken(s)
    }
}
