package com.shakil.barivara.activities.onboard;

import static com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE;
import static com.shakil.barivara.utils.Constants.mAppViewCount;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.ForgotPasswordActivity;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.activities.dashboard.DashboardActivity;
import com.shakil.barivara.activities.generatebill.GenerateBillActivity;
import com.shakil.barivara.activities.meter.ElectricityBillListActivity;
import com.shakil.barivara.activities.meter.MeterListActivity;
import com.shakil.barivara.activities.note.NoteListActivity;
import com.shakil.barivara.activities.notification.NotificationActivity;
import com.shakil.barivara.activities.profile.ProfileActivity;
import com.shakil.barivara.activities.room.RentListActivity;
import com.shakil.barivara.activities.room.RoomListActivity;
import com.shakil.barivara.activities.settings.AboutUsActivity;
import com.shakil.barivara.activities.settings.SettingsActivity;
import com.shakil.barivara.activities.tenant.TenantListActivity;
import com.shakil.barivara.activities.tutorial.TutorialActivity;
import com.shakil.barivara.databinding.ActivityMainBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.servies.MonthlyJobScheduler;
import com.shakil.barivara.utils.AppAnalytics;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.LanguageManager;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UtilsForAll;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding activityMainBinding;
    private UtilsForAll utilsForAll;
    private FirebaseCrudHelper firebaseCrudHelper;
    private PrefManager prefManager;
    private Tools tools;
    private CustomAdManager customAdManager;
    private AppAnalytics appAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //scheduleMonthlyAlarm();

        init();

        activityMainBinding.toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_green_800));
        setSupportActionBar(activityMainBinding.toolBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icon_menu);
        setupDrawerToggle();

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i(Constants.TAG,"Successfully received notification");
                        }
                        else{
                            Log.i(Constants.TAG,"Notification received failed");
                        }
                    }
                });

        new LanguageManager(this).configLanguage();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_CODE);
        }

        bindUIWithComponents();
    }

    private void init() {
        prefManager = new PrefManager(this);
        tools = new Tools(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this);
        customAdManager = new CustomAdManager(this);
        appAnalytics = new AppAnalytics(this);
    }

    private void setupDrawerToggle(){
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, activityMainBinding.myDrawerLayout, activityMainBinding.toolBar, R.string.app_name, R.string.app_name);
        activityMainBinding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void bindUIWithComponents() {
        activityMainBinding.navigationView.setNavigationItemSelectedListener(this);

        customAdManager.generateAd(activityMainBinding.adView);

        activityMainBinding.totalRoomFlatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RoomListActivity.class));
            }
        });
        activityMainBinding.totalMeterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MeterListActivity.class));
            }
        });
        activityMainBinding.totalTenantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TenantListActivity.class));
            }
        });

        if(prefManager.getInt(mAppViewCount)>0) {
            prefManager.set(mAppViewCount,prefManager.getInt(mAppViewCount)+1);
        }
        else prefManager.set(mAppViewCount,1);

        activityMainBinding.GreetingsText.setText(utilsForAll.setGreetings());
        activityMainBinding.DateTimeText.setText(utilsForAll.getDateTime());
        activityMainBinding.DayText.setText(utilsForAll.getDayOfTheMonth());

        firebaseCrudHelper.fetchAllTenant("tenant", new FirebaseCrudHelper.onTenantDataFetch() {
            @Override
            public void onFetch(ArrayList<Tenant> objects) {
                activityMainBinding.totalTenants.setText(""+objects.size());
            }
        });

        firebaseCrudHelper.fetchAllMeter("meter", new FirebaseCrudHelper.onDataFetch() {
            @Override
            public void onFetch(ArrayList<Meter> objects) {
                activityMainBinding.totalMeters.setText(""+objects.size());
            }
        });

        firebaseCrudHelper.fetchAllRoom("room", new FirebaseCrudHelper.onRoomDataFetch() {
            @Override
            public void onFetch(ArrayList<Room> objects) {
                activityMainBinding.totalRooms.setText(""+objects.size());
            }
        });

        activityMainBinding.moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            }
        });

        activityMainBinding.rentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RentListActivity.class));
            }
        });

        activityMainBinding.billList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ElectricityBillListActivity.class));
            }
        });

        activityMainBinding.meterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MeterListActivity.class));
            }
        });

        activityMainBinding.roomList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RoomListActivity.class));
            }
        });

        activityMainBinding.tenantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TenantListActivity.class));
            }
        });
    }

    private void scheduleMonthlyAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Intent intent = new Intent(this, MonthlyJobScheduler.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 30, // 30 days for a month
                    pendingIntent
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                tools.doPopUpForLogout(LoginActivity.class);
                return true;
            case R.id.menu_notification:
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_generate_bill:
                appAnalytics.registerEvent("generate_bill", appAnalytics.setData("generate_bill","Generate Bill Activity Launched"));
                startActivity(new Intent(MainActivity.this, GenerateBillActivity.class));
                break;
            case R.id.menu_profile:
                appAnalytics.registerEvent("profile", appAnalytics.setData("profile","Profile Activity Launched"));
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.menu_settings:
                appAnalytics.registerEvent("settings", appAnalytics.setData("settings","Settings Activity Launched"));
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.menu_change_password:
                appAnalytics.registerEvent("changePassword", appAnalytics.setData("changePassword","Change Password Activity Launched"));
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.menu_note:
                appAnalytics.registerEvent("note", appAnalytics.setData("note","Note Activity Launched"));
                startActivity(new Intent(MainActivity.this, NoteListActivity.class));
                break;
            case R.id.menu_notification:
                appAnalytics.registerEvent("notification", appAnalytics.setData("notification","Notification Activity Launched"));
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                break;
            case R.id.menu_tutorial:
                appAnalytics.registerEvent("tutorial", appAnalytics.setData("tutorial","Tutorial Activity Launched"));
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
                break;
            case R.id.menu_about_us:
                appAnalytics.registerEvent("about_us", appAnalytics.setData("about_us","About Us Launched"));
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;
            case R.id.menu_rate_us:
                appAnalytics.registerEvent("rate_us", appAnalytics.setData("rate_us","Rate Us Launched"));
                tools.rateApp();
                break;
            case R.id.menu_logout:
                tools.doPopUpForLogout(LoginActivity.class);
                break;
        }
        activityMainBinding.myDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        tools.doPopUpForExitApp(this);
    }
}
