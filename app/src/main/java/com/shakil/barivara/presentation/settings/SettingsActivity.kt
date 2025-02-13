package com.shakil.barivara.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivitySettingsBinding
import com.shakil.barivara.presentation.auth.login.LoginSelectionActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    private lateinit var activitySettingsBinding: ActivitySettingsBinding
    private var languageMap = HashMap<String, String>()

    @Inject
    lateinit var prefManager: PrefManager
    private var tools = Tools(this)
    override val layoutResourceId: Int
        get() = R.layout.activity_settings

    override fun setVariables(dataBinding: ActivitySettingsBinding) {
        activitySettingsBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                LoginSelectionActivity::class.java,
                prefManager
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
                startActivity(Intent(this@SettingsActivity, LoginSelectionActivity::class.java))
            }
        }
    }
}