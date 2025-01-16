package com.shakil.barivara.presentation.onboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityHomeBinding
import com.shakil.barivara.presentation.GenericDialog
import com.shakil.barivara.presentation.auth.forgotpassword.ForgotPasswordActivity
import com.shakil.barivara.presentation.auth.login.LoginSelectionActivity
import com.shakil.barivara.presentation.dashboard.DashboardActivity
import com.shakil.barivara.presentation.generatebill.GenerateBillActivity
import com.shakil.barivara.presentation.generatebill.GeneratedBillHistoryActivity
import com.shakil.barivara.presentation.profile.ProfileActivity
import com.shakil.barivara.presentation.room.RoomListActivity
import com.shakil.barivara.presentation.tenant.TenantListActivity
import com.shakil.barivara.presentation.tutorial.TutorialActivity
import com.shakil.barivara.utils.AppUpdateHelper
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserMobile
import com.shakil.barivara.utils.LanguageManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.orFalse
import com.shakil.barivara.utils.orZero
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var activityMainBinding: ActivityHomeBinding

    @Inject
    lateinit var prefManager: PrefManager
    private lateinit var utilsForAll: UtilsForAll
    private lateinit var appUpdate: AppUpdateHelper
    private var tools = Tools(this)
    private lateinit var ux: UX
    private val viewModel by viewModels<HomeViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            tools.doPopUpForExitApp()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_home

    override fun setVariables(dataBinding: ActivityHomeBinding) {
        activityMainBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewed(ScreenNameConstants.appSreenHome)
        init()
        setupDrawerToggle()
        setupNotification()
        LanguageManager(this, prefManager).configLanguage()
        bindUIWithComponents()
        initListeners()
        initObservers()
        viewModel.getAllTenants()
        viewModel.getAllRooms()

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        viewModel.getRentDataByYearAndMonth(
            year = currentYear,
            month = currentMonth
        )
    }

    private fun init() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
        appUpdate = AppUpdateHelper(this)
    }

    private fun setupNotification() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(Constants.TAG, "Successfully received notification")
                } else {
                    Log.i(Constants.TAG, "Notification received failed")
                }
            }
    }

    private fun setupDrawerToggle() {
        setSupportActionBar(activityMainBinding.toolBar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_icon_menu)

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

    private fun initListeners() {
        activityMainBinding.toolBar.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.md_green_800
            )
        )

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        activityMainBinding.totalRoomFlatLayout.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeRoom
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    RoomListActivity::class.java
                )
            )
        }
        activityMainBinding.totalTenantLayout.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeTenant
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    TenantListActivity::class.java
                )
            )
        }

        activityMainBinding.moreDetails.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeDashboard
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    DashboardActivity::class.java
                )
            )
        }

        activityMainBinding.noBillHistoryLayoutThisMonth.btnGenerate.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeGenerateBill
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    GenerateBillActivity::class.java
                )
            )
        }

        activityMainBinding.generateBillLayout.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeGenerateBill
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    GenerateBillActivity::class.java
                )
            )
        }

        activityMainBinding.generatedBillHistoryLayout.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeGeneratedBillHistory
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    GeneratedBillHistoryActivity::class.java
                )
            )
        }

        activityMainBinding.roomList.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeRoom
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    RoomListActivity::class.java
                )
            )
        }
        activityMainBinding.tenantList.setOnClickListener {
            buttonAction(
                ButtonActionConstants.actionHomeTenant
            )
            startActivity(
                Intent(
                    this@HomeActivity,
                    TenantListActivity::class.java
                )
            )
        }
    }

    private fun initObservers() {
        viewModel.getTenants().observe(this) { tenants ->
            activityMainBinding.totalTenants.text = "${tenants.size}"
        }

        viewModel.getRooms().observe(this) { rooms ->
            activityMainBinding.totalRooms.text = "${rooms.size}"
        }

        viewModel.getLogoutResponse().observe(this) { logoutResponse ->
            if (logoutResponse.statusCode == 200) {
                tools.clearPrefForLogout(LoginSelectionActivity::class.java, prefManager)
            } else {
                Toasty.warning(
                    this,
                    getString(R.string.please_try_again_something_went_wrong),
                    Toasty.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.getRentDataByMonthAndYear().observe(this) { response ->
            if (response.status.orFalse()) {
                activityMainBinding.generatedBillHistoryLayoutThisMonth.root.visibility =
                    View.VISIBLE
                activityMainBinding.noBillHistoryLayoutThisMonth.root.visibility = View.GONE

                activityMainBinding.generatedBillHistoryLayoutThisMonth.totalCollected.text =
                    "${response.totalPaid.orZero()}"
                activityMainBinding.generatedBillHistoryLayoutThisMonth.totalDue.text =
                    "${response.totalDue.orZero()}"
            } else {
                activityMainBinding.generatedBillHistoryLayoutThisMonth.root.visibility = View.GONE
                activityMainBinding.noBillHistoryLayoutThisMonth.root.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun bindUIWithComponents() {
        activityMainBinding.navigationView.setNavigationItemSelectedListener(this)
        activityMainBinding.greetingsText.text = utilsForAll.setGreetings()
        activityMainBinding.dateTimeText.text = utilsForAll.getDateTime()
        activityMainBinding.dayText.text = utilsForAll.getDayOfTheMonth()

        if (tools.hasConnection()) {
            appUpdate.checkForUpdate(
                onUpdateAvailable = {
                    appUpdate.startImmediateUpdate(this)
                },
                onUpdateNotAvailable = {
                    // empty implementation
                },
                onError = { error ->
                    //empty implementation
                }
            )
        }

        if (Build.VERSION.SDK_INT > 32) {
            if (ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.POST_NOTIFICATIONS),
                    Constants.REQUEST_CALL_CODE
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    Constants.REQUEST_CALL_CODE
                )
            }
        }
    }

    // Handle the result of the update flow
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppUpdateHelper.IMMEDIATE_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toasty.warning(this, getString(R.string.update_failed), Toasty.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If the update is downloaded but not installed, prompt the user to complete it
        appUpdate.completeUpdate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                buttonAction(
                    ButtonActionConstants.actionHomeMenuItemLogout
                )
                val bottomSheet = GenericDialog<View>(
                    context = this,
                    layoutResId = R.layout.logout_confirmation_layout,
                    onClose = {

                    },
                    onPrimaryAction = {

                    },
                    onSecondaryAction = {
                        viewModel.logout(
                            prefManager.getString(mUserMobile)
                        )
                    }
                )
                bottomSheet.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile -> {
                buttonAction(
                    ButtonActionConstants.actionHomeMenuItemMyProfile
                )
                startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
            }

            R.id.menu_change_password -> {
                buttonAction(
                    ButtonActionConstants.actionHomeMenuItemChangePassword,
                )
                startActivity(Intent(this@HomeActivity, ForgotPasswordActivity::class.java))
            }

            R.id.menu_tutorial -> {
                buttonAction(
                    ButtonActionConstants.actionHomeMenuItemTutorial,
                )
                startActivity(Intent(this@HomeActivity, TutorialActivity::class.java))
            }

            R.id.menu_rate_us -> {
                buttonAction(
                    ButtonActionConstants.actionHomeMenuItemRateUs,
                )
                tools.rateApp()
            }

            R.id.menu_logout -> {
                buttonAction(
                    ButtonActionConstants.actionHomeMenuItemMyProfile,
                )
                val bottomSheet = GenericDialog<View>(
                    context = this,
                    layoutResId = R.layout.logout_confirmation_layout,
                    onClose = {

                    },
                    onPrimaryAction = {

                    },
                    onSecondaryAction = {
                        viewModel.logout(
                            prefManager.getString(mUserMobile)
                        )
                    }
                )
                bottomSheet.show()
            }
        }
        activityMainBinding.myDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
