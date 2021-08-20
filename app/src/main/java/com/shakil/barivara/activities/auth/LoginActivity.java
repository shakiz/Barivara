package com.shakil.barivara.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.WelcomeActivity;
import com.shakil.barivara.databinding.ActivityLoginBinding;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;
import com.shakil.barivara.utils.UtilsForAll;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;

import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mUserEmail;
import static com.shakil.barivara.utils.Constants.mUserFullName;
import static com.shakil.barivara.utils.Constants.mUserId;
import static com.shakil.barivara.utils.Constants.mUserMobile;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth firebaseAuth;
    private UX ux;
    private PrefManager prefManager;
    private Validation validation;
    private Tools tools;
    private final Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //region init UI
        initUI();
        //endregion

        //region bind UI with components
        bindUiWithComponents();
        //endregion
    }

    //region init UI
    private void initUI() {
        firebaseAuth = FirebaseAuth.getInstance();
        ux = new UX(this);
        tools = new Tools(this);
        validation = new Validation(this, hashMap);
        prefManager = new PrefManager(this);
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"email", "password"},
                new String[]{getString(R.string.email_validation), getString(R.string.password_validation)});
        //endregion

        //region register text click listener
        activityLoginBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        //endregion

        //region login click listener
        activityLoginBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    if (tools.hasConnection()){
                        if (tools.validateEmailAddress(activityLoginBinding.email.getText().toString())){
                            login();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, getString(R.string.not_a_valid_email_address), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //endregion

        activityLoginBinding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
    //endregion

    private void login(){
        ux.getLoadingView();
        firebaseAuth.signInWithEmailAndPassword(activityLoginBinding.email.getText().toString()
                ,activityLoginBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i(Constants.TAG+":onComplete",getString(R.string.login_succcessful));
                    Toast.makeText(LoginActivity.this, getString(R.string.login_succcessful), Toast.LENGTH_SHORT).show();
                    prefManager.set(mIsLoggedIn, true);
                    if (task.getResult() != null){
                        prefManager.set(mUserId, task.getResult().getUser().getUid());
                        prefManager.set(mUserFullName, task.getResult().getUser().getDisplayName());
                        prefManager.set(mUserEmail, task.getResult().getUser().getEmail());
                        prefManager.set(mUserMobile, task.getResult().getUser().getPhoneNumber());
                    }
                    startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                }
                else{
                    if (task.getException().getMessage().equals(getString(R.string.firebase_password_not_valid_exception))){
                        Toast.makeText(LoginActivity.this, getString(R.string.wrong_password), Toast.LENGTH_LONG).show();
                    }
                    else if (task.getException().getMessage().equals(getString(R.string.firebase_no_user_exception))){
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.email_was_not_found_in_our_database), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_unsucccessful), Toast.LENGTH_LONG).show();
                    }
                    Log.i(Constants.TAG+":onComplete",getString(R.string.login_unsucccessful));
                }
                ux.removeLoadingView();
            }
        });
    }

    //region activity components
    @Override
    public void onBackPressed() {
        new UtilsForAll(this).exitApp();
    }
    //endregion
}