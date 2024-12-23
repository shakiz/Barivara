package com.shakil.barivara.presentation.auth.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityRegistrationBinding
import com.shakil.barivara.presentation.auth.login.LoginSelectionActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import es.dmoral.toasty.Toasty

class RegistrationActivity : BaseActivity<ActivityRegistrationBinding>() {
    private lateinit var activityBinding: ActivityRegistrationBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var ux: UX? = null
    private val tools = Tools(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    override val layoutResourceId: Int
        get() = R.layout.activity_registration

    override fun setVariables(dataBinding: ActivityRegistrationBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        bindUiWithComponents()
    }

    private fun initUI() {
        ux = UX(this)
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("email", "password"),
            arrayOf(getString(R.string.email_validation), getString(R.string.password_validation))
        )
        activityBinding.login.setOnClickListener {
            startActivity(
                Intent(
                    this@RegistrationActivity,
                    LoginSelectionActivity::class.java
                )
            )
        }
        activityBinding.signUp.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    if (tools.validateEmailAddress(activityBinding.email.text.toString())) {
                        if (activityBinding.password.text.toString().length >= 6) {
                            registerWithEmailPass()
                        } else {
                            Toasty.warning(
                                this@RegistrationActivity,
                                getString(R.string.password_must_be_six_character),
                                Toast.LENGTH_LONG,
                                true
                            ).show()
                        }
                    } else {
                        Toasty.warning(
                            this@RegistrationActivity,
                            getString(R.string.not_a_valid_email_address),
                            Toast.LENGTH_LONG,
                            true
                        ).show()
                    }
                }
            } else {
                Toasty.warning(
                    this@RegistrationActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_LONG,
                    true
                ).show()
            }
        }
    }

    private fun registerWithEmailPass() {
        ux?.getLoadingView()
        firebaseAuth.createUserWithEmailAndPassword(
            activityBinding.email.text.toString(),
            activityBinding.password.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(Constants.TAG + ":onComplete", getString(R.string.registration_succcessful))
                Toasty.success(
                    this@RegistrationActivity,
                    getString(R.string.registration_succcessful),
                    Toast.LENGTH_LONG,
                    true
                ).show()
                startActivity(Intent(this@RegistrationActivity, LoginSelectionActivity::class.java))
            } else {
                if (task.exception?.message == getString(R.string.firebase_user_exists_exception)) {
                    Toasty.error(
                        this@RegistrationActivity,
                        getString(R.string.user_already_exists),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                } else {
                    Toasty.error(
                        this@RegistrationActivity,
                        getString(R.string.registration_unsucccessful),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                }
                Log.i(Constants.TAG + ":onComplete", getString(R.string.registration_unsucccessful))
            }
            ux?.removeLoadingView()
        }
    }
}