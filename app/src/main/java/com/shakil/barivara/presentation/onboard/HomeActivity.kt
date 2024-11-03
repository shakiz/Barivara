package com.shakil.barivara.presentation.onboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityHomeBinding
import com.shakil.barivara.presentation.GenericDialog
import com.shakil.barivara.presentation.adapter.ImageSliderAdapter
import com.shakil.barivara.presentation.auth.forgotpassword.ForgotPasswordActivity
import com.shakil.barivara.presentation.auth.login.LoginSelectionActivity
import com.shakil.barivara.presentation.dashboard.DashboardActivity
import com.shakil.barivara.presentation.generatebill.GenerateBillActivity
import com.shakil.barivara.presentation.profile.ProfileActivity
import com.shakil.barivara.presentation.room.RoomListActivity
import com.shakil.barivara.presentation.tenant.TenantListActivity
import com.shakil.barivara.presentation.tutorial.TutorialActivity
import com.shakil.barivara.utils.ButtonActionConstants
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserMobile
import com.shakil.barivara.utils.LanguageManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.ScreenNameConstants
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var activityMainBinding: ActivityHomeBinding
    private lateinit var prefManager: PrefManager
    private var tools = Tools(this)
    private lateinit var ux: UX
    private val viewModel by viewModels<HomeViewModel>()
    private var currentPage = 0
    private val slideDelay: Long = 3000
    private val handler = Handler(Looper.getMainLooper())

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
        setUpSlider()
        setupDrawerToggle()
        setupNotification()
        LanguageManager(this).configLanguage()
        bindUIWithComponents()
        initListeners()
        initObservers()
        viewModel.getAllTenants()
        viewModel.getAllRooms()
    }

    private fun init() {
        prefManager = PrefManager(this)
        ux = UX(this)
    }

    private fun setUpSlider() {
        // Set up the adapter
        val adapter = ImageSliderAdapter(this, viewModel.getSliderData())
        val loopingImageList = viewModel.getSliderData() + viewModel.getSliderData()[0]
        activityMainBinding.viewPager.adapter = adapter
        // Create indicators
        setupIndicators(viewModel.getSliderData().size)
        setCurrentIndicator(0)

        // Change indicator on page change
        activityMainBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == loopingImageList.size - 1) {
                    // If at the fake last position, reset to the real first position without animation
                    handler.postDelayed({
                        activityMainBinding.viewPager.setCurrentItem(
                            0,
                            false
                        ) // false disables animation
                    }, 300) // Short delay to allow smooth transition
                } else {
                    setCurrentIndicator(position % viewModel.getSliderData().size)
                    currentPage = position
                }
            }
        })

        // Start auto-slide
        startAutoSlide()
    }

    private fun setupIndicators(count: Int) {
        val indicators = Array(count) { ImageView(this) }
        val params = LinearLayout.LayoutParams(
            24,
            24
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = ImageView(this).apply {
                setImageResource(R.drawable.ic_circle)
                layoutParams = params
            }
            activityMainBinding.indicatorLayout.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = activityMainBinding.indicatorLayout.childCount
        for (i in 0 until childCount) {
            val imageView = activityMainBinding.indicatorLayout.getChildAt(i) as ImageView
            imageView.setImageResource(
                if (i == index) R.drawable.ic_circle else R.drawable.ic_circle_inactive
            )
        }
    }

    private fun startAutoSlide() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage == viewModel.getSliderData().size) {
                    currentPage = 0
                }
                activityMainBinding.viewPager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, slideDelay)
            }
        }, slideDelay)
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

        activityMainBinding.generateBill.setOnClickListener {
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
                        tools.clearPrefForLogout(LoginSelectionActivity::class.java, prefManager)
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
                        tools.clearPrefForLogout(LoginSelectionActivity::class.java, prefManager)
                    }
                )
                bottomSheet.show()
            }
        }
        activityMainBinding.myDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
