package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityPasswordLoginBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class PasswordLoginActivity : BaseActivity<ActivityPasswordLoginBinding>() {
    private lateinit var activityBinding: ActivityPasswordLoginBinding
    private lateinit var ux: UX
    @Inject
    lateinit var utilsForAll: UtilsForAll
    @Inject
    lateinit var prefManager: PrefManager
    private lateinit var tools: Tools
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

    private val viewModel by viewModels<AuthViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@PasswordLoginActivity, LoginSelectionActivity::class.java))
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_password_login

    override fun setVariables(dataBinding: ActivityPasswordLoginBinding) {
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
        tools = Tools(this)

        validation.setEditTextIsNotEmpty(
            arrayOf("mobileNumber", "password"), arrayOf(
                getString(R.string.mobile_validation),
                getString(R.string.password_validation)
            )
        )
    }

    private fun initObservers() {
        viewModel.getVerifyOtpResponse().observe(this) { passwordLoginResponse ->
            if (passwordLoginResponse.accessToken != null) {
                tools.setLoginPrefs(
                    activityBinding.mobileNumber.text.toString(),
                    passwordLoginResponse,
                    prefManager = prefManager
                )
                val intent = Intent(
                    this, HomeActivity::class.java
                )
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

    private fun initListeners() {
        activityBinding.tvSetupPassword.setOnClickListener {
            startActivity(Intent(this@PasswordLoginActivity, PasswordSetupActivity::class.java))
        }

        activityBinding.login.setOnClickListener {
            if (validation.isValid) {
                if (activityBinding.mobileNumber.text.length == 11) {
                    utilsForAll.hideSoftKeyboard(this)
                    viewModel.passwordLogin(
                        prefManager.getString(mAccessToken),
                        activityBinding.mobileNumber.text.toString(),
                        activityBinding.password.text.toString(),
                    )
                } else {
                    Toasty.warning(
                        this@PasswordLoginActivity,
                        getString(R.string.mobile_validation),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                }
            }
        }
    }
}