package com.shakil.barivara.servies

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shakil.barivara.utils.Constants.TAG
import com.shakil.barivara.utils.Tools.Companion.sendNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.from)
        sendNotification(
            this, remoteMessage.notification?.title,
            remoteMessage.notification?.body
        )
    }

    override fun onNewToken(s: String) {
        Log.d("onNewToken:$TAG", "New Token: $s")
        super.onNewToken(s)
    }
}
