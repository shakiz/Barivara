package com.shakil.barivara.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.shakil.barivara.R

class UX(context: Context) {
    private val loadingDialog = Dialog(context)

    fun getLoadingView() {
        loadingDialog.setContentView(R.layout.loading_layout)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCanceledOnTouchOutside(false)
        loadingDialog.show()
    }

    fun removeLoadingView() {
        if (loadingDialog.isShowing) loadingDialog.cancel()
    }
}
