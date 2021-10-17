package com.shakil.barivara.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityForgotPasswordBinding;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.UX;
import com.shakil.barivara.utils.Validation;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding activityForgotPasswordBinding;
    private final Map<String, String[]> hashMap = new HashMap();
    private Validation validation;
    private FirebaseAuth firebaseAuth;
    private UX ux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);

        //region init objects and perform all UI interactions
        initUI();
        bindUiWithComponents();
        //endregion
    }

    //region init objects
    private void initUI() {
        ux = new UX(this);
        validation = new Validation(this, hashMap);
        firebaseAuth = FirebaseAuth.getInstance();
    }
    //endregion

    //region perform all UI interactions
    private void bindUiWithComponents() {
        //region validation
        validation.setEditTextIsNotEmpty(new String[]{"email"},
                new String[]{getString(R.string.email_validation)});
        //endregion

        //region reset email send listener job
        activityForgotPasswordBinding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isValid()){
                    ux.getLoadingView();
                    firebaseAuth.sendPasswordResetEmail(activityForgotPasswordBinding.email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(Constants.TAG, "Reset pass mail sent.");
                                        Toasty.info(ForgotPasswordActivity.this, getString(R.string.email_sent), Toast.LENGTH_SHORT, true).show();
                                        activityForgotPasswordBinding.changePasswordLayout.setVisibility(View.GONE);
                                        activityForgotPasswordBinding.afterChangePasswordLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.i(Constants.TAG, "Reset pass error : "+task.isSuccessful());
                                        if (task.getException().getMessage().equals(getString(R.string.firebase_no_user_exception))){
                                            Toasty.error(ForgotPasswordActivity.this, getString(R.string.email_was_not_found_in_our_database), Toast.LENGTH_SHORT, true).show();
                                        }
                                        else{
                                            Toasty.error(ForgotPasswordActivity.this, getString(R.string.failed_to_sent_reset_email), Toast.LENGTH_SHORT, true).show();
                                        }
                                        activityForgotPasswordBinding.changePasswordLayout.setVisibility(View.VISIBLE);
                                        activityForgotPasswordBinding.afterChangePasswordLayout.setVisibility(View.GONE);
                                    }
                                    ux.removeLoadingView();
                                }
                            });
                }
            }
        });
        //endregion

        activityForgotPasswordBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
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