package com.shakil.barivara.presentation.onboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shakil.barivara.R
import com.shakil.barivara.utils.AppUpdate
import com.shakil.barivara.utils.AppUpdate.onGetUpdate
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var tools = Tools(this)
    private lateinit var appUpdate: AppUpdate

    @Inject
    lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        appUpdate = AppUpdate(this)
        bindUIWithComponents()
    }

    private fun bindUIWithComponents() {
        if (tools.hasConnection()) {
            appUpdate.getUpdate(object : onGetUpdate {
                override fun onResult(updated: Boolean) {
                    if (!updated) {
                        appUpdate.checkUpdate(showUpdated = false, cancelable = false, prefManager)
                    } else {
                        tools.checkLogin(prefManager)
                    }
                }
            }, prefManager)
        } else {
            tools.checkLogin(prefManager)
        }
    }
}