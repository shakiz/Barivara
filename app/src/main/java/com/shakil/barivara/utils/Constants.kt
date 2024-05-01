package com.shakil.barivara.utils;

import java.util.regex.Pattern;

public class Constants {
    public static String TAG = "dev-shakil";
    public static final String PREF_NAME = "vara-adai";
    public static final String mAppViewCount = "AppViewCount";
    public static final String mIsLoggedIn = "isLoggedIn";
    public static final String mLanguage = "Language";
    public static final String mUserId = "UserId";
    public static final String mUserFullName = "UserFullName";
    public static final String mUserEmail = "UserEmail";
    public static final String mUserMobile = "UserMobile";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final int REQUEST_CALL_CODE = 1111;
    public static final int FILE_REQUEST_CODE = 335;
    public static final String mLanguageSetup = "LanguageSetup";
    public static final String mOldUser = "OldUser";
    public static final String mJobName = "JobName";
    public static final String IsStartOfMonth = "IsStartOfMonth";

    public static final String GIT_LIB_PACKAGE_NAME = "app.com.gitlib";
    public static final String SAGORKONNA_PACKAGE_NAME = "com.shakil.tourdekuakata";
    public static final String VARA_ADAI_FB_PAGE_LINK = "https://www.facebook.com/varaadai";
    public static final String MY_CONTACT_NO = "01688499299";
}
