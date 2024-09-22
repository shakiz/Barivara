package com.shakil.barivara.presentation.onboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shakil.barivara.R
import com.shakil.barivara.utils.AppUpdate
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools

class SplashActivity : AppCompatActivity() {
    private var tools = Tools(this)
    private var appUpdate = AppUpdate(this)
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        prefManager = PrefManager(this)
        setContentView(R.layout.activity_splash)
        bindUIWithComponents()
    }

    private fun bindUIWithComponents() {
        tools.checkLogin(prefManager)
    }
}