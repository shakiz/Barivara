package com.shakil.barivara.presentation.settings

import android.content.Intent
import android.os.Bundle
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityAboutUsBinding
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Tools
import es.dmoral.toasty.Toasty

class AboutUsActivity : BaseActivity<ActivityAboutUsBinding>() {
    private lateinit var activityBinding: ActivityAboutUsBinding
    private var tools = Tools(this)
    override val layoutResourceId: Int
        get() = R.layout.activity_about_us

    override fun setVariables(dataBinding: ActivityAboutUsBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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