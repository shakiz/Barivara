package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.auth.OtpType
import com.shakil.barivara.data.model.auth.OtpUIState
import com.shakil.barivara.databinding.ActivityPasswordSetupBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import dagger.hilt.android.AndroidEntryPoint

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
        //Default UI state
        viewModel.otpUiState.postValue(OtpUIState.SEND_OTP)
    }

    private fun initObservers() {
        viewModel.otpUiState.observe(this) { otpUiState ->
            when (otpUiState) {
                OtpUIState.SEND_OTP -> {
                    activityBinding.buttonAction.text = getString(R.string.send_otp)
                    activityBinding.layoutSendOtp.root.visibility = View.VISIBLE
                    activityBinding.layoutVerifyOtp.root.visibility = View.GONE
                    activityBinding.layoutSetupPassword.root.visibility = View.GONE
                }

                OtpUIState.VERIFY_OTP -> {
                    activityBinding.buttonAction.text = getString(R.string.verify_otp)
                    activityBinding.layoutVerifyOtp.root.visibility = View.VISIBLE
                    activityBinding.layoutSendOtp.root.visibility = View.GONE
                    activityBinding.layoutSetupPassword.root.visibility = View.GONE
                }

                else -> {
                    activityBinding.buttonAction.text = getString(R.string.setup_password)
                    activityBinding.layoutSetupPassword.root.visibility = View.VISIBLE
                    activityBinding.layoutSendOtp.root.visibility = View.GONE
                    activityBinding.layoutVerifyOtp.root.visibility = View.GONE
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun initListeners() {
        activityBinding.buttonAction.setOnClickListener {
            when (viewModel.otpUiState.value) {
                OtpUIState.SEND_OTP -> {
                    viewModel.sendOtp(
                        activityBinding.layoutSendOtp.mobileNumber.text.toString(),
                        OtpType.SET_PASS.value
                    )
                }

                OtpUIState.VERIFY_OTP -> {
                    viewModel.verifyOtp(
                        activityBinding.layoutSendOtp.mobileNumber.text.toString(),
                        activityBinding.layoutVerifyOtp.verificationCode.text.toString().toInt(),
                        OtpType.SET_PASS.value
                    )
                }

                else -> {
                    viewModel.setupPassword(
                        OtpType.SET_PASS.value,
                        viewModel.getVerifyOtpResponse().value?.verifyOtpResponse?.accessToken
                            ?: "",
                        activityBinding.layoutSetupPassword.password.text.toString(),
                        activityBinding.layoutSetupPassword.reEnterPassword.text.toString(),
                    )
                }
            }
        }
    }
}