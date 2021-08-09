package com.shakil.barivara.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.notification.Notification;

import java.util.UUID;

import static com.shakil.barivara.utils.Constants.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private FirebaseCrudHelper firebaseCrudHelper;
    private UtilsForAll utilsForAll;
    //region Called when message is received.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        //region Check if message contains a notification payload.
        Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        sendNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
        //endregion

        //region save notification
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this);
        Notification notification = new Notification(UUID.randomUUID().toString(),
                remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),
                utilsForAll.getDateTimeText(),0);
        firebaseCrudHelper.addNotification(notification, "notification");
        Log.i(TAG,"Notification data saved");
        //endregion
    }
    //endregion

    //region Create and show a simple notification containing the received FCM message.
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "1001")
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    //endregion
}
