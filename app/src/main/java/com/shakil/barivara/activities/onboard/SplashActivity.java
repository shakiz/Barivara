package com.shakil.barivara.activities.onboard;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.databinding.ActivitySplashBinding;
import com.shakil.barivara.utils.AppUpdate;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.Tools;

import static com.shakil.barivara.utils.Constants.mIsLoggedIn;
import static com.shakil.barivara.utils.Constants.mOldUser;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding activitySplashBinding;
    private Animation topAnim, middleAnim, bottomAnim;
    private PrefManager prefManager;
    private Tools tools;
    private AppUpdate appUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash);

        //region init components and their interactions
        initUI();
        bindUIWithComponents();
        //endregion
    }

    //region init XML components with backend
    private void initUI() {
        prefManager = new PrefManager(this);
        appUpdate = new AppUpdate(this);
        tools = new Tools(this);
    }
    //endregion

    //region perform UI interactions
    private void bindUIWithComponents() {
        setAnimation();
        loadAnimationForUI();

        //region splash screen code to call new activity after some time
        if (tools.hasConnection()){
            appUpdate.getUpdate(new AppUpdate.onGetUpdate() {
                @Override
                public void onResult(boolean updated) {
                    if(!updated){
                        appUpdate.checkUpdate(false, false);
                    }
                    else{
                        checkLogin();
                    }
                }
            });
        }
        else{
            checkLogin();
        }
        //endregion
    }
    //endregion

    //region check for user login
    private void checkLogin(){
        //region check for user login status
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //region check for new update
                Intent intent = null;
                if (prefManager.getBoolean(mIsLoggedIn)){
                    if (prefManager.getBoolean(mOldUser)) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    }
                }
                else{
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
                //endregion
            }
        }, 1000);
        //endregion
    }
    //endregion

    //region set animation
    private void setAnimation() {
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim_splash);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim_splash);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim_splash);
    }
    //endregion

    //region load animation into UI components
    private void loadAnimationForUI() {
        activitySplashBinding.FirstLine.setAnimation(topAnim);
        activitySplashBinding.SecondLine.setAnimation(topAnim);
        activitySplashBinding.ThirdLine.setAnimation(topAnim);
        activitySplashBinding.FourthLine.setAnimation(topAnim);
        activitySplashBinding.FifthLine.setAnimation(topAnim);

        activitySplashBinding.AppName.setAnimation(middleAnim);
        activitySplashBinding.DevelopedBy.setAnimation(bottomAnim);
        activitySplashBinding.DeveloperName.setAnimation(bottomAnim);
        activitySplashBinding.helloAnimation.animate().translationY(1000).setDuration(1000).setStartDelay(6000);
    }
    //endregion
}