package com.shakil.barivara.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsManager(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonAction(buttonName: String, additionalData: Map<String, String>? = null) {
        val bundle = Bundle().apply {
            putString("button_action", buttonName)
            additionalData?.forEach { (key, value) ->
                putString(key, value)
            }
        }
        firebaseAnalytics.logEvent("button_action", bundle)
    }

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}
