package com.shakil.barivara.utils

import java.util.regex.Pattern

object ApiConstants {
    const val BASE_URL = "https://rent-collector-8cceae311431.herokuapp.com/api/v1/"
}

object Constants {
    @JvmField
    var TAG = "dev-shakil"
    const val PREF_NAME = "vara-adai"
    const val mAppViewCount = "AppViewCount"
    const val mIsLoggedIn = "isLoggedIn"
    const val mLanguage = "Language"
    const val mUserId = "UserId"
    const val mUserFullName = "UserFullName"
    const val mUserEmail = "UserEmail"
    const val mUserMobile = "UserMobile"

    @JvmField
    val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    const val REQUEST_CALL_CODE = 1111
    const val FILE_REQUEST_CODE = 335
    const val mLanguageSetup = "LanguageSetup"
    const val mOldUser = "OldUser"
    const val mJobName = "JobName"
    const val IsStartOfMonth = "IsStartOfMonth"
    const val GIT_LIB_PACKAGE_NAME = "app.com.gitlib"
    const val SAGORKONNA_PACKAGE_NAME = "com.shakil.tourdekuakata"
    const val VARA_ADAI_FB_PAGE_LINK = "https://www.facebook.com/varaadai"
    const val MY_CONTACT_NO = "01688499299"
}
