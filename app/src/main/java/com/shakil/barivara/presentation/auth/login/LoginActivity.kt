package com.shakil.barivara.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.shakil.barivara.R
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.databinding.ActivityLoginBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.presentation.auth.registration.MobileRegVerificationActivity
import com.shakil.barivara.presentation.auth.registration.RegistrationActivity
import com.shakil.barivara.presentation.auth.forgotpassword.ForgotPasswordActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityLoginBinding
    private var firebaseAuth: FirebaseAuth? = null
    private var ux: UX? = null
    private var utilsForAll: UtilsForAll? = null
    private var loginWithStr: String? = null
    private var tools = Tools(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

    private val viewModel by viewModels<AuthViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            utilsForAll?.exitApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        initUI()
        loginWithStr = getString(R.string.mobile)
        bindUiWithComponents()

        viewModel.getData()
    }

    private fun initUI() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun bindUiWithComponents() {
        validation(arrayOf("mobileNumber"), arrayOf(getString(R.string.mobile_validation)))
        activityBinding.emailIdLayout.setOnClickListener {
            loginWithStr = getString(R.string.email)
            loginWith(loginWithStr)
            validation(
                arrayOf("email", "password"),
                arrayOf(
                    getString(R.string.email_validation),
                    getString(R.string.password_validation)
                )
            )
        }
        activityBinding.mobileLayout.setOnClickListener {
            loginWithStr = getString(R.string.mobile)
            loginWith(loginWithStr)
            validation(arrayOf("mobileNumber"), arrayOf(getString(R.string.mobile_validation)))
        }
        activityBinding.register.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegistrationActivity::class.java
                )
            )
        }
        activityBinding.login.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    if (loginWithStr == getString(R.string.email)) {
                        if (tools.validateEmailAddress(activityBinding.email.text.toString())) {
                            loginWithEmail()
                        } else {
                            Toasty.warning(
                                this@LoginActivity,
                                getString(R.string.not_a_valid_email_address),
                                Toast.LENGTH_LONG,
                                true
                            ).show()
                        }
                    } else if (loginWithStr == getString(R.string.mobile)) {
                        if (tools.isValidMobile(activityBinding.mobileNumber.text.toString())) {
                            loginWithMobile()
                        } else {
                            Toasty.warning(
                                this@LoginActivity,
                                getString(R.string.not_a_valid_mobile_number),
                                Toast.LENGTH_LONG,
                                true
                            ).show()
                        }
                    }
                } else {
                    Toasty.warning(
                        this@LoginActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                }
            }
        }
        activityBinding.forgotPassword.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    ForgotPasswordActivity::class.java
                )
            )
        }
    }

    private fun loginWithEmail() {
        ux?.getLoadingView()
        firebaseAuth?.signInWithEmailAndPassword(
            activityBinding.email.text.toString(), activityBinding.password.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(Constants.TAG + ":onComplete", getString(R.string.login_succcessful))
                Toasty.success(
                    this@LoginActivity,
                    getString(R.string.login_succcessful),
                    Toast.LENGTH_LONG,
                    true
                ).show()
                tools.setLoginPrefs(task, PrefManager(this@LoginActivity))
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                when (task.exception?.message) {
                    getString(R.string.firebase_password_not_valid_exception) -> {
                        Toasty.error(
                            this@LoginActivity,
                            getString(R.string.wrong_password),
                            Toast.LENGTH_LONG,
                            true
                        ).show()
                    }

                    getString(R.string.firebase_no_user_exception) -> {
                        Toasty.error(
                            this@LoginActivity,
                            getString(R.string.email_was_not_found_in_our_database),
                            Toast.LENGTH_LONG,
                            true
                        ).show()
                    }

                    else -> {
                        Toasty.error(
                            this@LoginActivity,
                            getString(R.string.login_unsucccessful),
                            Toast.LENGTH_LONG,
                            true
                        ).show()
                    }
                }
                Log.i(Constants.TAG + ":onComplete", getString(R.string.login_unsucccessful))
            }
            ux?.removeLoadingView()
        }
    }

    private fun loginWithMobile() {
        val intent = Intent(this@LoginActivity, MobileRegVerificationActivity::class.java)
        intent.putExtra("mobile", activityBinding.mobileNumber.text.toString())
        startActivity(intent)
    }

    private fun loginWith(registerWith: String?) {
        if (registerWith == getString(R.string.email)) {
            activityBinding.mainMobileRegistrationLayout.visibility = View.GONE
            activityBinding.mainEmailRegistrationLayout.visibility = View.VISIBLE
            activityBinding.emailIdLayout.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_background_filled_gender)
            activityBinding.EmailId.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                )
            )
            activityBinding.mobileLayout.setBackgroundResource(0)
            activityBinding.Mobile.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_green_800
                )
            )
            activityBinding.MobileIcon.setImageResource(R.drawable.ic_call_green)
            activityBinding.EmailIcon.setImageResource(R.drawable.ic_email_white)
        } else {
            activityBinding.mainEmailRegistrationLayout.visibility = View.GONE
            activityBinding.mainMobileRegistrationLayout.visibility = View.VISIBLE
            activityBinding.mobileLayout.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_background_filled_gender)
            activityBinding.emailIdLayout.setBackgroundResource(0)
            activityBinding.EmailId.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_green_800
                )
            )
            activityBinding.Mobile.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                )
            )
            activityBinding.MobileIcon.setImageResource(R.drawable.ic_call_white)
            activityBinding.EmailIcon.setImageResource(R.drawable.ic_email_green)
        }
    }

    private fun validation(resNames: Array<String>, validationMessages: Array<String>) {
        validation.setEditTextIsNotEmpty(resNames, validationMessages)
    }
}