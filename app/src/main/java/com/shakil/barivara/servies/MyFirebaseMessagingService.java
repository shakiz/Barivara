package com.shakil.barivara.servies;

import static com.shakil.barivara.utils.Constants.TAG;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.notification.Notification;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UtilsForAll;

import java.util.UUID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        Tools.sendNotification(this, remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());

        FirebaseCrudHelper firebaseCrudHelper = new FirebaseCrudHelper(this);
        UtilsForAll utilsForAll = new UtilsForAll(this);
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
    }


    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("onNewToken:"+TAG,"New Token: "+s);
        super.onNewToken(s);
    }
}
