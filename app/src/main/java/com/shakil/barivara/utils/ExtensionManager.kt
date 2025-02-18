package com.shakil.barivara.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import com.shakil.barivara.utils.DateTimeConstants.API_DATETIME_FORMAT_Z
import com.shakil.barivara.utils.DateTimeConstants.APP_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.boldAfterColon(): SpannableString {
    val colonIndex = this.indexOf(":")
    return if (colonIndex != -1) {
        val spannableString = SpannableString(this)
        val startIndex = colonIndex + 1
        val endIndex = this.length

        // Apply bold style to the text after the colon
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString
    } else {
        SpannableString(this) // Return the original string if no colon is found
    }
}

fun String.formatDateIntoAppDate(): String {
    val inputFormat = SimpleDateFormat(API_DATETIME_FORMAT_Z, Locale.ENGLISH)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC timezone

    val outputFormat = SimpleDateFormat(APP_DATE_FORMAT, Locale.ENGLISH)

    val date = inputFormat.parse(this) ?: return this
    val day = SimpleDateFormat("d", Locale.ENGLISH).format(date).toInt()
    val suffix = getDaySuffix(day)

    return "${day}$suffix ${outputFormat.format(date).substring(2)}"
}

fun getDaySuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}