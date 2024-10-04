package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.auth.OtpType
import com.shakil.barivara.databinding.ActivityLoginBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.presentation.auth.registration.MobileRegVerificationActivity
import com.shakil.barivara.utils.Constants.mUserMobile
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
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

    override val layoutResourceId: Int
        get() = R.layout.activity_login

    override fun setVariables(dataBinding: ActivityLoginBinding) {
        activityBinding = dataBinding
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
                Toasty.success(this, sendOtpResponse.message).show()
                val intent = Intent(
                    this, MobileRegVerificationActivity::class.java
                )
                intent.putExtra(mUserMobile, activityBinding.mobileNumber.text.toString())
                startActivity(intent)
            }
        }

        viewModel.getSendOtpErrorResponse().observe(this) { sendOtpErrorResponse ->
            Toasty.warning(this, sendOtpErrorResponse.message).show()
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
                    activityBinding.mobileNumber.text.toString(),
                    OtpType.OTP.value
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