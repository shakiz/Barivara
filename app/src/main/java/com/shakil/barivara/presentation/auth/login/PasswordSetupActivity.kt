package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityPasswordSetupBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class PasswordSetupActivity : BaseActivity<ActivityPasswordSetupBinding>() {
    private lateinit var activityBinding: ActivityPasswordSetupBinding
    private lateinit var ux: UX
    private lateinit var utilsForAll: UtilsForAll

    private val viewModel by viewModels<AuthViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@PasswordSetupActivity, LoginSelectionActivity::class.java))
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_password_setup

    override fun setVariables(dataBinding: ActivityPasswordSetupBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        initUI()
        initObservers()
        initListeners()
    }

    private fun initUI() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun initListeners() {
        activityBinding.setUpPassword.setOnClickListener {
            if (validation()) {

            }
        }
    }

    private fun validation(): Boolean {
        if (!activityBinding.password.text.isNullOrEmpty()) {
            Toasty.warning(
                this@PasswordSetupActivity,
                getString(R.string.password_validation),
                Toast.LENGTH_LONG,
                true
            ).show()
            return true
        }

        if (!activityBinding.reEnterPassword.text.isNullOrEmpty()) {
            Toasty.warning(
                this@PasswordSetupActivity,
                getString(R.string.re_enter_password_validation),
                Toast.LENGTH_LONG,
                true
            ).show()
            return true
        }
        return false
    }
}