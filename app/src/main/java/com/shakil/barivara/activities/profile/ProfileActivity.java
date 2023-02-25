package com.shakil.barivara.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityProfileBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.User;
import com.shakil.barivara.utils.CustomAdManager;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding activityBinding;
    private Validation validation;
    private final Map<String, String[]> hashMap = new HashMap();
    private FirebaseCrudHelper firebaseCrudHelper;
    private CustomAdManager customAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        initUI();
        bindUiWithComponents();
    }

    private void initUI() {
        validation = new Validation(this, hashMap);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        customAdManager = new CustomAdManager(this);
    }

    private void bindUiWithComponents() {
        customAdManager.generateAd(activityBinding.adView);

        validation.setEditTextIsNotEmpty(new String[]{"Name", "Address", "DOB"},
                new String[]{getString(R.string.validation_name), getString(R.string.validation_address)
                        ,getString(R.string.validation_dob)});

        activityBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseCrudHelper.fetchProfile("user", new FirebaseCrudHelper.onProfileFetch() {
            @Override
            public void onFetch(User user) {
                if (user != null){
                    if (user.getName() != null && !user.getName().isEmpty()) activityBinding.Name.setText(user.getName());
                    if (user.getDOB() != null && !user.getDOB().isEmpty()) activityBinding.DOB.setText(user.getDOB());
                }
            }
        });

        activityBinding.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibilityAndFocusable(true, View.GONE, View.VISIBLE, true);
                changeEditTextBack(new EditText[]{activityBinding.Name, activityBinding.Address, activityBinding.DOB}, R.drawable.edit_text_back_green);
            }
        });

        activityBinding.cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibilityAndFocusable(false, View.VISIBLE, View.GONE, false);
                changeEditTextBack(new EditText[]{activityBinding.Name, activityBinding.Address, activityBinding.DOB}, R.drawable.edit_text_back);
            }
        });

        activityBinding.saveOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()){
                    User user = new User(UUID.randomUUID().toString(), "", activityBinding.Name.getText().toString(),
                            "", activityBinding.Mobile.getText().toString(), activityBinding.Email.getText().toString(), activityBinding.DOB.getText().toString());
                    firebaseCrudHelper.add(user, "user");
                    changeVisibilityAndFocusable(false, View.VISIBLE, View.GONE, false);
                    changeEditTextBack(new EditText[]{activityBinding.Name, activityBinding.Address, activityBinding.DOB}, R.drawable.edit_text_back);
                    firebaseCrudHelper.fetchProfile("user", new FirebaseCrudHelper.onProfileFetch() {
                        @Override
                        public void onFetch(User user) {
                            if (user != null){
                                if (user.getName() != null && !user.getName().isEmpty()) activityBinding.Name.setText(user.getName());
                                if (user.getDOB() != null && !user.getDOB().isEmpty()) activityBinding.DOB.setText(user.getDOB());
                            }
                        }
                    });
                }
            }
        });
    }

    private void changeVisibilityAndFocusable(boolean editTextVisibility, int editIconVisibility, int saveCancelLayoutVisibility, boolean isFocusable){
        activityBinding.editIcon.setVisibility(editIconVisibility);
        activityBinding.saveCancelLayout.setVisibility(saveCancelLayoutVisibility);
        activityBinding.Name.setFocusable(editTextVisibility);
        activityBinding.Address.setFocusable(editTextVisibility);
        activityBinding.DOB.setFocusable(editTextVisibility);
        activityBinding.Name.setFocusableInTouchMode(isFocusable);
        activityBinding.Address.setFocusableInTouchMode(isFocusable);
        activityBinding.DOB.setFocusableInTouchMode(isFocusable);
    }

    private void changeEditTextBack(EditText[] editTexts, int backDrawable){
        for (EditText editText : editTexts) {
            editText.setBackgroundResource(backDrawable);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
}