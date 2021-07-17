package com.shakil.barivara.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shakil.barivara.R;

import java.util.Locale;

import static com.shakil.barivara.utils.Constants.mLanguage;
import static com.shakil.barivara.utils.Constants.mLanguageSetup;

public class LanguageManager {
    private Context context;
    PrefManager prefManager;

    public LanguageManager(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);
    }

    public void setLanguage(Class to){
        doPopUpForLanguage(to);
    }

    public void configLanguage(){
        String language = prefManager.getString(mLanguage);
        if(!TextUtils.isEmpty(language)){
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = (context.getResources().getConfiguration());
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            context.getResources().updateConfiguration(config, ((context.getResources().getDisplayMetrics())));
        }
    }

    //region open language selector
    private void doPopUpForLanguage(Class to){
        LinearLayout defaultLan, bengaliLan, englishLan;
        TextView DefaultTXT, BengaliTXT, EnglishTXT;
        Button ok;
        Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_selector_layout);
        dialog.setCanceledOnTouchOutside(true);

        defaultLan = dialog.findViewById(R.id.byDefault);
        bengaliLan = dialog.findViewById(R.id.bengali);
        englishLan = dialog.findViewById(R.id.english);
        DefaultTXT = dialog.findViewById(R.id.DefaultLanguageTXT);
        BengaliTXT = dialog.findViewById(R.id.BengaliTXT);
        EnglishTXT = dialog.findViewById(R.id.EnglishTXT);
        ok = dialog.findViewById(R.id.okButton);

        defaultLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(
                        DefaultTXT, defaultLan,
                        new TextView[]{BengaliTXT, EnglishTXT},
                        new LinearLayout[]{bengaliLan, englishLan}
                );

                prefManager.set(mLanguage, "en");
                prefManager.set(mLanguageSetup, true);
            }
        });


        bengaliLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(
                        BengaliTXT, bengaliLan,
                        new TextView[]{DefaultTXT, EnglishTXT},
                        new LinearLayout[]{defaultLan, englishLan}
                );

                prefManager.set(mLanguage, "bn");
                prefManager.set(mLanguageSetup, true);
            }
        });

        englishLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(
                        EnglishTXT, englishLan,
                        new TextView[]{DefaultTXT, BengaliTXT},
                        new LinearLayout[]{defaultLan, bengaliLan}
                );

                prefManager.set(mLanguage, "en");
                prefManager.set(mLanguageSetup, true);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (to != null) {
                    context.startActivity(new Intent(context, to));
                }
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    //endregion

    //region set text and background color based on language selection
    private void changeColor(TextView selectedTextResId, LinearLayout selectedBackResId,
                             TextView[] unselectedTextResIds, LinearLayout[] unselectedBackResIds){
        if (unselectedBackResIds != null && unselectedTextResIds != null){
            for (int start = 0; start < unselectedBackResIds.length; start++) {
                unselectedBackResIds[start].setBackgroundResource(R.drawable.rectangle_white);
                unselectedTextResIds[start].setTextColor(context.getResources().getColor(R.color.md_grey_800));
            }
        }

        selectedBackResId.setBackgroundResource(R.drawable.rectangle_green_selected);
        selectedTextResId.setTextColor(context.getResources().getColor(R.color.md_white_1000));
    }
    //endregion
}
