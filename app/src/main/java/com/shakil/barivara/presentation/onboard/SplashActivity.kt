package com.shakil.barivara.presentation.onboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shakil.barivara.R
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var tools = Tools(this)

    @Inject
    lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        bindUIWithComponents()
    }

    private fun bindUIWithComponents() {
        tools.checkLogin(prefManager)
    }
}