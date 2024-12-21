package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.auth.OtpType
import com.shakil.barivara.data.model.auth.OtpUIState
import com.shakil.barivara.databinding.ActivityPasswordSetupBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import com.shakil.barivara.utils.moveToNextEditText
import com.shakil.barivara.utils.moveToPreviousEditText
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class PasswordSetupActivity : BaseActivity<ActivityPasswordSetupBinding>() {
    private lateinit var activityBinding: ActivityPasswordSetupBinding
    private lateinit var ux: UX
    private lateinit var utilsForAll: UtilsForAll

    @Inject
    lateinit var prefManager: PrefManager
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        initUI()
        initObservers()
        initListeners()
        addValidation()
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

        viewModel.getSendOtpResponse().observe(this) { sendOtpResponse ->
            if (sendOtpResponse.sendOtpResponse.otpValidationTime > 0) {
                viewModel.otpUiState.postValue(OtpUIState.VERIFY_OTP)
                Toasty.success(this, sendOtpResponse.message).show()
                activityBinding.layoutVerifyOtp.sentCodeHintText.text = getString(
                    R.string.sent_you_code_on_your_number,
                    activityBinding.layoutSendOtp.mobileNumber.text
                )
            }
        }

        viewModel.getSendOtpErrorResponse().observe(this) { sendOtpErrorResponse ->
            Toasty.warning(this, sendOtpErrorResponse.message).show()
        }

        viewModel.getVerifyOtpResponse().observe(this) { loginBaseResponse ->
            if (loginBaseResponse.accessToken != null) {
                prefManager[mAccessToken] = loginBaseResponse.accessToken
                viewModel.otpUiState.postValue(OtpUIState.SETUP_PASS)
                Toasty.success(this, getString(R.string.otp_verified)).show()
            }
        }

        viewModel.getVerifyOtpErrorResponse().observe(this) { verifyOtpErrorResponse ->
            Toasty.warning(this, verifyOtpErrorResponse.message).show()
        }

        viewModel.getPasswordSetupResponse().observe(this) { passwordSetupResponse ->
            if (passwordSetupResponse.statusCode == 200) {
                Toasty.success(this, getString(R.string.password_setup_completed)).show()
                startActivity(Intent(this, PasswordLoginActivity::class.java))
            }
        }

        viewModel.getVerifyOtpErrorResponse().observe(this) { verifyOtpErrorResponse ->
            Toasty.warning(this, verifyOtpErrorResponse.message).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
    }

    private fun addValidation() {
        when (viewModel.otpUiState.value) {
            OtpUIState.SEND_OTP -> {
                validation = Validation(this, hashMap)
                validation.setEditTextIsNotEmpty(
                    arrayOf("mobileNumber"),
                    arrayOf(getString(R.string.mobile_validation))
                )
            }

            OtpUIState.VERIFY_OTP -> {
                validation = Validation(this, hashMap)
                validation.setEditTextIsNotEmpty(
                    arrayOf(
                        "verificationCode1",
                        "verificationCode2",
                        "verificationCode3",
                        "verificationCode4",
                        "verificationCode5",
                        "verificationCode6"
                    ), arrayOf(
                        getString(R.string.mobile_validation),
                        getString(R.string.otp_or_code_can_not_be_empty),
                        getString(R.string.otp_or_code_can_not_be_empty),
                        getString(R.string.otp_or_code_can_not_be_empty),
                        getString(R.string.otp_or_code_can_not_be_empty),
                        getString(R.string.otp_or_code_can_not_be_empty),
                        getString(R.string.otp_or_code_can_not_be_empty)
                    )
                )
            }

            OtpUIState.SETUP_PASS -> {
                validation = Validation(this, hashMap)
                validation.setEditTextIsNotEmpty(
                    arrayOf(
                        "password",
                        "reEnterPassword",
                    ), arrayOf(
                        getString(R.string.password_validation),
                        getString(R.string.re_enter_password_validation),
                    )
                )
            }

            else -> {
                //empty impl
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
                        ("${activityBinding.layoutVerifyOtp.verificationCode1.text}${activityBinding.layoutVerifyOtp.verificationCode2.text}${activityBinding.layoutVerifyOtp.verificationCode3.text}" + "${activityBinding.layoutVerifyOtp.verificationCode4.text}${activityBinding.layoutVerifyOtp.verificationCode5.text}${activityBinding.layoutVerifyOtp.verificationCode6.text}").toInt(),
                        OtpType.SET_PASS.value
                    )
                }

                else -> {
                    viewModel.setupPassword(
                        activityBinding.layoutSetupPassword.password.text.toString(),
                        activityBinding.layoutSetupPassword.reEnterPassword.text.toString(),
                    )
                }
            }
        }

        // Set up focus change for each EditText to next EditText
        activityBinding.layoutVerifyOtp.verificationCode1.moveToNextEditText(activityBinding.layoutVerifyOtp.verificationCode2)
        activityBinding.layoutVerifyOtp.verificationCode2.moveToNextEditText(activityBinding.layoutVerifyOtp.verificationCode3)
        activityBinding.layoutVerifyOtp.verificationCode3.moveToNextEditText(activityBinding.layoutVerifyOtp.verificationCode4)
        activityBinding.layoutVerifyOtp.verificationCode4.moveToNextEditText(activityBinding.layoutVerifyOtp.verificationCode5)
        activityBinding.layoutVerifyOtp.verificationCode5.moveToNextEditText(activityBinding.layoutVerifyOtp.verificationCode6)
        activityBinding.layoutVerifyOtp.verificationCode6.moveToNextEditText(activityBinding.layoutVerifyOtp.verificationCode6)

        // Set up focus change for each EditText to previous EditText
        activityBinding.layoutVerifyOtp.verificationCode2.moveToPreviousEditText(activityBinding.layoutVerifyOtp.verificationCode1)
        activityBinding.layoutVerifyOtp.verificationCode3.moveToPreviousEditText(activityBinding.layoutVerifyOtp.verificationCode2)
        activityBinding.layoutVerifyOtp.verificationCode4.moveToPreviousEditText(activityBinding.layoutVerifyOtp.verificationCode3)
        activityBinding.layoutVerifyOtp.verificationCode5.moveToPreviousEditText(activityBinding.layoutVerifyOtp.verificationCode4)
        activityBinding.layoutVerifyOtp.verificationCode6.moveToPreviousEditText(activityBinding.layoutVerifyOtp.verificationCode5)
    }
}