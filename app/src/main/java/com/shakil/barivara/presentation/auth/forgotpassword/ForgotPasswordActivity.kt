package com.shakil.barivara.presentation.auth.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityForgotPasswordBinding
import com.shakil.barivara.presentation.auth.login.LoginActivity
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import es.dmoral.toasty.Toasty

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var ux: UX
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForgotPasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        initUI()
        bindUiWithComponents()
    }

    private fun initUI() {
        ux = UX(this)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("email"),
            arrayOf(getString(R.string.email_validation))
        )
        activityForgotPasswordBinding.changePassword.setOnClickListener {
            if (validation.isValid) {
                ux.getLoadingView()
                firebaseAuth?.sendPasswordResetEmail(activityForgotPasswordBinding.email.text.toString())
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(Constants.TAG, "Reset pass mail sent.")
                            Toasty.info(
                                this@ForgotPasswordActivity,
                                getString(R.string.email_sent),
                                Toast.LENGTH_LONG,
                                true
                            ).show()
                            activityForgotPasswordBinding.changePasswordLayout.visibility =
                                View.GONE
                            activityForgotPasswordBinding.afterChangePasswordLayout.visibility =
                                View.VISIBLE
                        } else {
                            Log.i(Constants.TAG, "Reset pass error : " + task.isSuccessful)
                            if (task.exception?.message == getString(R.string.firebase_no_user_exception)) {
                                Toasty.error(
                                    this@ForgotPasswordActivity,
                                    getString(R.string.email_was_not_found_in_our_database),
                                    Toast.LENGTH_LONG,
                                    true
                                ).show()
                            } else {
                                Toasty.error(
                                    this@ForgotPasswordActivity,
                                    getString(R.string.failed_to_sent_reset_email),
                                    Toast.LENGTH_LONG,
                                    true
                                ).show()
                            }
                            activityForgotPasswordBinding.changePasswordLayout.visibility =
                                View.VISIBLE
                            activityForgotPasswordBinding.afterChangePasswordLayout.visibility =
                                View.GONE
                        }
                        ux.removeLoadingView()
                    }
            }
        }
        activityForgotPasswordBinding.login.setOnClickListener {
            startActivity(
                Intent(
                    this@ForgotPasswordActivity,
                    LoginActivity::class.java
                )
            )
        }
    }
}