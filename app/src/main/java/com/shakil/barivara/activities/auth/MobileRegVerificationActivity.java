package com.shakil.barivara.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityMobileRegVerificationBinding;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.concurrent.TimeUnit;

public class MobileRegVerificationActivity extends AppCompatActivity {
    private ActivityMobileRegVerificationBinding activityBinding;
    String mobileNumber = "", mVerificationId = "";
    private FirebaseAuth firebaseAuth;
    private Tools tools;
    private UX ux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_reg_verification);

        //region get intent data, init UI and perform interactions
        initUI();
        getIntentData();
        bindUIWithComponents();
        //endregion
    }

    //region init UI
    private void initUI() {
        firebaseAuth = FirebaseAuth.getInstance();
        tools = new Tools(this);
        ux = new UX(this);
    }
    //endregion

    //region perform interactions
    private void bindUIWithComponents() {
        activityBinding.sentCodeHintText.setText(getString(R.string.sent_you_code_on_your_number) +"("+mobileNumber+")");

        //region verify button click listener
        activityBinding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(activityBinding.verificationCode.getText().toString())){
                    if (activityBinding.verificationCode.getText().toString().length() < 6){
                        Toast.makeText(MobileRegVerificationActivity.this,
                                getString(R.string.not_valid_otp_or_code), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //verifying the code entered manually
                        verifyVerificationCode(activityBinding.verificationCode.getText().toString());
                    }
                }
                else{
                    activityBinding.verificationCode.requestFocus();
                    activityBinding.verificationCode.setError(getString(R.string.otp_or_code_can_not_be_empty));
                }
            }
        });
        //endregion
    }
    //endregion

    //region get intent data
    private void getIntentData() {
        if (getIntent().getStringExtra("mobile") != null) {
            mobileNumber = getIntent().getStringExtra("mobile");
            if (tools.hasConnection()) {
                sendVerificationCode(mobileNumber);
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), getString(R.string.no_internet_title), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }
    //endregion

    //region send verification code
    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+88"+mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                String code = phoneAuthCredential.getSmsCode();
                                if (code != null) {
                                    activityBinding.verificationCode.setText(code);
                                    verifyVerificationCode(code);
                                }
                            }
                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Toast.makeText(MobileRegVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                //storing the verification id that is sent to the user
                                mVerificationId = s;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(
                options
        );
    }
    //endregion

    //region verify code
    private void verifyVerificationCode(String code) {
        if (tools.hasConnection()) {
            //creating the credential
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            //signing the user
            signInWithPhoneAuthCredential(credential);
        }
        else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), getString(R.string.no_internet_title), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    //endregion

    //region sign in with credentials
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        ux.getLoadingView();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(MobileRegVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            tools.setLoginPrefs(task);
                            ux.removeLoadingView();
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(MobileRegVerificationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            ux.removeLoadingView();
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
    //endregion

    //region activity components

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //endregion
}