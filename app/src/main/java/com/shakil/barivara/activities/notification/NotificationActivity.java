package com.shakil.barivara.activities.notification;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.NotificationRecyclerAdapter;
import com.shakil.barivara.databinding.ActivityNotificationBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.notification.Notification;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding activityNotificationBinding;
    private ArrayList<Notification> notificationList;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private Tools tools;

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
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        ux = new UX(this);
        tools = new Tools(this);
    }
    //endregion

    //region perform UI interactions
    private void bindUiWithComponents() {
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion
    }
    //endregion

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllNotification("notification", new FirebaseCrudHelper.onNotificationDataFetch() {
            @Override
            public void onFetch(ArrayList<Notification> objects) {
                notificationList = objects;
                if (notificationList.size() <= 0){
                    activityNotificationBinding.mNoDataMessage.setVisibility(View.VISIBLE);
                }
                else{
                    activityNotificationBinding.mNoDataMessage.setVisibility(View.GONE);
                }
                setNotificationRecycler();
                ux.removeLoadingView();
            }
        });
    }
    //endregion

    //region set notification recycler adapter
    private void setNotificationRecycler(){
        if (notificationList != null) {
            Iterator<Notification> iter = notificationList.iterator();
            while (iter.hasNext()) {
                Notification value = iter.next();
                if (value.getTitle() == null)
                    iter.remove();
            }
        }
        NotificationRecyclerAdapter notificationRecyclerAdapter = new NotificationRecyclerAdapter(notificationList);
        activityNotificationBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityNotificationBinding.mRecyclerView.setAdapter(notificationRecyclerAdapter);
        notificationRecyclerAdapter.notifyDataSetChanged();
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(NotificationActivity.this, MainActivity.class));
    }
    //endregion
}