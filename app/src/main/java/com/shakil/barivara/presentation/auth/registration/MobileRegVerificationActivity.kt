package com.shakil.barivara.presentation.auth.registration

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
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
            if (!TextUtils.isEmpty(activityBinding.verificationCode.text.toString())) {
                if (activityBinding.verificationCode.text.toString().length < 6) {
                    Toasty.error(
                        this@MobileRegVerificationActivity,
                        getString(R.string.not_valid_otp_or_code),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                } else {
                    //verifying the code entered manually
                    verifyVerificationCode(activityBinding.verificationCode.text.toString())
                }
            } else {
                activityBinding.verificationCode.requestFocus()
                activityBinding.verificationCode.error =
                    getString(R.string.otp_or_code_can_not_be_empty)
            }
        }
    }

    private fun initObservers() {
        viewModel.getVerifyOtpResponse().observe(this) { verifyOtpBaseResponse ->
            if (verifyOtpBaseResponse.verifyOtpResponse.accessToken != null) {
                tools.setLoginPrefs(
                    mobileNumber,
                    verifyOtpBaseResponse.verifyOtpResponse.userId,
                    verifyOtpBaseResponse.verifyOtpResponse.accessToken ?: "",
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

    private fun verifyVerificationCode(code: String) {
        if (tools.hasConnection()) {
            viewModel.verifyOtp(mobileNumber, code.trim().toInt())
        } else {
            Snackbar.make(
                findViewById(R.id.parent),
                getString(R.string.no_internet_title),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}