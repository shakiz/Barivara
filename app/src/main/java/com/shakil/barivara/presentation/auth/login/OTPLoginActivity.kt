package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.auth.OtpType
import com.shakil.barivara.databinding.ActivityOtpLoginBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.presentation.auth.registration.MobileRegVerificationActivity
import com.shakil.barivara.utils.Constants.mOtpResendTime
import com.shakil.barivara.utils.Constants.mUserMobile
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class OTPLoginActivity : BaseActivity<ActivityOtpLoginBinding>() {
    private lateinit var activityBinding: ActivityOtpLoginBinding
    private lateinit var ux: UX
    @Inject
    lateinit var utilsForAll: UtilsForAll
    private val viewModel by viewModels<AuthViewModel>()
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@OTPLoginActivity, LoginSelectionActivity::class.java))
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_otp_login

    override fun setVariables(dataBinding: ActivityOtpLoginBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        validation.setEditTextIsNotEmpty(
            arrayOf("mobileNumber"), arrayOf(
                getString(R.string.mobile_validation)
            )
        )
        initUI()
        bindUiWithComponents()
        initObservers()
    }

    private fun initUI() {
        ux = UX(this)
    }

    private fun initObservers() {
        viewModel.getSendOtpResponse().observe(this) { sendOtpResponse ->
            if (sendOtpResponse.sendOtpResponse.otpValidationTime > 0) {
                Toasty.success(this, sendOtpResponse.message).show()
                val intent = Intent(
                    this, MobileRegVerificationActivity::class.java
                )
                intent.putExtra(mUserMobile, activityBinding.mobileNumber.text.toString())
                intent.putExtra(mOtpResendTime, sendOtpResponse.sendOtpResponse.otpValidationTime)
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
        activityBinding.login.setOnClickListener {
            if (validation.isValid) {
                if (activityBinding.mobileNumber.text.length == 11) {
                    utilsForAll.hideSoftKeyboard(this)
                    viewModel.sendOtp(
                        activityBinding.mobileNumber.text.toString(),
                        OtpType.OTP.value
                    )
                } else {
                    Toasty.warning(
                        this@OTPLoginActivity,
                        getString(R.string.validation_mobile_number_length),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                }
            }
        }
    }
}