package com.shakil.barivara.utils

import java.util.regex.Pattern

object ApiConstants {
    //const val BASE_URL = "https://rent-collector-8cceae311431.herokuapp.com/api/v1/"
    const val BASE_URL = "http://rentcollector.xyz/api/v1/"
}

object DateTimeConstants {
    const val API_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val API_DATETIME_FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
    const val APP_DATETIME_FORMAT = "MMM dd, yyyy"
    const val APP_DATETIME_FORMAT_HASH = "d-M-yyyy"
}

object Constants {
    @JvmField
    var TAG = "dev-shakil"
    const val PREF_NAME = "vara-adai"
    const val mAppViewCount = "AppViewCount"
    const val mIsLoggedIn = "isLoggedIn"
    const val mAccessToken = "accessToken"
    const val mRefreshToken = "refreshToken"
    const val mRefreshTokenValidity = "refreshTokenValidity"
    const val mLanguage = "Language"
    const val mUserId = "UserId"
    const val mUserFullName = "UserFullName"
    const val mUserEmail = "UserEmail"
    const val mUserMobile = "UserMobile"
    const val mOtpResendTime = "OtpResendTime"

    @JvmField
    val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    const val REQUEST_CALL_CODE = 1111
    const val FILE_REQUEST_CODE = 335
    const val APP_UPDATE_REQUEST_CODE = 336
    const val mLanguageSetup = "LanguageSetup"
    const val mOldUser = "OldUser"
    const val mJobName = "JobName"
    const val IsStartOfMonth = "IsStartOfMonth"
    const val GIT_LIB_PACKAGE_NAME = "app.com.gitlib"
    const val SAGORKONNA_PACKAGE_NAME = "com.shakil.tourdekuakata"
    const val VARA_ADAI_FB_PAGE_LINK = "https://www.facebook.com/varaadai"
    const val MY_CONTACT_NO = "+8801688499299"
    const val CONTENT_TYPE = "application/json"
    const val ACCEPT = "application/json"

    const val MOBILE_NUMBER_PREFIX = "01"
}
