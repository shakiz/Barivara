package com.shakil.barivara.utils;

import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AppAnalytics {

    private final FirebaseAnalytics firebaseAnalytics;

    public AppAnalytics(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void registerEvent(String eventName, Bundle eventData) {
        firebaseAnalytics.logEvent(eventName, eventData);
    }

    public Bundle setData(String key1, String data1, String key2, int data2) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        bundle.putInt(key2, data2);
        return bundle;
    }

    public Bundle setData(String key1, String data1, String key2, long data2) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        bundle.putLong(key2, data2);
        return bundle;
    }

    public Bundle setData(String key1, String data1) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        return bundle;
    }

    public Bundle setData(String key1, String data1,String key2, String data2) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        bundle.putString(key2, data2);
        return bundle;
    }

    public Bundle setData(String key1, int data1) {
        Bundle bundle = new Bundle();
        bundle.putInt(key1, data1);
        return bundle;
    }

    public Bundle setData(String key1, long data1) {
        Bundle bundle = new Bundle();
        bundle.putLong(key1, data1);
        return bundle;
    }
}
