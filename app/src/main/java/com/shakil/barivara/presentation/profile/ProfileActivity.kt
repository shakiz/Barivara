package com.shakil.barivara.presentation.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.user.UserInfo
import com.shakil.barivara.databinding.ActivityProfileBinding
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.Constants.mUserMobile
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.Tools.Companion.hideKeyboard
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    private lateinit var activityBinding: ActivityProfileBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private lateinit var prefManager: PrefManager
    private lateinit var ux: UX
    private lateinit var tools: Tools
    private val viewModel by viewModels<UserViewModel>()
    override val layoutResourceId: Int
        get() = R.layout.activity_profile

    override fun setVariables(dataBinding: ActivityProfileBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initUI()
        bindUiWithComponents()
        initObservers()
        viewModel.getProfile(prefManager.getString(mAccessToken))
    }

    private fun initUI() {
        validation = Validation(this, hashMap)
        prefManager = PrefManager(this)
        ux = UX(this)
        tools = Tools(this)
    }

    private fun initObservers() {
        viewModel.getUserInfo().observe(this) { userInfo ->
            userInfo?.let {
                activityBinding.name.setText(
                    userInfo.name
                )
                activityBinding.mobile.setText(
                    userInfo.phone
                )
                activityBinding.email.setText(
                    userInfo.email
                )
            }
        }

        viewModel.getUpdateProfileResponse().observe(this) { response ->
            if (response.statusCode == 200) {
                Toasty.success(
                    this,
                    getString(R.string.profile_updated_successfully),
                    Toasty.LENGTH_SHORT
                ).show()
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

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("Name", "Email"), arrayOf(
                getString(R.string.validation_name),
                getString(R.string.validation_email),
            )
        )
        activityBinding.toolBar.setNavigationOnClickListener { finish() }

        activityBinding.saveOrUpdate.setOnClickListener {
            if (validation.isValid) {
                hideKeyboard(this)
                val user = UserInfo(
                    userId = prefManager.getInt(mUserId),
                    name = activityBinding.name.text.toString(),
                    email = activityBinding.email.text.toString(),
                    phone = prefManager.getString(mUserMobile)
                )
                viewModel.updateProfile(user, prefManager.getString(mAccessToken))
            }
        }
    }
}