package com.shakil.barivara.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

class CustomAdManager(private val context: Context) {
    fun generateAd(adView: AdView) {
        MobileAds.initialize(context) { Log.i("onInitComplete", "InitializationComplete") }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.i(Constants.TAG + "::onAdListener", "AdlLoaded")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.i(Constants.TAG + "::onAdListener", "AdFailedToLoad")
                Log.i(Constants.TAG + "::onAdListener", "AdFailedToLoad Error " + adError.message)
            }

            override fun onAdOpened() {
                Log.i(Constants.TAG + "::onAdListener", "AdOpened")
            }

            override fun onAdClicked() {
                Log.i(Constants.TAG + "::onAdListener", "AdClicked")
            }

            override fun onAdClosed() {
                Log.i(Constants.TAG + "::onAdListener", "AdClosed")
            }
        }
    }
}
