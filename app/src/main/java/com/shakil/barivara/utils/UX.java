package com.shakil.barivara.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.shakil.barivara.R;

public class UX {
    public Dialog loadingDialog;

    public UX(Context context) {
        loadingDialog = new Dialog(context);
    }

    public void getLoadingView(){
        loadingDialog.setContentView(R.layout.loading_layout);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    public void removeLoadingView(){
        if (loadingDialog.isShowing()) loadingDialog.cancel();
    }
}
