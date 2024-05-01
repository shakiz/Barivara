package com.shakil.barivara.activities.onboard

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.shakil.barivara.R
import com.shakil.barivara.activities.auth.ForgotPasswordActivity
import com.shakil.barivara.activities.auth.LoginActivity
import com.shakil.barivara.activities.dashboard.DashboardActivity
import com.shakil.barivara.activities.generatebill.GenerateBillActivity
import com.shakil.barivara.activities.meter.ElectricityBillListActivity
import com.shakil.barivara.activities.meter.MeterListActivity
import com.shakil.barivara.activities.note.NoteListActivity
import com.shakil.barivara.activities.notification.NotificationActivity
import com.shakil.barivara.activities.profile.ProfileActivity
import com.shakil.barivara.activities.room.RentListActivity
import com.shakil.barivara.activities.room.RoomListActivity
import com.shakil.barivara.activities.settings.AboutUsActivity
import com.shakil.barivara.activities.settings.SettingsActivity
import com.shakil.barivara.activities.tenant.TenantListActivity
import com.shakil.barivara.activities.tutorial.TutorialActivity
import com.shakil.barivara.databinding.ActivityMainBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.meter.Meter
import com.shakil.barivara.model.room.Room
import com.shakil.barivara.model.tenant.Tenant
import com.shakil.barivara.utils.AppAnalytics
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.LanguageManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UtilsForAll
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var activityMainBinding: ActivityMainBinding
    private var utilsForAll = UtilsForAll(this)
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var prefManager: PrefManager
    private var tools = Tools(this)
    private var customAdManager = CustomAdManager(this)
    private var appAnalytics = AppAnalytics(this)

    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            tools.doPopUpForExitApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        prefManager = PrefManager(this)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityMainBinding.toolBar.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.md_green_800
            )
        )
        setSupportActionBar(activityMainBinding.toolBar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_icon_menu)
        setupDrawerToggle()
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(Constants.TAG, "Successfully received notification")
                } else {
                    Log.i(Constants.TAG, "Notification received failed")
                }
            }
        LanguageManager(this).configLanguage()
        if (Build.VERSION.SDK_INT > 32) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.POST_NOTIFICATIONS),
                    Constants.REQUEST_CALL_CODE
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    Constants.REQUEST_CALL_CODE
                )
            }
        }
        bindUIWithComponents()
    }

    private fun setupDrawerToggle() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            activityMainBinding.myDrawerLayout,
            activityMainBinding.toolBar,
            R.string.app_name,
            R.string.app_name
        )
        activityMainBinding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun bindUIWithComponents() {
        activityMainBinding.navigationView.setNavigationItemSelectedListener(this)
        customAdManager.generateAd(activityMainBinding.adView)
        activityMainBinding.totalRoomFlatLayout.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    RoomListActivity::class.java
                )
            )
        }
        activityMainBinding.totalMeterLayout.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    MeterListActivity::class.java
                )
            )
        }
        activityMainBinding.totalTenantLayout.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    TenantListActivity::class.java
                )
            )
        }
        if (prefManager.getInt(Constants.mAppViewCount) > 0) {
            prefManager[Constants.mAppViewCount] =
                prefManager.getInt(Constants.mAppViewCount) + 1
        } else prefManager[Constants.mAppViewCount] = 1
        activityMainBinding.GreetingsText.text = utilsForAll.setGreetings()
        activityMainBinding.DateTimeText.text = utilsForAll.dateTime
        activityMainBinding.DayText.text = utilsForAll.dayOfTheMonth
        firebaseCrudHelper.fetchAllTenant(
            "tenant",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onTenantDataFetch {
                override fun onFetch(objects: ArrayList<Tenant?>?) {
                    activityMainBinding.totalTenants.text = "" + objects?.size
                }
            })

        firebaseCrudHelper.fetchAllMeter(
            "meter",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onDataFetch {
                override fun onFetch(objects: ArrayList<Meter?>?) {
                    activityMainBinding.totalMeters.text = "" + objects?.size
                }
            })

        firebaseCrudHelper.fetchAllRoom(
            "room",
            prefManager.getString(mUserId),
            object : FirebaseCrudHelper.onRoomDataFetch {
                override fun onFetch(objects: ArrayList<Room?>?) {
                    activityMainBinding.totalRooms.text = "" + objects?.size
                }
            })
        activityMainBinding.moreDetails.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    DashboardActivity::class.java
                )
            )
        }
        activityMainBinding.rentList.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    RentListActivity::class.java
                )
            )
        }
        activityMainBinding.billList.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    ElectricityBillListActivity::class.java
                )
            )
        }
        activityMainBinding.meterList.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    MeterListActivity::class.java
                )
            )
        }
        activityMainBinding.roomList.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    RoomListActivity::class.java
                )
            )
        }
        activityMainBinding.tenantList.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    TenantListActivity::class.java
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                tools.doPopUpForLogout(LoginActivity::class.java, prefManager)
                return true
            }

            R.id.menu_notification -> {
                startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_generate_bill -> {
                appAnalytics.registerEvent(
                    "generate_bill",
                    appAnalytics.setData("generate_bill", "Generate Bill Activity Launched")
                )
                startActivity(Intent(this@MainActivity, GenerateBillActivity::class.java))
            }

            R.id.menu_profile -> {
                appAnalytics.registerEvent(
                    "profile",
                    appAnalytics.setData("profile", "Profile Activity Launched")
                )
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }

            R.id.menu_settings -> {
                appAnalytics.registerEvent(
                    "settings",
                    appAnalytics.setData("settings", "Settings Activity Launched")
                )
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }

            R.id.menu_change_password -> {
                appAnalytics.registerEvent(
                    "changePassword",
                    appAnalytics.setData("changePassword", "Change Password Activity Launched")
                )
                startActivity(Intent(this@MainActivity, ForgotPasswordActivity::class.java))
            }

            R.id.menu_note -> {
                appAnalytics.registerEvent(
                    "note",
                    appAnalytics.setData("note", "Note Activity Launched")
                )
                startActivity(Intent(this@MainActivity, NoteListActivity::class.java))
            }

            R.id.menu_notification -> {
                appAnalytics.registerEvent(
                    "notification",
                    appAnalytics.setData("notification", "Notification Activity Launched")
                )
                startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
            }

            R.id.menu_tutorial -> {
                appAnalytics.registerEvent(
                    "tutorial",
                    appAnalytics.setData("tutorial", "Tutorial Activity Launched")
                )
                startActivity(Intent(this@MainActivity, TutorialActivity::class.java))
            }

            R.id.menu_about_us -> {
                appAnalytics.registerEvent(
                    "about_us",
                    appAnalytics.setData("about_us", "About Us Launched")
                )
                startActivity(Intent(this@MainActivity, AboutUsActivity::class.java))
            }

            R.id.menu_rate_us -> {
                appAnalytics.registerEvent(
                    "rate_us",
                    appAnalytics.setData("rate_us", "Rate Us Launched")
                )
                tools.rateApp()
            }

            R.id.menu_logout -> tools.doPopUpForLogout(LoginActivity::class.java, prefManager)
        }
        activityMainBinding.myDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
