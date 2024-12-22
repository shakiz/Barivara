package com.shakil.barivara.presentation

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import com.shakil.barivara.R


class GenericDialog<T>(
    private val context: Context,
    private val layoutResId: Int,
    private val onClose: () -> Unit = {},
    private val onPrimaryAction: () -> Unit = {},
    private val onSecondaryAction: () -> Unit = {}
) {
    private lateinit var dialog: Dialog

    fun show() {
        val view = LayoutInflater.from(context).inflate(layoutResId, null)

        dialog = Dialog(context, R.style.CustomDialogTheme).apply {
            setContentView(view)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.setDimAmount(0.5f)
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        view.findViewById<Button>(R.id.primaryAction)?.setOnClickListener {
            onPrimaryAction()
            close()
        }
        view.findViewById<Button>(R.id.secondaryAction)?.setOnClickListener {
            onSecondaryAction()
            close()
        }

        dialog.show()
    }

    private fun close() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
