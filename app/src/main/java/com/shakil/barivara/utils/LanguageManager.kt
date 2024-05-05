package com.shakil.barivara.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.shakil.barivara.R
import com.shakil.barivara.utils.Constants.mLanguage
import com.shakil.barivara.utils.Constants.mLanguageSetup
import java.util.Locale

class LanguageManager(private val context: Context) {
    private var prefManager: PrefManager = PrefManager(context)

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
        val dialog = Dialog(context, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.language_selector_layout)
        dialog.setCanceledOnTouchOutside(true)
        val defaultLan: LinearLayout = dialog.findViewById(R.id.byDefault)
        val bengaliLan: LinearLayout = dialog.findViewById(R.id.bengali)
        val englishLan: LinearLayout = dialog.findViewById(R.id.english)
        val DefaultTXT: TextView = dialog.findViewById(R.id.DefaultLanguageTXT)
        val BengaliTXT: TextView = dialog.findViewById(R.id.BengaliTXT)
        val EnglishTXT: TextView = dialog.findViewById(R.id.EnglishTXT)
        val ok: Button = dialog.findViewById(R.id.okButton)
        defaultLan.setOnClickListener {
            changeColor(
                DefaultTXT,
                defaultLan,
                arrayOf(BengaliTXT, EnglishTXT),
                arrayOf(bengaliLan, englishLan)
            )
            prefManager[mLanguage] = "en"
            prefManager[mLanguageSetup] = true
        }
        bengaliLan.setOnClickListener {
            changeColor(
                BengaliTXT,
                bengaliLan,
                arrayOf(DefaultTXT, EnglishTXT),
                arrayOf(defaultLan, englishLan)
            )
            prefManager[mLanguage] = "bn"
            prefManager[mLanguageSetup] = true
        }
        englishLan.setOnClickListener {
            changeColor(
                EnglishTXT,
                englishLan,
                arrayOf(DefaultTXT, BengaliTXT),
                arrayOf(defaultLan, bengaliLan)
            )
            prefManager[mLanguage] = "en"
            prefManager[mLanguageSetup] = true
        }
        ok.setOnClickListener {
            dialog.dismiss()
            if (to != null) {
                context.startActivity(Intent(context, to))
            }
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
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
