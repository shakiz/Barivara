package com.shakil.barivara.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.shakil.barivara.R
import com.shakil.barivara.utils.Constants.mLanguage
import java.util.Locale

class LanguageManager(private val context: Context, private val prefManager: PrefManager) {

    fun setLanguage(to: Class<*>?) {
        doPopUpForLanguage(to)
    }

    fun configLanguage() {
        val language = prefManager.getString(mLanguage)
        if (!TextUtils.isEmpty(language)) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    private fun doPopUpForLanguage(to: Class<*>?) {
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.language_selector_layout)
        dialog.setCanceledOnTouchOutside(true)
        val defaultLan: LinearLayout = dialog.findViewById(R.id.byDefault)
        val bengaliLan: LinearLayout = dialog.findViewById(R.id.bengali)
        val englishLan: LinearLayout = dialog.findViewById(R.id.english)
        val defaultTXT: TextView = dialog.findViewById(R.id.DefaultLanguageTXT)
        val bengaliTXT: TextView = dialog.findViewById(R.id.BengaliTXT)
        val englishTXT: TextView = dialog.findViewById(R.id.EnglishTXT)
        val ok: TextView = dialog.findViewById(R.id.okButton)
        defaultLan.setOnClickListener {
            changeColor(
                defaultTXT,
                defaultLan,
                arrayOf(bengaliTXT, englishTXT),
                arrayOf(bengaliLan, englishLan)
            )
            prefManager[mLanguage] = "en"
        }
        bengaliLan.setOnClickListener {
            changeColor(
                bengaliTXT,
                bengaliLan,
                arrayOf(defaultTXT, englishTXT),
                arrayOf(defaultLan, englishLan)
            )
            prefManager[mLanguage] = "bn"
        }
        englishLan.setOnClickListener {
            changeColor(
                englishTXT,
                englishLan,
                arrayOf(defaultTXT, bengaliTXT),
                arrayOf(defaultLan, bengaliLan)
            )
            prefManager[mLanguage] = "en"
        }
        ok.setOnClickListener {
            dialog.dismiss()
            if (to != null) {
                context.startActivity(Intent(context, to))
            }
        }
        dialog.show()
    }

    private fun changeColor(
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
