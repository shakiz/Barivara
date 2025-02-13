package com.shakil.barivara.utils

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.LinearLayout
import android.widget.TextView
import com.shakil.barivara.R
import com.shakil.barivara.utils.Constants.PREF_NAME
import com.shakil.barivara.utils.Constants.mLanguage
import java.util.*

interface LanguageCallBack {
    fun onLanguageChange(selectedLan: String)
}

object LocaleManager {
    fun setLocale(context: Context, language: String) {
        persistLanguage(context, language)
        updateResources(context, language)
    }

    fun getCurrentLocale(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(mLanguage, Locale.getDefault().language)
            ?: Locale.getDefault().language
    }

    private fun persistLanguage(context: Context, language: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(mLanguage, language).apply()
    }

    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLocale(locale)
            } else {
                @Suppress("DEPRECATION")
                this.locale = locale
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun applyLocale(context: Context): Context {
        val language = getCurrentLocale(context)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLocale(locale)
            } else {
                @Suppress("DEPRECATION")
                this.locale = locale
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun doPopUpForLanguage(context: Context, onLanguageCallBack: LanguageCallBack) {
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.language_selector_layout)
        dialog.setCanceledOnTouchOutside(true)
        var selectedLan = "en"
        val defaultLan: LinearLayout = dialog.findViewById(R.id.byDefault)
        val bengaliLan: LinearLayout = dialog.findViewById(R.id.bengali)
        val englishLan: LinearLayout = dialog.findViewById(R.id.english)
        val defaultTXT: TextView = dialog.findViewById(R.id.DefaultLanguageTXT)
        val bengaliTXT: TextView = dialog.findViewById(R.id.BengaliTXT)
        val englishTXT: TextView = dialog.findViewById(R.id.EnglishTXT)
        val ok: TextView = dialog.findViewById(R.id.okButton)
        defaultLan.setOnClickListener {
            changeColor(
                context,
                defaultTXT,
                defaultLan,
                arrayOf(bengaliTXT, englishTXT),
                arrayOf(bengaliLan, englishLan)
            )
            selectedLan = "en"
        }
        bengaliLan.setOnClickListener {
            changeColor(
                context,
                bengaliTXT,
                bengaliLan,
                arrayOf(defaultTXT, englishTXT),
                arrayOf(defaultLan, englishLan)
            )
            selectedLan = "bn"
        }
        englishLan.setOnClickListener {
            changeColor(
                context,
                englishTXT,
                englishLan,
                arrayOf(defaultTXT, bengaliTXT),
                arrayOf(defaultLan, bengaliLan)
            )
            selectedLan = "en"
        }
        ok.setOnClickListener {
            onLanguageCallBack.onLanguageChange(selectedLan = selectedLan)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun changeColor(
        context: Context,
        selectedTextResId: TextView, selectedBackResId: LinearLayout,
        unselectedTextResIds: Array<TextView>?, unselectedBackResIds: Array<LinearLayout>?
    ) {
        if (unselectedBackResIds != null && unselectedTextResIds != null) {
            for (start in unselectedBackResIds.indices) {
                unselectedBackResIds[start].setBackgroundResource(R.drawable.rectangle_white)
                unselectedTextResIds[start].setTextColor(context.resources.getColor(R.color.md_grey_800))
            }
        }
        selectedBackResId.setBackgroundResource(R.drawable.rectangle_green_selected)
        selectedTextResId.setTextColor(context.resources.getColor(R.color.md_white_1000))
    }
}
