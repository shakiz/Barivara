package com.shakil.barivara.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.shakil.homeapp.activities.model.room.Room;
import com.shakil.homeapp.activities.utils.Constants;

import java.util.ArrayList;

public class RoomDbHelper extends SQLiteOpenHelper {

    private static String TAG = "RoomDbHelper";

    private static final String COLUMN_ROOM_ID = "room_id";
    private static final String COLUMN_ROOM_NAME = "room_name";
    private static final String COLUMN_RENT_MONTH = "rent_month";
    private static final String COLUMN_ROOM_METER = "room_meter";
    private static final String COLUMN_TENANT_NAME = "tenant_name";
    private static final String COLUMN_ADVANCED_AMOUNT = "advanced_amount";

    private String CREATE_ROOM_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_ROOM + "("
            + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ROOM_NAME + " TEXT,"+ COLUMN_RENT_MONTH + " TEXT,"
            + COLUMN_ROOM_METER + " TEXT," + COLUMN_TENANT_NAME + " TEXT," + COLUMN_ADVANCED_AMOUNT + " REAL" + ")";

    private String DROP_ROOM_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME_ROOM;

    public RoomDbHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ROOM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_ROOM_TABLE);
    }

    public void addRoom(Room room) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NAME, room.getRoomName());
        values.put(COLUMN_RENT_MONTH, room.getStartMonthName());
        values.put(COLUMN_ROOM_METER, room.getAssociateMeterName());
        values.put(COLUMN_TENANT_NAME, room.getTenantName());
        values.put(COLUMN_ADVANCED_AMOUNT, room.getAdvancedAmount());
        // Inserting Row
        db.insert(Constants.TABLE_NAME_ROOM, null, values);
        Log.v("----------------","");
        Log.v(TAG,"");
        Log.v("Room Name : ", room.getRoomName());
        Log.v("Start Date : ", room.getStartMonthName());
        Log.v("Associate Meter : ", room.getAssociateMeterName());
        Log.v("Tenant Name : ",""+ room.getTenantName());
        Log.v("Advanced Amount : ",""+ room.getAdvancedAmount());
        Log.v("----------------","");
        db.close();
    }
    public ArrayList<Room> getAllRoomDetails() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ROOM_NAME,
                COLUMN_RENT_MONTH,
                COLUMN_ROOM_METER,
                COLUMN_TENANT_NAME,
                COLUMN_ADVANCED_AMOUNT
        };
        // sorting orders
        String sortOrder =
                COLUMN_ROOM_NAME + " ASC";
        ArrayList<Room> roomList = new ArrayList<Room>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_ROOM, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Room room = new Room();
                room.setRoomName(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_NAME)));
                room.setStartMonthName(cursor.getString(cursor.getColumnIndex(COLUMN_RENT_MONTH)));
                room.setAssociateMeterName(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_METER)));
                room.setTenantName(cursor.getString(cursor.getColumnIndex(COLUMN_TENANT_NAME)));
                room.setAdvancedAmount(cursor.getInt(cursor.getColumnIndex(COLUMN_ADVANCED_AMOUNT)));
                // Adding food item record to list
                roomList.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return roomList;
    }
}

