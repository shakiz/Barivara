package com.shakil.barivara.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.shakil.barivara.R;

import java.util.Locale;

import static com.shakil.barivara.utils.Constants.mLanguage;

public class LanguageManager {
    private Context context;
    PrefManager prefManager;

    public LanguageManager(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);
    }

    public interface onSetLanguageListener {
        void onSet();
    }

    public void setLanguage(onSetLanguageListener listener){

        final onSetLanguageListener customListener = listener;

        final String shortLanguage[] = new String[]{ "en", "bn", "en"};
        String fullLanguage[] = new String[]{ context.getString(R.string.english),
                context.getString(R.string.bengali), "Default"};

        buttonLessSingleChoiceModal(context.getString(R.string.language_settings), fullLanguage, new onModalListItemClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                prefManager.set(mLanguage, shortLanguage[id]);
                if(customListener != null) customListener.onSet();
            }
        });
    }

    public void configLanguage(){
        String language = prefManager.getString(mLanguage);
        if(!TextUtils.isEmpty(language)){
            Locale locale = new Locale(language);
            Configuration config = ((Activity)context).getBaseContext().getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            ((Activity)context).getBaseContext().getResources().updateConfiguration(config, ((Activity)context).getBaseContext().getResources().getDisplayMetrics());

        }
    }


    //region modal
    public interface onModalListItemClickListener {
        void onClick(DialogInterface dialog, int id);
    }
    public void buttonLessSingleChoiceModal(String title, String[] listItems, onModalListItemClickListener listener) {

        final onModalListItemClickListener customListener = listener;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(title);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                customListener.onClick(dialog, id);
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    //endregion
}
