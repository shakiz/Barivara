package com.shakil.barivara.activities.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.activities.onboard.MainActivity
import com.shakil.barivara.databinding.ActivityAboutUsBinding
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Tools
import es.dmoral.toasty.Toasty

class AboutUsActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityAboutUsBinding
    private var tools = Tools(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_us)
        setSupportActionBar(activityBinding.toolBar)
        activityBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@AboutUsActivity,
                    MainActivity::class.java
                )
            )
        }
        bindUIWithComponents()
    }
    
    private fun bindUIWithComponents() {
        activityBinding.giLibApp.setOnClickListener { tools.launchAppByPackageName(Constants.GIT_LIB_PACKAGE_NAME) }
        activityBinding.sagorKonnyaApp.setOnClickListener {
            tools.launchAppByPackageName(
                Constants.SAGORKONNA_PACKAGE_NAME
            )
        }
        activityBinding.fbContactUs.setOnClickListener {
            Toasty.info(
                applicationContext,
                getString(R.string.opening_facebook),
                Toasty.LENGTH_LONG
            ).show()
            tools.launchUrl(Constants.VARA_ADAI_FB_PAGE_LINK)
        }
        activityBinding.mailContactUs.setOnClickListener { tools.launchGmail() }
        activityBinding.messageContactUs.setOnClickListener { tools.sendMessage(Constants.MY_CONTACT_NO) }
    }
}