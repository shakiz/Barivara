package com.shakil.barivara.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.shakil.barivara.R
import com.shakil.barivara.data.model.auth.LoginResponse
import com.shakil.barivara.presentation.auth.login.LoginSelectionActivity
import com.shakil.barivara.presentation.auth.onboard.WelcomeActivity
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.presentation.onboard.SplashActivity
import com.shakil.barivara.utils.Constants.REQUEST_CALL_CODE
import com.shakil.barivara.utils.Constants.TAG
import com.shakil.barivara.utils.Constants.VALID_EMAIL_ADDRESS_REGEX
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.Constants.mAppViewCount
import com.shakil.barivara.utils.Constants.mIsLoggedIn
import com.shakil.barivara.utils.Constants.mLanguage
import com.shakil.barivara.utils.Constants.mOldUser
import com.shakil.barivara.utils.Constants.mRefreshToken
import com.shakil.barivara.utils.Constants.mRefreshTokenValidity
import com.shakil.barivara.utils.Constants.mUserEmail
import com.shakil.barivara.utils.Constants.mUserFullName
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.Constants.mUserMobile
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.regex.Matcher

class Tools(private val context: Context) {
    fun askCallPermission(activity: Activity?) {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_CODE
            )
        }
    }

    fun rateApp() {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.unable_to_open_play_store),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun doPopUpForLogout(to: Class<*>, prefManager: PrefManager) {
        val cancel: Button
        val logout: Button
        val dialog = Dialog(context, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout_confirmation_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        cancel = dialog.findViewById(R.id.cancelButton)
        logout = dialog.findViewById(R.id.logoutButton)
        cancel.setOnClickListener { dialog.dismiss() }
        logout.setOnClickListener {
            clearPrefForLogout(to, prefManager)
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

    fun doPopUpForExitApp() {
        val cancel: Button
        val exit: Button
        val dialog = Dialog(context, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.exit_app_confirmation_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        cancel = dialog.findViewById(R.id.cancelButton)
        exit = dialog.findViewById(R.id.exitButton)
        cancel.setOnClickListener { dialog.dismiss() }
        exit.setOnClickListener {
            dialog.dismiss()
            UtilsForAll(context).exitApp()
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    fun clearPrefForLogout(to: Class<*>, prefManager: PrefManager) {
        prefManager[mAppViewCount] = 0
        prefManager[mIsLoggedIn] = false
        prefManager[mUserId] = ""
        prefManager[mAccessToken] = ""
        prefManager[mRefreshToken] = ""
        prefManager[mRefreshTokenValidity] = 0
        prefManager[mUserId] = ""
        prefManager[mLanguage] = "en"
        prefManager[mUserFullName] = ""
        prefManager[mUserEmail] = ""
        prefManager[mUserMobile] = ""
        context.startActivity(Intent(context, to))
        Toast.makeText(
            context,
            context.getString(R.string.logged_out_successfully),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun validateEmailAddress(emailAddress: String?): Boolean {
        if (emailAddress.isNullOrEmpty()) {
            return false
        }
        var isValidEmail = false
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress)
        isValidEmail = matcher.find()
        return isValidEmail
    }

    val appVersionName: String
        get() {
            val manager = context.packageManager
            var info: PackageInfo? = null
            try {
                info = manager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return info!!.versionName.toString()
        }

    fun hasConnection(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            return true
        }
        val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobileNetwork != null && mobileNetwork.isConnected) {
            return true
        }
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun sendMessage(number: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        smsIntent.setType("vnd.android-dir/mms-sms")
        smsIntent.setData(Uri.parse("sms:$number"))
        context.startActivity(smsIntent)
    }

    fun isValidMobile(mobileNumber: String?): Boolean {
        var valid = false
        if (mobileNumber != null) {
            if (mobileNumber.length == 11) {
                val codes = arrayOf("011", "013", "014", "015", "016", "017", "018", "019")
                val userCode = mobileNumber.substring(0, 3)
                for (tempCode in codes) {
                    if (tempCode == userCode) {
                        valid = true
                        break
                    }
                }
            } else {
                valid = false
            }
        }
        return valid
    }

    fun checkLogin(prefManager: PrefManager) {
        Handler().postDelayed({
            var intent: Intent? = null
            intent = if (prefManager.getBoolean(mOldUser)) {
                if (prefManager.getBoolean(mIsLoggedIn)) {
                    Intent(context, HomeActivity::class.java)
                } else {
                    Intent(context, LoginSelectionActivity::class.java)
                }
            } else {
                Intent(context, WelcomeActivity::class.java)
            }
            context.startActivity(intent)
        }, 1000)
    }

    fun generatePDF(content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val myPdfDocument = PdfDocument()
            val myPageInfo = PageInfo.Builder(300, 600, 1).create()
            val myPage = myPdfDocument.startPage(myPageInfo)
            val myPaint = Paint()
            val x = 10
            var y = 25
            for (line in content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                myPage.canvas.drawText(line, x.toFloat(), y.toFloat(), myPaint)
                y = (y + (myPaint.descent() - myPaint.ascent())).toInt()
            }
            myPdfDocument.finishPage(myPage)
            val myFilePath = Environment.getExternalStorageDirectory().path + "/myPDFFile.pdf"
            val myFile = File(myFilePath)
            try {
                myPdfDocument.writeTo(FileOutputStream(myFile))
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "ERROR!!!!", Toast.LENGTH_SHORT).show()
            }
            myPdfDocument.close()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.your_device_does_not_support_this_feature),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun setLoginPrefs(
        mobileNumber: String,
        loginResponse: LoginResponse,
        prefManager: PrefManager
    ) {
        prefManager[mIsLoggedIn] = true
        prefManager[mAccessToken] = loginResponse.accessToken
        prefManager[mRefreshToken] = loginResponse.refreshToken
        prefManager[mRefreshTokenValidity] = loginResponse.refreshTokenExpiresAt ?: 0
        prefManager[mUserId] = loginResponse.userInfo.userId
        prefManager[mUserMobile] = mobileNumber
        prefManager[mUserEmail] = loginResponse.userInfo.email
        prefManager[mUserFullName] = loginResponse.userInfo.name
    }

    fun launchAppByPackageName(appPackageName: String) {
        try {
            Toasty.info(
                context,
                context.getString(R.string.redirecting_to_play_store),
                Toasty.LENGTH_LONG
            ).show()
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun launchUrl(url: String) {
        var url = url
        if (!url.contains("https")) {
            url = "https://$url"
        }
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun launchGmail() {
        val emailIntent = Intent(
            Intent.ACTION_VIEW, Uri.fromParts(
                "mailto", "shakil.play335@gmail.com", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is my subject text")
        context.startActivity(Intent.createChooser(emailIntent, null))
    }

    fun isFirstDayOfMonth(calendar: Calendar?): Boolean {
        requireNotNull(calendar) { "Calendar cannot be null." }
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        Log.i(TAG + "-Current Day", "::$dayOfMonth")
        //return dayOfMonth == 1;
        return true
    }

    companion object {
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        @JvmStatic
        fun sendNotification(context: Context, title: String?, message: String?) {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent = PendingIntent.getActivity(
                context,
                1001,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, "1001")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "1001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }
}
