package com.shakil.barivara.presentation.auth.login

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityLoginSelectionBinding
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginSelectionActivity : BaseActivity<ActivityLoginSelectionBinding>() {
    private lateinit var activityBinding: ActivityLoginSelectionBinding
    private lateinit var ux: UX
    private lateinit var utilsForAll: UtilsForAll

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            utilsForAll.exitApp()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_login_selection

    override fun setVariables(dataBinding: ActivityLoginSelectionBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        initUI()
        initListeners()
    }

    private fun initUI() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
    }

    private fun initListeners() {
        activityBinding.loginWithOtpLayout.setOnClickListener { }
        activityBinding.loginWithPasswordLayout.setOnClickListener { }
    }
}