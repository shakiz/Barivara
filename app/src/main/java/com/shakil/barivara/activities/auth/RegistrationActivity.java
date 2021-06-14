package com.shakil.barivara.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding activityRegistrationBinding;
    private FirebaseAuth firebaseAuth;
    private UX ux;
    private Tools tools;
    private Validation validation;
    private Map<String, String[]> hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

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
        tools = new Tools(this, activityRegistrationBinding.mainLayout);
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"email", "password"},
                new String[]{getString(R.string.email_validation), getString(R.string.password_validation)});
        //endregion

        //region login click listener
        activityRegistrationBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
        //endregion

        //region register click listener
        activityRegistrationBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    if (activityRegistrationBinding.password.getText().toString().length() >= 6){
                        //region check email address validation
                        if (!TextUtils.isEmpty(activityRegistrationBinding.email.getText().toString())){
                            if (tools.validateEmailAddress(activityRegistrationBinding.email.getText().toString())){
                                ux.getLoadingView();
                                firebaseAuth.createUserWithEmailAndPassword(activityRegistrationBinding.email.getText().toString(),
                                        activityRegistrationBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Log.i(Constants.TAG+":onComplete",getString(R.string.registration_succcessful));
                                            Toast.makeText(RegistrationActivity.this, getString(R.string.registration_succcessful), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                        }
                                        else{
                                            Log.i(Constants.TAG+":onComplete",getString(R.string.registration_unsucccessful));
                                            Toast.makeText(RegistrationActivity.this, getString(R.string.registration_unsucccessful), Toast.LENGTH_SHORT).show();
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
                }
            }
        });
        //endregion
    }
    //endregion


    //region activity components
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //endregion
}