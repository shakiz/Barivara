package com.shakil.barivara.presentation.auth.forgotpassword

import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityForgotPasswordBinding
import com.shakil.barivara.presentation.auth.AuthViewModel
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var ux: UX
    private lateinit var prefManager: PrefManager
    private val viewModel by viewModels<AuthViewModel>()

    override val layoutResourceId: Int
        get() = R.layout.activity_forgot_password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        bindUiWithComponents()
        initListeners()
        initObservers()
    }

    override fun setVariables(dataBinding: ActivityForgotPasswordBinding) {
        activityForgotPasswordBinding = dataBinding
    }

    private fun initUI() {
        ux = UX(this)
        prefManager = PrefManager(this)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("oldPassword", "newPassword", "reEnterNewPassword"),
            arrayOf(
                getString(R.string.old_password_validation),
                getString(R.string.new_password_validation),
                getString(R.string.re_enter_password_validation)
            )
        )
    }

    private fun initListeners() {
        activityForgotPasswordBinding.changePassword.setOnClickListener {
            if (validation.isValid) {
                viewModel.changePassword(
                    prefManager.getString(Constants.mAccessToken),
                    activityForgotPasswordBinding.oldPassword.text.toString(),
                    activityForgotPasswordBinding.newPassword.text.toString(),
                    activityForgotPasswordBinding.reEnterNewPassword.text.toString()
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                ux.getLoadingView()
            } else {
                ux.removeLoadingView()
            }
        }

        viewModel.getChangePasswordResponse().observe(this) {
            if (it.statusCode == 200) {
                Toasty.success(this, getString(R.string.password_changed_successfully)).show()
            }
        }

        viewModel.getChangePasswordErrorResponse().observe(this) {
            Toasty.error(this, it.message).show()
        }
    }
}