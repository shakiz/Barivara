package com.shakil.barivara.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.Calendar

class DatePickerManager {
    fun showDatePicker(context: Context, callback: DatePickerCallback) {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate =
                    selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear
                callback.onDateSelected(selectedDate)
            }, year, month, day
        ).show()
    }

    interface DatePickerCallback {
        fun onDateSelected(date: String)
    }
}