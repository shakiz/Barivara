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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityLoginBinding;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.UX;

import static com.shakil.barivara.utils.Constants.mIsLoggedIn;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth firebaseAuth;
    private UX ux;
    private PrefManager prefManager;

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
        prefManager = new PrefManager(this);
    }
    //endregion

    //region bind UI with components
    private void bindUiWithComponents(){
        activityLoginBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        activityLoginBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ux.getLoadingView();
                firebaseAuth.signInWithEmailAndPassword(activityLoginBinding.email.getText().toString()
                  ,activityLoginBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i(Constants.TAG+":onComplete",getString(R.string.login_succcessful));
                            Toast.makeText(LoginActivity.this, getString(R.string.login_succcessful), Toast.LENGTH_SHORT).show();
                            prefManager.set(mIsLoggedIn, true);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else{
                            Log.i(Constants.TAG+":onComplete",getString(R.string.login_unsucccessful));
                            Toast.makeText(LoginActivity.this, getString(R.string.login_unsucccessful), Toast.LENGTH_SHORT).show();
                        }
                        ux.removeLoadingView();
                    }
                });
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