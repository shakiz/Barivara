package com.shakil.barivara.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan

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