package com.shakil.barivara.activities.onboard;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivitySplashBinding;
import com.shakil.barivara.utils.AppUpdate;
import com.shakil.barivara.utils.PrefManager;
import com.shakil.barivara.utils.Tools;

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
                        appUpdate.checkUpdate(false, true);
                    }
                    else{
                        //region check for user login
                        tools.checkLogin();
                        //endregion
                    }
                }
            });
        }
        else{
            tools.checkLogin();
        }
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