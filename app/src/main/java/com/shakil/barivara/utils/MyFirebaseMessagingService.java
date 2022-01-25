package com.shakil.barivara.utils;

import static com.shakil.barivara.utils.Constants.TAG;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.notification.Notification;

import java.util.UUID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private FirebaseCrudHelper firebaseCrudHelper;
    private UtilsForAll utilsForAll;
    //region Called when message is received.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        //region Check if message contains a notification payload.
        Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        Tools.sendNotification(this, remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
        //endregion

        //region save notification
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this);
        if (
                remoteMessage.getNotification().getTitle() != null &&
                remoteMessage.getNotification().getBody() != null &&
                !TextUtils.isEmpty(remoteMessage.getNotification().getTitle()) &&
                !TextUtils.isEmpty(remoteMessage.getNotification().getBody())
                ) {
            Notification notification = new Notification(UUID.randomUUID().toString(),
                    remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),
                    utilsForAll.getDateTimeWithPM(),0);
            firebaseCrudHelper.addNotification(notification, "notification");
            Log.i(TAG,"Notification data saved");
        }
        //endregion
    }
    //endregion
}
