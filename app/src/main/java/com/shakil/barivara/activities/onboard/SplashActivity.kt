package com.shakil.barivara.activities.onboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shakil.barivara.R
import com.shakil.barivara.utils.AppUpdate
import com.shakil.barivara.utils.Tools

class SplashActivity : AppCompatActivity() {
    private var tools = Tools(this)
    private var appUpdate = AppUpdate(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        bindUIWithComponents()
    }

    private fun bindUIWithComponents() {
        if (tools.hasConnection()) {
            appUpdate.getUpdate { updated ->
                if (!updated) {
                    appUpdate.checkUpdate(false, true)
                } else {
                    tools.checkLogin()
                }
            }
        } else {
            tools.checkLogin()
        }
    }
}