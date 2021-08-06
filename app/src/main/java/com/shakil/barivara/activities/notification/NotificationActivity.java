package com.shakil.barivara.activities.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.NotificationRecyclerAdapter;
import com.shakil.barivara.databinding.ActivityNotificationBinding;
import com.shakil.barivara.model.notification.Notification;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding activityNotificationBinding;
    private NotificationRecyclerAdapter notificationRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        setSupportActionBar(activityNotificationBinding.toolBar);

        activityNotificationBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationActivity.this, MainActivity.class));
            }
        });

        //region init UI and perform UI interactions
        initUI();
        bindUiWithComponents();
        //endregion
    }

    //region init UI
    private void initUI() {
    }
    //endregion

    //region perform UI interactions
    private void bindUiWithComponents() {
        //region set notification recycler adapter
        setNotificationRecycler();
        //endregion
    }
    //endregion

    //region set notification recycler adapter
    private void setNotificationRecycler(){
        notificationRecyclerAdapter = new NotificationRecyclerAdapter(getNotifications());
        activityNotificationBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityNotificationBinding.mRecyclerView.setAdapter(notificationRecyclerAdapter);
        notificationRecyclerAdapter.notifyDataSetChanged();
    }

    private ArrayList<Notification> getNotifications(){
        ArrayList<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("1","Greetings","Good morning","12-12-2021",0));
        notifications.add(new Notification("2","Test","Test Notification","09-08-2021",0));
        notifications.add(new Notification("3","Rent","Collect Rent","10-09-2021",0));
        notifications.add(new Notification("4","Bill","Collect electricity bills","12-09-2021",0));
        notifications.add(new Notification("5","Tenant","Add a new tenant","09-08-2021",1));
        return notifications;
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(NotificationActivity.this, MainActivity.class));
    }
    //endregion
}