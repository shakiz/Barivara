package com.shakil.barivara.activities.profile

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityProfileBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.User
import com.shakil.barivara.utils.CustomAdManager
import com.shakil.barivara.utils.Validation
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityProfileBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private var customAdManager = CustomAdManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initUI()
        bindUiWithComponents()
    }

    private fun initUI() {
        validation = Validation(this, hashMap)
        firebaseCrudHelper = FirebaseCrudHelper(this)
        customAdManager = CustomAdManager(this)
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
        activityBinding.toolBar.setNavigationOnClickListener { onBackPressed() }
        firebaseCrudHelper.fetchProfile("user") { user ->
            if (user != null) {
                if (user.name != null && user.name.isNotEmpty()) activityBinding.Name.setText(user.name)
                if (user.dob != null && user.dob.isNotEmpty()) activityBinding.DOB.setText(user.dob)
            }
        }
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
                val user = User(
                    UUID.randomUUID().toString(),
                    "",
                    activityBinding.Name.text.toString(),
                    "",
                    activityBinding.Mobile.text.toString(),
                    activityBinding.Email.text.toString(),
                    activityBinding.DOB.text.toString()
                )
                firebaseCrudHelper.add(user, "user")
                changeVisibilityAndFocusable(false, View.VISIBLE, View.GONE, false)
                changeEditTextBack(
                    arrayOf(
                        activityBinding.Name,
                        activityBinding.Address,
                        activityBinding.DOB
                    ), R.drawable.edit_text_back
                )
                firebaseCrudHelper.fetchProfile("user") { user ->
                    if (user != null) {
                        if (user.name != null && !user.name.isEmpty()) activityBinding.Name.setText(
                            user.name
                        )
                        if (user.dob != null && !user.dob.isEmpty()) activityBinding.DOB.setText(
                            user.dob
                        )
                    }
                }
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