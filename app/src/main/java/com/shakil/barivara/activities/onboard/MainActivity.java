package com.shakil.barivara.activities.onboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.activities.dashboard.DashboardActivity;
import com.shakil.barivara.activities.meter.ElectricityBillListActivity;
import com.shakil.barivara.activities.meter.MeterListActivity;
import com.shakil.barivara.activities.notification.NotificationActivity;
import com.shakil.barivara.activities.room.RentListActivity;
import com.shakil.barivara.activities.room.RoomListActivity;
import com.shakil.barivara.activities.settings.SettingsActivity;
import com.shakil.barivara.activities.tenant.TenantListActivity;
import com.shakil.barivara.activities.tutorial.TutorialActivity;
import com.shakil.barivara.adapter.RecyclerNavDrawerAdapter;
import com.shakil.barivara.databinding.ActivityMainBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.drawer.DrawerItem;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.notification.Notification;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.LanguageManager;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UtilsForAll;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.UUID;

import static com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE;
import static com.shakil.barivara.utils.Constants.mAppViewCount;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding activityMainBinding;
    private UtilsForAll utilsForAll;
    private FirebaseCrudHelper firebaseCrudHelper;
    private PrefManager prefManager;
    private Tools tools;
    private SlidingRootNav slidingRootNav;
    private RecyclerView navRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //region notification for general topic
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i(Constants.TAG,"Successfully received notification");
                            Notification notification = new Notification(UUID.randomUUID().toString(),
                                    "","","",0);
                            firebaseCrudHelper.add(notification, "notification");
                        }
                        else{
                            Log.i(Constants.TAG,"Notification received failed");
                        }
                    }
                });
        //endregion

        //region set language
        new LanguageManager(this).configLanguage();
        //endregion

        //region ask permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_CODE);
        }
        //endregion

        //region setting the navigation menu recycler
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(activityMainBinding.toolBar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        setDataAdapter();
        //endregion

        //region init objects
        init();
        //endregion

        //region set toolbar
        setSupportActionBar(activityMainBinding.toolBar);
        //endregion

        //region nav drawer open and close listener
        activityMainBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuClosed())
                    slidingRootNav.openMenu();
                else slidingRootNav.closeMenu();
            }
        });
        //endregion

        //region perform all UI interactions
        bindUIWithComponents();
        //endregion
    }

    //region UI interactions
    private void bindUIWithComponents() {
        //region Update App ViewCount
        if(prefManager.getInt(mAppViewCount)>0) {
            prefManager.set(mAppViewCount,prefManager.getInt(mAppViewCount)+1);
        }
        else prefManager.set(mAppViewCount,1);
        //endregion

        //region set text for greetings, dateTime, totalRooms, totalMeter, totalTenants
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
        //endregion

        //region on click listener for all list home item
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
        //endregion
    }
    //endregion

    //region set nav recycler data and adapter
    private void setDataAdapter() {
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(R.drawable.ic_baseline_settings, getString(R.string.settings)));
        //items.add(new DrawerItem(R.drawable.ic_notifications, getString(R.string.notifications)));
        items.add(new DrawerItem(R.drawable.ic_baseline_info_24, getString(R.string.tutorial)));
        items.add(new DrawerItem(R.drawable.ic_baseline_star_24, getString(R.string.rate_us)));

        RecyclerNavDrawerAdapter recyclerNavDrawerAdapter = new RecyclerNavDrawerAdapter(items);
        navRecycler = findViewById(R.id.navRecycler);
        navRecycler.setLayoutManager(new LinearLayoutManager(this));
        navRecycler.setAdapter(recyclerNavDrawerAdapter);
        recyclerNavDrawerAdapter.setOnItemClickListener(new RecyclerNavDrawerAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DrawerItem drawerItem) {
                if (drawerItem.getIcon() == R.drawable.ic_baseline_settings){
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
                else if (drawerItem.getIcon() == R.drawable.ic_baseline_star_24){
                    tools.rateApp();
                }
                else if (drawerItem.getIcon() == R.drawable.ic_baseline_info_24){
                    startActivity(new Intent(MainActivity.this, TutorialActivity.class));
                }
                else if (drawerItem.getIcon() == R.drawable.ic_notifications){
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                }
                slidingRootNav.closeMenu();
            }
        });
    }
    //endregion

    //region init components
    private void init() {
        prefManager = new PrefManager(this);
        tools = new Tools(this, activityMainBinding.mainLayout);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this,activityMainBinding.mainLayout);
    }
    //endregion

    //region activity components

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        utilsForAll.exitApp();
    }
    //endregion
}
