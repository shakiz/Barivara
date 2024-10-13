package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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
                        ("${activityBinding.layoutVerifyOtp.verificationCode1.text}${activityBinding.layoutVerifyOtp.verificationCode2.text}${activityBinding.layoutVerifyOtp.verificationCode3.text}" +
                                "${activityBinding.layoutVerifyOtp.verificationCode4.text}${activityBinding.layoutVerifyOtp.verificationCode5.text}${activityBinding.layoutVerifyOtp.verificationCode6.text}").toInt(),
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

        // Set up focus change for each EditText
        moveToNextEditText(
            activityBinding.layoutVerifyOtp.verificationCode1,
            activityBinding.layoutVerifyOtp.verificationCode2
        )
        moveToNextEditText(
            activityBinding.layoutVerifyOtp.verificationCode2,
            activityBinding.layoutVerifyOtp.verificationCode3
        )
        moveToNextEditText(
            activityBinding.layoutVerifyOtp.verificationCode3,
            activityBinding.layoutVerifyOtp.verificationCode4
        )
        moveToNextEditText(
            activityBinding.layoutVerifyOtp.verificationCode4,
            activityBinding.layoutVerifyOtp.verificationCode5
        )
        moveToNextEditText(
            activityBinding.layoutVerifyOtp.verificationCode5,
            activityBinding.layoutVerifyOtp.verificationCode6
        )
    }

    // Helper function to move focus
    private fun moveToNextEditText(currentEditText: EditText, nextEditText: EditText) {
        currentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    nextEditText.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}