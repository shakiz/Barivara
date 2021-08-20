package com.shakil.barivara.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AppAnalytics {

    private final FirebaseAnalytics firebaseAnalytics;

    public AppAnalytics(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    //region register for an event
    public void registerEvent(String eventName, Bundle eventData) {
        firebaseAnalytics.logEvent(eventName, eventData);
    }
    //endregion

    //region set bundle data with string and int
    public Bundle setData(String key1, String data1, String key2, int data2) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        bundle.putInt(key2, data2);
        return bundle;
    }
    //endregion

    //region set bundle data with string and long
    public Bundle setData(String key1, String data1, String key2, long data2) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        bundle.putLong(key2, data2);
        return bundle;
    }
    //endregion

    //region set bundle data with string
    public Bundle setData(String key1, String data1) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        return bundle;
    }
    //endregion

    //region set bundle data with string
    public Bundle setData(String key1, String data1,String key2, String data2) {
        Bundle bundle = new Bundle();
        bundle.putString(key1, data1);
        bundle.putString(key2, data2);
        return bundle;
    }
    //endregion

    //region set bundle data with int
    public Bundle setData(String key1, int data1) {
        Bundle bundle = new Bundle();
        bundle.putInt(key1, data1);
        return bundle;
    }
    //endregion

    //region set bundle data with long
    public Bundle setData(String key1, long data1) {
        Bundle bundle = new Bundle();
        bundle.putLong(key1, data1);
        return bundle;
    }
    //endregion
}
