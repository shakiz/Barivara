package com.shakil.barivara.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityRegistrationBinding;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding activityBinding;
    private FirebaseAuth firebaseAuth;
    private UX ux;
    private Tools tools;
    private Validation validation;
    private final Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        initUI();
        bindUiWithComponents();
    }

    private void initUI() {
        firebaseAuth = FirebaseAuth.getInstance();
        ux = new UX(this);
        validation = new Validation(this, hashMap);
        tools = new Tools(this);
    }

    private void bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(new String[]{"email", "password"},
                new String[]{getString(R.string.email_validation), getString(R.string.password_validation)});

        activityBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        activityBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()) {
                    if (tools.hasConnection()) {
                        if (tools.validateEmailAddress(activityBinding.email.getText().toString())) {
                            if (activityBinding.password.getText().toString().length() >= 6) {
                                registerWithEmailPass();
                            } else {
                                Toasty.warning(RegistrationActivity.this, getString(R.string.password_must_be_six_character), Toast.LENGTH_LONG, true).show();
                            }
                        } else {
                            Toasty.warning(RegistrationActivity.this, getString(R.string.not_a_valid_email_address), Toast.LENGTH_LONG, true).show();
                        }
                    }
                } else {
                    Toasty.warning(RegistrationActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_LONG, true).show();
                }
            }
        });
    }

    private void registerWithEmailPass() {
        ux.getLoadingView();
        firebaseAuth.createUserWithEmailAndPassword(activityBinding.email.getText().toString(),
                activityBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i(Constants.TAG + ":onComplete", getString(R.string.registration_succcessful));
                    Toasty.success(RegistrationActivity.this, getString(R.string.registration_succcessful), Toast.LENGTH_LONG, true).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                } else {
                    if (task.getException().getMessage().equals(getString(R.string.firebase_user_exists_exception))) {
                        Toasty.error(RegistrationActivity.this, getString(R.string.user_already_exists), Toast.LENGTH_LONG, true).show();
                    } else {
                        Toasty.error(RegistrationActivity.this, getString(R.string.registration_unsucccessful), Toast.LENGTH_LONG, true).show();
                    }
                    Log.i(Constants.TAG + ":onComplete", getString(R.string.registration_unsucccessful));
                }
                ux.removeLoadingView();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}