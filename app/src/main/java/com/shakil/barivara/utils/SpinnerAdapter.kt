package com.shakil.barivara.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.shakil.barivara.R

class SpinnerAdapter {
    fun setSpinnerAdapter(spinner: Spinner, context: Context?, spinnerValue: ArrayList<String>) {
        val spinnerAdapter = ArrayAdapter(context!!, R.layout.spinner_drop, spinnerValue)
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }
}
