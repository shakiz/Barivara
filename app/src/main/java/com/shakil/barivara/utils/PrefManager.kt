package com.shakil.barivara.utils

import android.content.Context
import com.shakil.barivara.utils.Constants.PREF_NAME

class PrefManager(context: Context) {
    private val PRIVATE_MODE = 0
    private val pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit()
    operator fun set(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    operator fun set(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    operator fun set(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getBoolean(key: String?): Boolean {
        return pref.getBoolean(key, false)
    }

    fun getString(key: String?): String? {
        return pref.getString(key, "")
    }

    fun getInt(key: String?): Int {
        return pref.getInt(key, 0)
    }

    fun clear() {
        editor.clear()
        editor.commit()
    }
}
