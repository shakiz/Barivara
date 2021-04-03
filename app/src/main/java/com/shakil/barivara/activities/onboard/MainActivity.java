package com.shakil.barivara.activities.onboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.navigation.NavigationView;
import com.shakil.barivara.dbhelper.DbHelperParent;
import com.shakil.barivara.utils.UtilsForAll;\

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding activityMainBinding;
    private ActionBarDrawerToggle toggle;
    private DbHelperParent dbHelperParent;
    private UtilsForAll utilsForAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();

        setSupportActionBar(activityMainBinding.toolBar);
        activityMainBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    activityMainBinding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        toggle = new ActionBarDrawerToggle(
                this, activityMainBinding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityMainBinding.drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        activityMainBinding.navView.setNavigationItemSelectedListener(this);

        bindUIWithComponents();
    }

    //region UI interactions
    private void bindUIWithComponents() {
        //region set text for greetings, dateTime, totalRooms, totalMeter, totalTenants
        activityMainBinding.GreetingsText.setText(utilsForAll.setGreetings());
        activityMainBinding.DateTimeText.setText(utilsForAll.getDateTimeText());
        activityMainBinding.totalRooms.setText(""+dbHelperParent.getTotalRoomRows());
        activityMainBinding.totalMeters.setText(""+dbHelperParent.getTotalMeterRows());
        activityMainBinding.totalTenants.setText(""+dbHelperParent.getTotalTenantRows());
        //endregion

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
    //endregion

    //region init components
    private void init() {
        dbHelperParent = new DbHelperParent(this);
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
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
        }
        return toggle != null && toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handling navigation view item clicks based on their respective ids.
        int id = item.getItemId();
        if (id == R.id.nav_meter_list){
            startActivity(new Intent(MainActivity.this, MeterListActivity.class));
        }
        else if (id == R.id.nav_room_list){
            startActivity(new Intent(MainActivity.this, RoomListActivity.class));
        }
        else if (id == R.id.nav_tenant_list){
            startActivity(new Intent(MainActivity.this, TenantListActivity.class));
        }
        else if (id == R.id.nav_add_room){
            startActivity(new Intent(MainActivity.this, RoomActivity.class));
        }
        else if (id == R.id.nav_add_meter){
            startActivity(new Intent(MainActivity.this, NewMeterActivity.class));
        }
        else if (id == R.id.nav_new_rent){
            startActivity(new Intent(MainActivity.this, RentDetailsActivity.class));
        }
        else if (id == R.id.nav_new_electricity_bill){
            startActivity(new Intent(MainActivity.this, ElectricityBillDetailsActivity.class));
        }
        activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            utilsForAll.exitApp();
        }
    }
    //endregion
}
