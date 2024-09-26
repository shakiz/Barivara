package com.shakil.barivara.presentation

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shakil.barivara.R

class GenericBottomSheet<T>(
    private val context: Context,
    private val layoutResId: Int,
    private val onClose: () -> Unit = {},
    private val onPrimaryAction: () -> Unit = {},
    private val onSecondaryAction: () -> Unit = {}
) {
    private lateinit var bottomSheetDialog: BottomSheetDialog

    fun show() {
        val view = LayoutInflater.from(context).inflate(layoutResId, null)
        bottomSheetDialog = BottomSheetDialog(context, R.style.TransparentBottomSheetDialog).apply {
            setContentView(view)
            setOnDismissListener {
                onClose()
                close()
            }
        }
        view.findViewById<Button>(R.id.primaryAction).setOnClickListener {
            onPrimaryAction()
            close()
        }
        view.findViewById<Button>(R.id.secondaryAction).setOnClickListener {
            onSecondaryAction()
            close()
        }
        bottomSheetDialog.show()
    }

    private fun close() {
        if (::bottomSheetDialog.isInitialized && bottomSheetDialog.isShowing) {
            bottomSheetDialog.dismiss()
        }
    }
}
