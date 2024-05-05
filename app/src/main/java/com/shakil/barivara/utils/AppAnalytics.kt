package com.shakil.barivara.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AppAnalytics(context: Context) {
    private val firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun registerEvent(eventName: String, eventData: Bundle?) {
        firebaseAnalytics.logEvent(eventName, eventData)
    }

    fun setData(key1: String?, data1: String?, key2: String?, data2: Int): Bundle {
        val bundle = Bundle()
        bundle.putString(key1, data1)
        bundle.putInt(key2, data2)
        return bundle
    }

    fun setData(key1: String?, data1: String?, key2: String?, data2: Long): Bundle {
        val bundle = Bundle()
        bundle.putString(key1, data1)
        bundle.putLong(key2, data2)
        return bundle
    }

    fun setData(key1: String?, data1: String?): Bundle {
        val bundle = Bundle()
        bundle.putString(key1, data1)
        return bundle
    }

    fun setData(key1: String?, data1: String?, key2: String?, data2: String?): Bundle {
        val bundle = Bundle()
        bundle.putString(key1, data1)
        bundle.putString(key2, data2)
        return bundle
    }

    fun setData(key1: String?, data1: Int): Bundle {
        val bundle = Bundle()
        bundle.putInt(key1, data1)
        return bundle
    }

    fun setData(key1: String?, data1: Long): Bundle {
        val bundle = Bundle()
        bundle.putLong(key1, data1)
        return bundle
    }
}
