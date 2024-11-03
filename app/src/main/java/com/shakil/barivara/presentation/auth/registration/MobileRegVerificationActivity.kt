package com.shakil.barivara.presentation.auth.registration

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.auth.OtpType
import com.shakil.barivara.databinding.ActivityMobileRegVerificationBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.moveToNextEditText
import com.shakil.barivara.utils.moveToPreviousEditText
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class MobileRegVerificationActivity : BaseActivity<ActivityMobileRegVerificationBinding>() {
    private lateinit var activityBinding: ActivityMobileRegVerificationBinding
    private lateinit var mobileNumber: String
    private var otpResendTime: Long = 0
    private var tools = Tools(this)
    private lateinit var ux: UX
    private lateinit var prefManager: PrefManager
    private val viewModel by viewModels<AuthViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_mobile_reg_verification

    override fun setVariables(dataBinding: ActivityMobileRegVerificationBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        activityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_mobile_reg_verification)
        initUI()
        getIntentData()
        bindUIWithComponents()
        initObservers()
        startOtpTimer(otpResendTime)
    }

    private fun initUI() {
        ux = UX(this)
        prefManager = PrefManager(this)
    }

    private fun getIntentData() {
        if (intent.getStringExtra(Constants.mUserMobile) != null) {
            mobileNumber = intent.getStringExtra(Constants.mUserMobile) ?: ""
            otpResendTime = intent.getLongExtra(Constants.mOtpResendTime, 0)
        }
    }

    private fun bindUIWithComponents() {
        activityBinding.sentCodeHintText.text =
            getString(R.string.sent_you_code_on_your_number, mobileNumber)
        activityBinding.verify.setOnClickListener {
            if (!TextUtils.isEmpty(
                    "${activityBinding.verificationCode1.text}${activityBinding.verificationCode2.text}${activityBinding.verificationCode3.text}" +
                            "${activityBinding.verificationCode4.text}${activityBinding.verificationCode5.text}${activityBinding.verificationCode6.text}"
                )
            ) {
                if (("${activityBinding.verificationCode1.text}${activityBinding.verificationCode2.text}${activityBinding.verificationCode3.text}" +
                            "${activityBinding.verificationCode4.text}${activityBinding.verificationCode5.text}${activityBinding.verificationCode6.text}").length < 6
                ) {
                    Toasty.error(
                        this@MobileRegVerificationActivity,
                        getString(R.string.not_valid_otp_or_code),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                } else {
                    verifyVerificationCode()
                }
            } else {
                Toasty.warning(
                    this,
                    getString(R.string.otp_or_code_can_not_be_empty),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Set up focus change for each EditText to next EditText
        activityBinding.verificationCode1.moveToNextEditText(activityBinding.verificationCode2)
        activityBinding.verificationCode2.moveToNextEditText(activityBinding.verificationCode3)
        activityBinding.verificationCode3.moveToNextEditText(activityBinding.verificationCode4)
        activityBinding.verificationCode4.moveToNextEditText(activityBinding.verificationCode5)
        activityBinding.verificationCode5.moveToNextEditText(activityBinding.verificationCode6)
        activityBinding.verificationCode6.moveToNextEditText(activityBinding.verificationCode6)

        // Set up focus change for each EditText to previous EditText
        activityBinding.verificationCode2.moveToPreviousEditText(activityBinding.verificationCode1)
        activityBinding.verificationCode3.moveToPreviousEditText(activityBinding.verificationCode2)
        activityBinding.verificationCode4.moveToPreviousEditText(activityBinding.verificationCode3)
        activityBinding.verificationCode5.moveToPreviousEditText(activityBinding.verificationCode4)
        activityBinding.verificationCode6.moveToPreviousEditText(activityBinding.verificationCode5)
    }

    private fun initObservers() {
        viewModel.getVerifyOtpResponse().observe(this) { verifyOtpBaseResponse ->
            if (verifyOtpBaseResponse.accessToken != null) {
                tools.setLoginPrefs(
                    mobileNumber,
                    verifyOtpBaseResponse,
                    prefManager = prefManager
                )
                val intent = Intent(
                    this, HomeActivity::class.java
                )
                startActivity(intent)
            }
        }

        viewModel.getVerifyOtpErrorResponse().observe(this) { verifyOtpErrorResponse ->
            Toasty.warning(this, verifyOtpErrorResponse.message).show()
        }

        viewModel.getSendOtpResponse().observe(this) { sendOtpResponse ->
            if ((sendOtpResponse.sendOtpResponse.otpValidationTime ?: 0) > 0) {
                Toasty.success(this, sendOtpResponse.message).show()
                startOtpTimer(sendOtpResponse.sendOtpResponse.otpValidationTime ?: 0)
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

    private fun startOtpTimer(durationSeconds: Long) {
        viewModel.isOtpResendable.postValue(false)
        activityBinding.resendOtp.visibility = View.GONE
        activityBinding.didReceiveCode.visibility = View.VISIBLE
        val durationMs = durationSeconds * 1000

        object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                activityBinding.didReceiveCode.text = getString(
                    R.string.did_not_receive_a_code_x,
                    String.format("%02d:%02d", minutes, seconds)
                )
            }

            override fun onFinish() {
                viewModel.isOtpResendable.postValue(true)
                activityBinding.resendOtp.visibility = View.VISIBLE
                activityBinding.didReceiveCode.visibility = View.GONE
                activityBinding.resendOtp.setOnClickListener {
                    if (viewModel.isOtpResendable.value == true) {
                        viewModel.sendOtp(mobileNumber, OtpType.OTP.value)
                    }
                }
            }
        }.start()
    }

    private fun verifyVerificationCode() {
        if (tools.hasConnection()) {
            viewModel.verifyOtp(
                mobileNumber,
                ("${activityBinding.verificationCode1.text}${activityBinding.verificationCode2.text}${activityBinding.verificationCode3.text}" +
                        "${activityBinding.verificationCode4.text}${activityBinding.verificationCode5.text}${activityBinding.verificationCode6.text}").trim()
                    .toInt(),
                OtpType.OTP.value
            )
        } else {
            Snackbar.make(
                findViewById(R.id.parent),
                getString(R.string.no_internet_title),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}