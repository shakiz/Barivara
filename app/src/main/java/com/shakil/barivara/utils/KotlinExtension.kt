package com.shakil.barivara.utils

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.round


/**
 * Created by Rafiqul Hasan on 2019-08-25
 * Brain Station 23
 * rafiqul.hasan@bs-23.net
 */


fun Boolean?.orFalse(): Boolean = this ?: false

fun Int?.orZero(): Int = this ?: 0

fun Long?.orZero(): Long = this ?: 0L

fun Float?.orZero(): Float = this ?: 0f

fun Double?.orZero(): Double = this ?: 0.0

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun String?.equalsIgnoreCase(other: String?): Boolean {
    return equals(other, true)
}

fun String?.containsIgnoreCase(other: String?): Boolean {
    if (this == null || other == null) return false

    return contains(other, true)
}

fun Context.currentLanguage(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0].language
    } else {
        resources.configuration.locale.language
    }
}

fun Context?.dimenSize(@DimenRes dimenRes: Int): Int {
    return this?.resources?.getDimensionPixelSize(dimenRes) ?: 0
}

fun Context?.compatColor(@ColorRes colorRes: Int): Int {
    this ?: return Color.TRANSPARENT

    return ResourcesCompat.getColor(resources, colorRes, theme)
}

fun Context?.attrColor(@AttrRes attrRes: Int): Int {
    this ?: return Color.TRANSPARENT

    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

fun Resources?.toBitmap(@DrawableRes resId: Int): Bitmap? {
    this ?: return null
    return BitmapFactory.decodeResource(this, resId)
}

fun Context.showShortToast(@StringRes message: Int) {
    showShortToast(getString(message))
}

fun Context.showShortToast(message: String?) {
    if (message.isNullOrBlank()) {
        return
    }
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(@StringRes message: Int) {
    showLongToast(getString(message))
}

fun Context.showLongToast(message: String?) {
    if (message.isNullOrBlank()) {
        return
    }

    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Show the view  (visibility = View.VISIBLE)
 */
fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.showIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun View.hideIf(condition: Boolean) {
    visibility = if (condition) View.GONE else View.VISIBLE
}


/**
 * invisible the view  (visibility = View.INVISIBLE)
 */
fun View.invisible(): View {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
    return this
}

/**
 * invisible the view if [condition] returns true
 * (visibility = View.INVISIBLE)
 */
inline fun View.invisibleIf(condition: () -> Boolean): View {
    if (visibility != View.INVISIBLE && condition()) {
        visibility = View.INVISIBLE
    }
    return this
}

/**
 * Remove the view (visibility = View.GONE)
 */
fun View.gone(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Remove the view if [predicate] returns true
 * (visibility = View.GONE)
 */
inline fun View.goneIf(predicate: () -> Boolean): View {
    if (visibility != View.GONE && predicate()) {
        visibility = View.GONE
    }
    return this
}

/**
 * Returns false when [Context] is unavailable or is about to become unavailable
 */
fun Context?.isAlive(): Boolean = when (this) {
    null -> false
    is Application -> true
    is Activity -> !(this.isDestroyed or this.isFinishing)
    else -> true
}

fun Any?.isNull() = this == null


/**
 * Call this function on a dp value and it will return the equivalent
 * number of pixels for the current display.
 * e.g. 8.dp
 */
val Number.dp get() = toFloat() * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)


/**
 * Returns `true` if enum T contains an entry with the specified name.
 */
inline fun <reified T : Enum<T>> enumContains(name: String): Boolean {
    return enumValues<T>().any { it.name.equals(name, ignoreCase = true) }
}

fun Activity.hideSoftKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
}

fun SimpleDateFormat.tryParse(source: String): Date? {
    return try {
        parse(source)
    } catch (ex: Exception) {
        null
    }
}

fun Activity.changeStatusBarColor(@ColorRes colorRes: Int) {
    changeStatusBarBackgroundColor(compatColor(colorRes))
}

fun Activity.changeStatusBarBackgroundColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
fun Context.saveFileUsingMediaStore(url: String, fileName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val resolver = this.contentResolver
    val uri = resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
        URL(url).openStream().use { input ->
            resolver.openOutputStream(uri).use { output ->
                input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
            }
        }
    }
    return uri
}

fun Context.openPDFFile(file: File): Boolean {
    try {
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        val path: Uri = Uri.fromFile(file)
        val objIntent = Intent(Intent.ACTION_VIEW)
        objIntent.setDataAndType(path, "application/pdf")
        objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        objIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.startActivity(objIntent)
        return true
    } catch (e: Exception) {
    }
    return false
}

fun Context.openPDFFile(uri: Uri): Boolean {
    try {
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        val objIntent = Intent(Intent.ACTION_VIEW)
        objIntent.setDataAndType(uri, "application/pdf")
        objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        objIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.startActivity(objIntent)
        return true
    } catch (e: Exception) {
    }
    return false
}

fun getBlinkingAnimation(duration: Long): Animation {
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.duration = duration
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE

    return anim
}

fun Window?.makeSystemBarsLight() {
    this ?: return

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        insetsController
            ?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
    } else {
        decorView?.systemUiVisibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
    }
}

fun Window?.makeSystemBarsDark() {
    this ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController
            ?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
    } else {
        decorView?.systemUiVisibility = 0
    }
}

fun FragmentManager.currentNavigationFragment(): Fragment? {
    return primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()
}

inline fun <reified T> Map<String, Any?>.toObject(): T {
    return convert()
}

fun <T> T.toMap(): Map<String, Any> {
    return convert()
}

inline fun <T, reified R> T.convert(): R {
    val json = Gson().toJson(this)
    return Gson().fromJson(json, object : TypeToken<R>() {}.type)
}

fun String?.toUri(): Uri? {
    this ?: return null

    return try {
        Uri.parse(this)
    } catch (ex: Exception) {
        null
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
fun TextView.onTextChanged(debounceInterval: Long): Flow<CharSequence?> {
    return callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySendBlocking(s)
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) }
    }.debounce(debounceInterval)
}

fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, SHOW_FORCED)
    }
}