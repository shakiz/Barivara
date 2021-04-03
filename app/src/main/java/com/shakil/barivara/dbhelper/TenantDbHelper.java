package com.shakil.barivara.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shakil.homeapp.activities.utils.Constants;

public class TenantDbHelper extends SQLiteOpenHelper {

    private static String TABLE_NAME = "owner";

    private static String TAG = "TenantDbHelper";

    private String CREATE_OWNER_TABLE = "";

    private String DROP_OWNER_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TenantDbHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_OWNER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_OWNER_TABLE);
    }

}
