package com.shakil.barivara.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.shakil.barivara.R;

public class AppUpdate {
    private final Context context;
    private final PrefManager prefManager;
    private final Tools tools;

    public AppUpdate(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);
        tools = new Tools(context);
    }

    public interface onGetUpdate {
        void onResult(boolean updated);
    }

    public void getUpdate(final onGetUpdate listener) {
        AppUpdaterUtils appUpdater = new AppUpdaterUtils(context)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        boolean updated = true;
                        String latestVersion = update.getLatestVersion();
                        String currentVersion = tools.getAppVersionName();
                        prefManager.set("LatestVersion", latestVersion);
                        prefManager.set("CurrentVersion", currentVersion);
                        if ((!TextUtils.isEmpty(latestVersion)) && (!latestVersion.equals("0.0.0.0"))) {
                            if (!latestVersion.equals(currentVersion)) {
                                updated = false;
                            }
                        }
                        if (listener != null) listener.onResult(updated);
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Toast.makeText(context, "App Updater Error: Something went wrong!", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onResult(true);
                    }
                });
        appUpdater.start();
    }

    public boolean checkUpdate(final boolean showUpdated, final boolean cancelable) {
        boolean isUpdated = true;
        String latestVersion = prefManager.getString("LatestVersion");
        String currentVersion = prefManager.getString("CurrentVersion");
        if (latestVersion != null) {
            if ((!TextUtils.isEmpty(latestVersion)) && (!latestVersion.equals("0.0.0.0"))) {
                boolean shouldDisplay = true;
                if (latestVersion.equals(currentVersion)) {
                    shouldDisplay = showUpdated;
                } else {
                    isUpdated = false;
                }
                if (shouldDisplay) {
                    View view = View.inflate(context, R.layout.dialog_popup_app_update, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("")
                            .setView(view)
                            .setCancelable(cancelable);
                    final Dialog mPopupDialogNoTitle = builder.create();
                    mPopupDialogNoTitle.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mPopupDialogNoTitle.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mPopupDialogNoTitle.show();
                    mPopupDialogNoTitle.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                    TextView btnCancel = view.findViewById(R.id.btnCancel);
                    TextView btnOk = view.findViewById(R.id.btnOk);
                    btnCancel.setText(context.getString(R.string.cancel));
                    btnOk.setText(context.getString(R.string.update));

                    TextView title = view.findViewById(R.id.title);
                    TextView version = view.findViewById(R.id.version);
                    TextView message = view.findViewById(R.id.message);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPopupDialogNoTitle.dismiss();
                            Tools tools = new Tools(context);
                            tools.checkLogin();
                        }
                    });
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToPlayStore(mPopupDialogNoTitle);
                        }
                    });
                    if (latestVersion.equals(currentVersion)) {
                        btnOk.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.VISIBLE);
                        title.setText(context.getString(R.string.no_update_available));
                        version.setText(context.getString(R.string.no_update_version) + " " + currentVersion);
                        message.setText("");
                    } else {
                        btnOk.setVisibility(View.VISIBLE);
                        if (cancelable) {
                            btnCancel.setVisibility(View.VISIBLE);
                        } else {
                            btnCancel.setVisibility(View.GONE);
                        }
                        title.setText(context.getString(R.string.update_available));
                        version.setText(context.getString(R.string.version) + " " + latestVersion + " " + context.getString(R.string.has_been_released_small));

                        message.setText(context.getString(R.string.please_update_detail_text));
                    }
                }
            }
        }
        return isUpdated;
    }

    private void goToPlayStore(Dialog mPopupDialogNoTitle) {
        final String appPackageName = context.getPackageName();
        if (tools.hasConnection()) {
            mPopupDialogNoTitle.dismiss();
            try {
                ((Activity) context).finish();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                ((Activity) context).finish();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else {
            Toast.makeText(context, context.getString(R.string.please_enable_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

}
