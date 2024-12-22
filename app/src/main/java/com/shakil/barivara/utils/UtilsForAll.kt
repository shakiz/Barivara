package com.shakil.barivara.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.shakil.barivara.R
import com.shakil.barivara.utils.Constants.TAG
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class UtilsForAll(private val context: Context) {
    fun exitApp() {
        val exitIntent = Intent(Intent.ACTION_MAIN)
        exitIntent.addCategory(Intent.CATEGORY_HOME)
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(exitIntent)
    }

    fun toIntValue(value: String): Int {
        return try {
            Log.v(TAG, "" + value.toInt())
            value.toInt()
        } catch (e: Exception) {
            Log.v(TAG, e.message!!)
            0
        }
    }

    fun setGreetings(): String {
        val calendar = Calendar.getInstance()
        val timeOfTheDay = calendar[Calendar.HOUR_OF_DAY]
        return if (timeOfTheDay in 0..11) {
            context.getString(R.string.good_morning)
        } else if (timeOfTheDay in 12..15) {
            context.getString(R.string.good_noon)
        } else if (timeOfTheDay in 16..17) {
            context.getString(R.string.good_afternoon)
        } else if (timeOfTheDay in 18..19) {
            context.getString(R.string.good_evening)
        } else {
            context.getString(R.string.good_night)
        }
    }

    fun dateTimeWithPM(): String {
        return DateFormat.getDateTimeInstance()
            .format(Date())
    }

    fun dayOfTheMonth(): String {
        val dateFormat: DateFormat = SimpleDateFormat("EEE")
        return dateFormat.format(Date())
    }

    fun isValidMobileNo(mobileNo: String): Boolean {
        return mobileNo.length == 11 && !mobileNo.startsWith(Constants.MOBILE_NUMBER_PREFIX)
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getMonthFromMonthName(monthName: String): Int {
        return when (monthName) {
            context.getString(R.string.january) -> 1
            context.getString(R.string.february) -> 2
            context.getString(R.string.march) -> 3
            context.getString(R.string.april) -> 4
            context.getString(R.string.may) -> 5
            context.getString(R.string.june) -> 6
            context.getString(R.string.july) -> 7
            context.getString(R.string.august) -> 8
            context.getString(R.string.september) -> 9
            context.getString(R.string.october) -> 10
            context.getString(R.string.november) -> 11
            context.getString(R.string.december) -> 12
            else -> 0
        }
    }

    fun getDateTime(): String {
        val df: DateFormat = SimpleDateFormat("MMM d, yyyy")
        val dateTimeText = df.format(Date())
        return dateTimeText
    }

    fun getDateTimeWithPM(): String {
        val currentDateTimeString = DateFormat.getDateTimeInstance()
            .format(Date())
        return currentDateTimeString
    }

    fun getDayOfTheMonth(): String {
        val dateFormat: DateFormat = SimpleDateFormat("EEE")
        val day = dateFormat.format(Date())
        return day
    }
}
