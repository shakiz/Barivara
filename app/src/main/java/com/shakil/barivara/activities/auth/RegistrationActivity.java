package com.shakil.barivara.activities.auth;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.shakil.barivara.databinding.ActivityRegistrationBinding;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding activityBinding;
    private FirebaseAuth firebaseAuth;
    private UX ux;
    private Tools tools;
    private String registerWithStr= "";
    private Validation validation;
    private final Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        //region init UI
        initUI();
        //endregion

        //region bind UI with components
        bindUiWithComponents();
        //endregion
    }

    //region init UI
    private void initUI(){
        firebaseAuth = FirebaseAuth.getInstance();
        ux = new UX(this);
        validation = new Validation(this, hashMap);
        tools = new Tools(this, activityBinding.mainLayout);
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"email", "password"},
                new String[]{getString(R.string.email_validation), getString(R.string.password_validation)});
        //endregion

        //region login click listener
        activityBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
        //endregion

        //region register with click
        activityBinding.emailIdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithStr = getString(R.string.email);
                registerWith(registerWithStr);
            }
        });
        activityBinding.mobileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithStr = getString(R.string.mobile);
                registerWith(registerWithStr);
            }
        });
        //endregion

        //region register click listener
        activityBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    if (tools.hasConnection()) {
                        if (activityBinding.password.getText().toString().length() >= 6){
                            //region check email address validation
                            if (!TextUtils.isEmpty(activityBinding.email.getText().toString())){
                                if (tools.validateEmailAddress(activityBinding.email.getText().toString())){
                                    ux.getLoadingView();
                                    firebaseAuth.createUserWithEmailAndPassword(activityBinding.email.getText().toString(),
                                            activityBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                Log.i(Constants.TAG+":onComplete",getString(R.string.registration_succcessful));
                                                Toast.makeText(RegistrationActivity.this, getString(R.string.registration_succcessful), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                            }
                                            else{
                                                if (task.getException().getMessage().equals(getString(R.string.firebase_user_exists_exception))){
                                                    Toast.makeText(RegistrationActivity.this,
                                                            getString(R.string.user_already_exists), Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(RegistrationActivity.this,
                                                            getString(R.string.registration_unsucccessful), Toast.LENGTH_LONG).show();
                                                }
                                                Log.i(Constants.TAG+":onComplete",getString(R.string.registration_unsucccessful));
                                            }
                                            ux.removeLoadingView();
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(RegistrationActivity.this, getString(R.string.not_a_valid_email_address),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(RegistrationActivity.this, getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
                            }
                            //endregion
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this, getString(R.string.password_must_be_six_character), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //endregion
    }
    //endregion

    //region change gender UI
    private void registerWith(String registerWith){
        if (registerWith.equals(getString(R.string.email))) {
            activityBinding.mainMobileRegistrationLayout.setVisibility(View.GONE);
            activityBinding.mainEmailRegistrationLayout.setVisibility(View.VISIBLE);
            activityBinding.emailIdLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.rectangle_background_filled_gender));
            activityBinding.EmailId.setTextColor(ContextCompat.getColor(this,R.color.md_white_1000));
            activityBinding.mobileLayout.setBackgroundResource(0);
            activityBinding.Mobile.setTextColor(ContextCompat.getColor(this,R.color.md_green_800));
            activityBinding.MobileIcon.setImageResource(R.drawable.ic_call_green);
            activityBinding.EmailIcon.setImageResource(R.drawable.ic_email_white);
        } else {
            activityBinding.mainEmailRegistrationLayout.setVisibility(View.GONE);
            activityBinding.mainMobileRegistrationLayout.setVisibility(View.VISIBLE);
            activityBinding.mobileLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.rectangle_background_filled_gender));
            activityBinding.emailIdLayout.setBackgroundResource(0);
            activityBinding.EmailId.setTextColor(ContextCompat.getColor(this,R.color.md_green_800));
            activityBinding.Mobile.setTextColor(ContextCompat.getColor(this,R.color.md_white_1000));
            activityBinding.MobileIcon.setImageResource(R.drawable.ic_call_white);
            activityBinding.EmailIcon.setImageResource(R.drawable.ic_email_green);
        }
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //endregion
}