package com.shakil.barivara.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityProfileBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.User;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding activityBinding;
    private Validation validation;
    private final Map<String, String[]> hashMap = new HashMap();
    private FirebaseCrudHelper firebaseCrudHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        //region init objects and perform all UI operations
        initUI();
        bindUiWithComponents();
        //endregion
    }

    //region init objects
    private void initUI() {
        validation = new Validation(this, hashMap);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
    }
    //endregion

    //region perform all UI operations
    private void bindUiWithComponents() {
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"Name", "Address", "DOB"},
                new String[]{getString(R.string.validation_name), getString(R.string.validation_address)
                        ,getString(R.string.validation_dob)});
        //endregion

        activityBinding.backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        activityBinding.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityBinding.Name.setFocusable(true);
                activityBinding.Address.setFocusable(true);
                activityBinding.DOB.setFocusable(true);
                activityBinding.Name.setFocusableInTouchMode(true);
                activityBinding.Address.setFocusableInTouchMode(true);
                activityBinding.DOB.setFocusableInTouchMode(true);
                activityBinding.editIcon.setVisibility(View.GONE);
                activityBinding.saveCancelLayout.setVisibility(View.VISIBLE);
            }
        });

        activityBinding.cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityBinding.editIcon.setVisibility(View.VISIBLE);
                activityBinding.saveCancelLayout.setVisibility(View.GONE);
                activityBinding.Name.setFocusable(false);
                activityBinding.Address.setFocusable(false);
                activityBinding.DOB.setFocusable(false);
            }
        });

        activityBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation.isValid()){
                    User user = new User();
                    firebaseCrudHelper.update(user, "user", "");
                }
            }
        });
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
    //endregion
}