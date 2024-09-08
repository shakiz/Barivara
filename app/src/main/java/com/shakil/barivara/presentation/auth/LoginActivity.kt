package com.shakil.barivara.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityLoginBinding
import com.shakil.barivara.utils.Constants.mUserMobile
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityLoginBinding
    private lateinit var ux: UX
    private lateinit var utilsForAll: UtilsForAll
    private var loginWithStr: String? = null
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

    private val viewModel by viewModels<AuthViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            utilsForAll.exitApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        initUI()
        loginWithStr = getString(R.string.mobile)
        bindUiWithComponents()
        initObservers()
    }

    private fun initUI() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
    }

    private fun initObservers() {
        viewModel.getSendOtpResponse().observe(this) { sendOtpResponse ->
            if (!sendOtpResponse.sendOtpResponse.otpValidationTime.isNullOrEmpty()) {
                val intent = Intent(
                    this, MobileRegVerificationActivity::class.java
                )
                intent.putExtra(mUserMobile, activityBinding.mobileNumber.text.toString())
                startActivity(intent)
            }
        }

        viewModel.getSendOtpErrorResponse().observe(this) { sendOtpErrorResponse ->
            Toasty.warning(this, getString(R.string.please_try_again_something_went_wrong)).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun bindUiWithComponents() {
        validation(arrayOf("mobileNumber"), arrayOf(getString(R.string.mobile_validation)))
        activityBinding.login.setOnClickListener {
            if (!activityBinding.mobileNumber.text.isNullOrEmpty() && activityBinding.mobileNumber.text.length == 11) {
                utilsForAll.hideSoftKeyboard(this)
                viewModel.sendOtp(
                    activityBinding.mobileNumber.text.toString()
                )
            } else {
                Toasty.warning(
                    this@LoginActivity,
                    getString(R.string.mobile_validation),
                    Toast.LENGTH_LONG,
                    true
                ).show()
            }
        }
    }

    private fun validation(resNames: Array<String>, validationMessages: Array<String>) {
        validation.setEditTextIsNotEmpty(resNames, validationMessages)
    }
}