package com.shakil.barivara.presentation.profile

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.data.model.user.UserInfo
import com.shakil.barivara.databinding.ActivityProfileBinding
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.UX
import com.shakil.barivara.utils.Validation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    private lateinit var activityBinding: ActivityProfileBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var customAdManager = CustomAdManager(this)
    private lateinit var prefManager: PrefManager
    private lateinit var ux: UX
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
        customAdManager = CustomAdManager(this)
        prefManager = PrefManager(this)
        ux = UX(this)
    }

    private fun initObservers() {
        viewModel.getUserInfo().observe(this) { userInfo ->
            userInfo?.let {
                activityBinding.Name.setText(
                    userInfo.name
                )
                activityBinding.Mobile.setText(
                    userInfo.phone
                )
                activityBinding.Email.setText(
                    userInfo.email
                )
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
        customAdManager.generateAd(activityBinding.adView)
        validation.setEditTextIsNotEmpty(
            arrayOf("Name", "Address", "DOB"), arrayOf(
                getString(R.string.validation_name),
                getString(R.string.validation_address),
                getString(R.string.validation_dob)
            )
        )
        activityBinding.toolBar.setNavigationOnClickListener { finish() }

        activityBinding.editIcon.setOnClickListener {
            changeVisibilityAndFocusable(true, View.GONE, View.VISIBLE, true)
            changeEditTextBack(
                arrayOf(
                    activityBinding.Name,
                    activityBinding.Address,
                    activityBinding.DOB
                ), R.drawable.edit_text_back_green
            )
        }
        activityBinding.cancelEdit.setOnClickListener {
            changeVisibilityAndFocusable(false, View.VISIBLE, View.GONE, false)
            changeEditTextBack(
                arrayOf(
                    activityBinding.Name,
                    activityBinding.Address,
                    activityBinding.DOB
                ), R.drawable.edit_text_back
            )
        }
        activityBinding.saveOrUpdate.setOnClickListener {
            if (validation.isValid) {
                val user = UserInfo(
                    name = activityBinding.Name.text.toString(),
                    email = activityBinding.Email.text.toString(),
                )

            }
        }
    }

    private fun changeVisibilityAndFocusable(
        editTextVisibility: Boolean,
        editIconVisibility: Int,
        saveCancelLayoutVisibility: Int,
        isFocusable: Boolean
    ) {
        activityBinding.editIcon.visibility = editIconVisibility
        activityBinding.saveCancelLayout.visibility = saveCancelLayoutVisibility
        activityBinding.Name.isFocusable = editTextVisibility
        activityBinding.Address.isFocusable = editTextVisibility
        activityBinding.DOB.isFocusable = editTextVisibility
        activityBinding.Name.isFocusableInTouchMode = isFocusable
        activityBinding.Address.isFocusableInTouchMode = isFocusable
        activityBinding.DOB.isFocusableInTouchMode = isFocusable
    }

    private fun changeEditTextBack(editTexts: Array<EditText>, backDrawable: Int) {
        for (editText in editTexts) {
            editText.setBackgroundResource(backDrawable)
        }
    }
}