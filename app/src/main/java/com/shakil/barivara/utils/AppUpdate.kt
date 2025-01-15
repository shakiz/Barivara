package com.shakil.barivara.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.shakil.barivara.R

class AppUpdate(private val context: Context) {
    private val tools = Tools(context)

    interface OnGetUpdate {
        fun onResult(updated: Boolean)
    }

    fun getUpdate(listener: OnGetUpdate?, prefManager: PrefManager) {
        val appUpdater = AppUpdaterUtils(context)
            .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
            .withListener(object : UpdateListener {
                override fun onSuccess(update: Update, isUpdateAvailable: Boolean) {
                    var updated = true
                    val latestVersion = update.latestVersion
                    val currentVersion = tools.appVersionName
                    prefManager["LatestVersion"] = latestVersion
                    prefManager["CurrentVersion"] = currentVersion
                    if (!TextUtils.isEmpty(latestVersion) && latestVersion != "0.0.0.0") {
                        if (latestVersion != currentVersion) {
                            updated = false
                        }
                    }
                    listener?.onResult(updated)
                }

                override fun onFailed(error: AppUpdaterError) {
                    Toast.makeText(
                        context,
                        "App Updater Error: Something went wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                    listener?.onResult(true)
                }
            })
        appUpdater.start()
    }

    fun checkUpdate(showUpdated: Boolean, cancelable: Boolean, prefManager: PrefManager): Boolean {
        var isUpdated = true
        val latestVersion = prefManager.getString("LatestVersion")
        val currentVersion = prefManager.getString("CurrentVersion")
        if (latestVersion != null) {
            if (!TextUtils.isEmpty(latestVersion) && latestVersion != "0.0.0.0") {
                var shouldDisplay = true
                if (latestVersion == currentVersion) {
                    shouldDisplay = showUpdated
                } else {
                    isUpdated = false
                }
                if (shouldDisplay) {
                    showAppUpdateDialog(cancelable, latestVersion)
                }
            }
        }
        return isUpdated
    }

    private fun showAppUpdateDialog(isCancelable: Boolean, latestVersion: String) {
        val primaryAction: Button
        val secondaryAction: Button
        val version: TextView
        val dialog = Dialog(context, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_popup_app_update)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(isCancelable)

        primaryAction = dialog.findViewById(R.id.primaryAction)
        secondaryAction = dialog.findViewById(R.id.secondaryAction)
        version = dialog.findViewById(R.id.version)

        version.text = context.getString(R.string.version_x_s_has_been_released, latestVersion)

        primaryAction.setOnClickListener {
            dialog.dismiss()
            goToPlayStore()
        }
        secondaryAction.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun goToPlayStore() {
        val appPackageName = context.packageName
        if (tools.hasConnection()) {
            try {
                (context as Activity).finish()
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                (context as Activity).finish()
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.please_enable_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
