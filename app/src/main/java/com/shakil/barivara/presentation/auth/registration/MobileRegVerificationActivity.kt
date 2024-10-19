package com.shakil.barivara.presentation.auth.registration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
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
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class MobileRegVerificationActivity : BaseActivity<ActivityMobileRegVerificationBinding>() {
    private lateinit var activityBinding: ActivityMobileRegVerificationBinding
    private lateinit var mobileNumber: String
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
        activityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_mobile_reg_verification)
        initUI()
        getIntentData()
        bindUIWithComponents()
        initObservers()
    }

    private fun initUI() {
        ux = UX(this)
        prefManager = PrefManager(this)
    }

    private fun getIntentData() {
        if (intent.getStringExtra(Constants.mUserMobile) != null) {
            mobileNumber = intent.getStringExtra(Constants.mUserMobile) ?: ""
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

        // Set up focus change for each EditText
        moveToNextEditText(
            activityBinding.verificationCode1,
            activityBinding.verificationCode2,
        )
        moveToNextEditText(
            activityBinding.verificationCode2,
            activityBinding.verificationCode3,
        )
        moveToNextEditText(
            activityBinding.verificationCode3,
            activityBinding.verificationCode4,
        )
        moveToNextEditText(
            activityBinding.verificationCode4,
            activityBinding.verificationCode5,
        )
        moveToNextEditText(
            activityBinding.verificationCode5,
            activityBinding.verificationCode6,
        )
        moveToNextEditText(
            activityBinding.verificationCode6,
            activityBinding.verificationCode6,
        )
    }

    private fun initObservers() {
        viewModel.getVerifyOtpResponse().observe(this) { verifyOtpBaseResponse ->
            if (verifyOtpBaseResponse.accessToken != null) {
                tools.setLoginPrefs(
                    mobileNumber,
                    verifyOtpBaseResponse.userInfo,
                    verifyOtpBaseResponse.accessToken ?: "",
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

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }
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

    // Helper function to move focus
    private fun moveToNextEditText(
        currentEditText: EditText,
        nextEditText: EditText,
    ) {
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