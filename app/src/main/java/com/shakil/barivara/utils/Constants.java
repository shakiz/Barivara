package com.shakil.barivara.utils;

import java.util.regex.Pattern;

public class Constants {
    public static String DATABASE_NAME = "home_rent";
    public static int DATABASE_VERSION = 3;
    public static String TAG = "shakil-dev";
    public static final String PREF_NAME = "vara-adai";
    public static final String mAppViewCount = "AppViewCount";
    public static final String mShouldRemember = "isRemembered";
    public static final String mIsLoggedIn = "isLoggedIn";
    public static final String mAlreadyVisited = "alreadyVisited";
    public static final String mLanguage = "Language";
    public static final String mUserId = "UserId";
    public static final String mUserFullName = "UserFullName";
    public static final String mUserEmail = "UserEmail";
    public static final String mUserMobile = "UserMobile";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final int REQUEST_CALL_CODE = 1111;
    public static final String mLanguageSetup = "LanguageSetup";
    public static final String mOldUser = "OldUser";
}
