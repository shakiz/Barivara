package com.shakil.barivara.activities.onboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.shakil.barivara.R;
import com.shakil.barivara.utils.AppUpdate;
import com.shakil.barivara.utils.Tools;

public class SplashActivity extends AppCompatActivity {
    private Tools tools;
    private AppUpdate appUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();
        bindUIWithComponents();
    }

    private void initUI() {
        appUpdate = new AppUpdate(this);
        tools = new Tools(this);
    }

    private void bindUIWithComponents() {
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
    }
}