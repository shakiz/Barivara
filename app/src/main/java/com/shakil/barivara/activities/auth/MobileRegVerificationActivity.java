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
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class MobileRegVerificationActivity extends AppCompatActivity {
    private ActivityMobileRegVerificationBinding activityBinding;
    private String mobileNumber = "", mVerificationId = "";
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
                        Toasty.error(MobileRegVerificationActivity.this, getString(R.string.not_valid_otp_or_code), Toast.LENGTH_SHORT, true).show();
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
                Toasty.info(this, getString(R.string.please_wait)+"\n"+getString(R.string.we_are_verifying_you), Toast.LENGTH_SHORT, true).show();
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
                                    Log.i(Constants.TAG+":VerificationCompleted","Code::"+code);
                                    activityBinding.verificationCode.setText(code);
                                    verifyVerificationCode(code);
                                }
                            }
                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Log.i(Constants.TAG+":onVerificationFailed","Error::"+e.getMessage());
                                Toasty.error(MobileRegVerificationActivity.this, getString(R.string.code_verification_failed)+"\n"+getString(R.string.try_again), Toast.LENGTH_SHORT, true).show();
                            }
                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Log.i(Constants.TAG+":onCodeSent","Verification ID::"+s);
                                Toasty.info(MobileRegVerificationActivity.this,  getString(R.string.code_sent_please_check), Toast.LENGTH_SHORT, true).show();
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
            Log.i(Constants.TAG+":verifyVerificationCode","Code:"+code);
            try{
                //creating the credential
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                //signing the user
                loginWithMobile(credential);
            }
            catch (Exception e){
                Log.i(Constants.TAG+":verifyVerificationCode",e.getMessage());
            }
        }
        else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), getString(R.string.no_internet_title), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    //endregion

    //region sign in with credentials
    private void loginWithMobile(PhoneAuthCredential credential) {
        ux.getLoadingView();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(MobileRegVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(Constants.TAG+":loginWithMobile","Success");
                            tools.setLoginPrefs(task);
                            ux.removeLoadingView();
                            Toasty.success(MobileRegVerificationActivity.this, getString(R.string.login_succcessful), Toast.LENGTH_SHORT, true).show();
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(MobileRegVerificationActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.i(Constants.TAG+":loginWithMobile","Failed");
                            ux.removeLoadingView();
                            //verification unsuccessful.. display an error message
                            String message = getString(R.string.login_unsucccessful);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "\n"+getString(R.string.invalid_code_entered);
                            }
                            Toasty.error(MobileRegVerificationActivity.this, message, Toast.LENGTH_SHORT, true).show();
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