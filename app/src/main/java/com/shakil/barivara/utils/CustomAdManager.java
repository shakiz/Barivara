package com.shakil.barivara.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class CustomAdManager {
    private AdView adView;
    private final Context context;

    public CustomAdManager(AdView adView, Context context) {
        this.adView = adView;
        this.context = context;
    }

    //region generate ad
    public void generateAd(){
        //region adMob
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i("onInitComplete","InitializationComplete");
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i(Constants.TAG+"::onAdListener","AdlLoaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.i(Constants.TAG+"::onAdListener","AdFailedToLoad");
                Log.i(Constants.TAG+"::onAdListener","AdFailedToLoad Error "+adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i(Constants.TAG+"::onAdListener","AdOpened");
            }

            @Override
            public void onAdClicked() {
                Log.i(Constants.TAG+"::onAdListener","AdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                Log.i(Constants.TAG+"::onAdListener","AdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                Log.i(Constants.TAG+"::onAdListener","AdClosed");
            }
        });
        //endregion
    }
    //endregion

    //region clear adVIew reference
    public void disposeViews(){
        adView = null;
    }
    //endregion
}
