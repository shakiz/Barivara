package com.shakil.barivara.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTimeToAppText(dateString: String?): String {
    if (dateString.isNullOrEmpty()) {
        return ""
    }
    val inputFormat =
        SimpleDateFormat(DateTimeConstants.API_DATETIME_FORMAT, Locale.getDefault())
    val outputFormat =
        SimpleDateFormat(DateTimeConstants.APP_DATETIME_FORMAT, Locale.getDefault())
    val date: Date? = inputFormat.parse(dateString)
    return if (date != null) outputFormat.format(date) else ""
}