package com.shakil.barivara.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.shakil.barivara.R

class AppUpdate(private val context: Context) {
    private val prefManager = PrefManager(context)
    private val tools = Tools(context)

    interface onGetUpdate {
        fun onResult(updated: Boolean)
    }

    fun getUpdate(listener: onGetUpdate?) {
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

    fun checkUpdate(showUpdated: Boolean, cancelable: Boolean): Boolean {
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
                    val view = View.inflate(context, R.layout.dialog_popup_app_update, null)
                    val builder = AlertDialog.Builder(
                        context
                    )
                    builder.setTitle("")
                        .setView(view)
                        .setCancelable(cancelable)
                    val mPopupDialogNoTitle: Dialog = builder.create()
                    mPopupDialogNoTitle.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mPopupDialogNoTitle.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    mPopupDialogNoTitle.show()
                    mPopupDialogNoTitle.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                    val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
                    val btnOk = view.findViewById<TextView>(R.id.btnOk)
                    btnCancel.text = context.getString(R.string.cancel)
                    btnOk.text = context.getString(R.string.update)
                    val title = view.findViewById<TextView>(R.id.title)
                    val version = view.findViewById<TextView>(R.id.version)
                    val message = view.findViewById<TextView>(R.id.message)
                    btnCancel.setOnClickListener {
                        mPopupDialogNoTitle.dismiss()
                        val tools = Tools(context)
                        tools.checkLogin()
                    }
                    btnOk.setOnClickListener { goToPlayStore(mPopupDialogNoTitle) }
                    if (latestVersion == currentVersion) {
                        btnOk.visibility = View.GONE
                        btnCancel.visibility = View.VISIBLE
                        title.text = context.getString(R.string.no_update_available)
                        version.text =
                            context.getString(R.string.no_update_version) + " " + currentVersion
                        message.text = ""
                    } else {
                        btnOk.visibility = View.VISIBLE
                        if (cancelable) {
                            btnCancel.visibility = View.VISIBLE
                        } else {
                            btnCancel.visibility = View.GONE
                        }
                        title.text = context.getString(R.string.update_available)
                        version.text =
                            context.getString(R.string.version) + " " + latestVersion + " " + context.getString(
                                R.string.has_been_released_small
                            )
                        message.text = context.getString(R.string.please_update_detail_text)
                    }
                }
            }
        }
        return isUpdated
    }

    private fun goToPlayStore(mPopupDialogNoTitle: Dialog) {
        val appPackageName = context.packageName
        if (tools.hasConnection()) {
            mPopupDialogNoTitle.dismiss()
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
