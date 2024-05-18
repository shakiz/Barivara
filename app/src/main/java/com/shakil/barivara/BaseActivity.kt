package com.shakil.barivara

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.properties.Delegates

abstract class BaseActivity<DataBinding : ViewDataBinding> : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var dataBinding: DataBinding by Delegates.notNull()

    @get:LayoutRes
    abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyApplication.firebaseAnalytics.let {
            firebaseAnalytics = it
        }

        dataBinding = DataBindingUtil.setContentView(this, layoutResourceId)
        dataBinding.lifecycleOwner = this
        setVariables(dataBinding)
    }

    fun registerEvent(eventName: String, eventData: Bundle?) {
        firebaseAnalytics.logEvent(eventName, eventData)
    }

    fun setData(key1: String?, data1: String?): Bundle {
        val bundle = Bundle()
        bundle.putString(key1, data1)
        return bundle
    }

    abstract fun setVariables(dataBinding: DataBinding)

    fun getContext(): Context = this

    // fix for "android.view.InflateException Error inflating class android.webkit.WebView"
    override fun getAssets(): AssetManager {
        return resources.assets
    }
}