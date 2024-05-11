package com.shakil.barivara.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.presentation.auth.LoginActivity
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.databinding.ActivitySettingsBinding
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.LanguageManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools

class SettingsActivity : AppCompatActivity() {
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private var languageMap = HashMap<String, String>()
    private lateinit var languageManager: LanguageManager
    private lateinit var prefManager: PrefManager
    private var tools = Tools(this)
    private var customAdManager = CustomAdManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        languageManager = LanguageManager(this)
        prefManager = PrefManager(this)

        setSupportActionBar(activitySettingsBinding.toolBar)
        activitySettingsBinding.toolBar.setNavigationOnClickListener { finish() }
        setupLanguage()
        bindUiWithComponents()
    }

    private fun setupLanguage() {
        languageMap.clear()
        languageMap["bn"] = getString(R.string.bengali)
        languageMap["en"] = getString(R.string.english)
    }

    private fun bindUiWithComponents() {
        customAdManager.generateAd(activitySettingsBinding.adView)
        if (!TextUtils.isEmpty(prefManager.getString(Constants.mUserFullName))) {
            activitySettingsBinding.UserFullName.text =
                getString(R.string.username) + ":" + prefManager.getString(
                    Constants.mUserFullName
                )
        } else {
            activitySettingsBinding.UserFullName.text = getString(R.string.username_not_found)
        }
        if (!TextUtils.isEmpty(prefManager.getString(Constants.mUserEmail))) {
            activitySettingsBinding.Email.text =
                getString(R.string.email) + ":" + prefManager.getString(
                    Constants.mUserEmail
                )
        } else {
            activitySettingsBinding.Email.text = getString(R.string.email_not_found)
        }
        if (!TextUtils.isEmpty(prefManager.getString(Constants.mUserMobile))) {
            activitySettingsBinding.Mobile.text =
                getString(R.string.mobile) + ":" + prefManager.getString(
                    Constants.mUserMobile
                )
        } else {
            activitySettingsBinding.Mobile.text = getString(R.string.mobile_not_found)
        }
        val language = prefManager.getString(Constants.mLanguage)
        if (language != null) {
            if (!TextUtils.isEmpty(language)) {
                activitySettingsBinding.currentAppLanguage.text = languageMap[language]
            } else {
                activitySettingsBinding.currentAppLanguage.text = getString(R.string.english)
            }
        } else {
            activitySettingsBinding.currentAppLanguage.text = getString(R.string.english)
        }
        activitySettingsBinding.logoutButton.setOnClickListener {
            tools.doPopUpForLogout(
                LoginActivity::class.java,
                prefManager
            )
        }
        activitySettingsBinding.language.setOnClickListener {
            languageManager.setLanguage(
                MainActivity::class.java
            )
        }
        activitySettingsBinding.googleLoginLayout.setOnClickListener {
            if (prefManager.getBoolean(Constants.mIsLoggedIn)) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.already_logged_in),
                    Toast.LENGTH_SHORT
                ).show()
                activitySettingsBinding.googleLoginLayout.isClickable = false
                activitySettingsBinding.googleLoginLayout.isActivated = false
            } else {
                startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
            }
        }
    }
}