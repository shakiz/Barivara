package com.shakil.barivara.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityLoginBinding;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;
import com.shakil.barivara.utils.UtilsForAll;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding activityBinding;
    private FirebaseAuth firebaseAuth;
    private UX ux;
    private String loginWithStr;
    private Validation validation;
    private Tools tools;
    private final Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //region init UI
        initUI();
        loginWithStr = getString(R.string.mobile);
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
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        //region validation
        validation(new String[]{"mobileNumber"},
                new String[]{getString(R.string.mobile_validation)});
        //endregion

        //region register with click
        activityBinding.emailIdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithStr = getString(R.string.email);
                loginWith(loginWithStr);
                //region validation
                validation(new String[]{"email", "password"},
                        new String[]{getString(R.string.email_validation), getString(R.string.password_validation)});
                //endregion
            }
        });
        activityBinding.mobileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithStr = getString(R.string.mobile);
                loginWith(loginWithStr);
                //region validation
                validation(new String[]{"mobileNumber"},
                        new String[]{getString(R.string.mobile_validation)});
                //endregion
            }
        });
        //endregion

        //region register text click listener
        activityBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        //endregion

        //region login click listener
        activityBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    if (tools.hasConnection()){
                        ///region check use choose mobile or email option for login
                        if (loginWithStr.equals(getString(R.string.email))) {
                            if (tools.validateEmailAddress(activityBinding.email.getText().toString())){
                                loginWithEmail();
                            }
                            else{
                                Toasty.warning(LoginActivity.this, getString(R.string.not_a_valid_email_address), Toast.LENGTH_SHORT, true).show();
                            }
                        } else if (loginWithStr.equals(getString(R.string.mobile))){
                            loginWithMobile();
                        }
                        //endregion
                    }
                    else{
                        Toasty.warning(LoginActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT, true).show();
                    }
                }
            }
        });
        //endregion

        activityBinding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
    //endregion

    //region login with email
    private void loginWithEmail(){
        ux.getLoadingView();
        firebaseAuth.signInWithEmailAndPassword(activityBinding.email.getText().toString()
                , activityBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i(Constants.TAG+":onComplete",getString(R.string.login_succcessful));
                    Toasty.success(LoginActivity.this, getString(R.string.login_succcessful), Toast.LENGTH_SHORT, true).show();
                    //region set pref
                    tools.setLoginPrefs(task);
                    //endregion
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    if (task.getException().getMessage().equals(getString(R.string.firebase_password_not_valid_exception))){
                        Toasty.error(LoginActivity.this, getString(R.string.wrong_password), Toast.LENGTH_SHORT, true).show();
                    }
                    else if (task.getException().getMessage().equals(getString(R.string.firebase_no_user_exception))){
                        Toasty.error(LoginActivity.this, getString(R.string.email_was_not_found_in_our_database), Toast.LENGTH_SHORT, true).show();
                    }
                    else {
                        Toasty.error(LoginActivity.this, getString(R.string.login_unsucccessful), Toast.LENGTH_SHORT, true).show();
                    }
                    Log.i(Constants.TAG+":onComplete",getString(R.string.login_unsucccessful));
                }
                ux.removeLoadingView();
            }
        });
    }
    //endregion

    //region login with mobile
    private void loginWithMobile(){
        Intent intent = new Intent(LoginActivity.this, MobileRegVerificationActivity.class);
        intent.putExtra("mobile", activityBinding.mobileNumber.getText().toString());
        startActivity(intent);
    }
    //endregion

    //region change register with UI
    private void loginWith(String registerWith) {
        if (registerWith.equals(getString(R.string.email))) {
            activityBinding.mainMobileRegistrationLayout.setVisibility(View.GONE);
            activityBinding.mainEmailRegistrationLayout.setVisibility(View.VISIBLE);
            activityBinding.emailIdLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.rectangle_background_filled_gender));
            activityBinding.EmailId.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
            activityBinding.mobileLayout.setBackgroundResource(0);
            activityBinding.Mobile.setTextColor(ContextCompat.getColor(this, R.color.md_green_800));
            activityBinding.MobileIcon.setImageResource(R.drawable.ic_call_green);
            activityBinding.EmailIcon.setImageResource(R.drawable.ic_email_white);
        } else {
            activityBinding.mainEmailRegistrationLayout.setVisibility(View.GONE);
            activityBinding.mainMobileRegistrationLayout.setVisibility(View.VISIBLE);
            activityBinding.mobileLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.rectangle_background_filled_gender));
            activityBinding.emailIdLayout.setBackgroundResource(0);
            activityBinding.EmailId.setTextColor(ContextCompat.getColor(this, R.color.md_green_800));
            activityBinding.Mobile.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
            activityBinding.MobileIcon.setImageResource(R.drawable.ic_call_white);
            activityBinding.EmailIcon.setImageResource(R.drawable.ic_email_green);
        }
    }
    //endregion

    //region custom validation
    private void validation(String[] resNames, String[] validationMessages) {
        validation.setEditTextIsNotEmpty(resNames, validationMessages);
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        new UtilsForAll(this).exitApp();
    }
    //endregion
}