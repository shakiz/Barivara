package com.shakil.barivara.activities.onboard;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.activities.dashboard.DashboardActivity;
import com.shakil.barivara.activities.meter.ElectricityBillListActivity;
import com.shakil.barivara.activities.meter.MeterListActivity;
import com.shakil.barivara.activities.room.RentListActivity;
import com.shakil.barivara.activities.room.RoomListActivity;
import com.shakil.barivara.activities.settings.SettingsActivity;
import com.shakil.barivara.activities.tenant.TenantListActivity;
import com.shakil.barivara.databinding.ActivityMainBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.LanguageManager;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.UtilsForAll;

import java.util.ArrayList;

import static com.shakil.barivara.utils.Constants.mAppViewCount;
import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mLanguage;
import static com.shakil.barivara.utils.Constants.mUserId;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding activityMainBinding;
    private UtilsForAll utilsForAll;
    private FirebaseCrudHelper firebaseCrudHelper;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region set language
        new LanguageManager(this).configLanguage();
        //endregion

        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();

        setSupportActionBar(activityMainBinding.toolBar);
        activityMainBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bindUIWithComponents();
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

    //region init components
    private void init() {
        prefManager = new PrefManager(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        utilsForAll = new UtilsForAll(this,activityMainBinding.mainLayout);
    }
    //endregion

    //region logout click action
    public void doPopUpForLogout(){
        Button cancel, logout;
        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_confirmation_layout);
        dialog.setCanceledOnTouchOutside(true);

        cancel = dialog.findViewById(R.id.cancelButton);
        logout = dialog.findViewById(R.id.logoutButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.set(mAppViewCount, 0);
                prefManager.set(mIsLoggedIn, false);
                prefManager.set(mUserId, "");
                prefManager.set(mLanguage, "en");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(MainActivity.this, getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
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
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.menu_logout:
                doPopUpForLogout();
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
