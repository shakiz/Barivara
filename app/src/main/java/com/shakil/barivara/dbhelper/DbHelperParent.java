package com.shakil.barivara.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Constants;

import java.util.ArrayList;

import static com.shakil.barivara.utils.Constants.ElectricityBillTable.COLUMN_ELECTRICITY_BILL_ID;
import static com.shakil.barivara.utils.Constants.ElectricityBillTable.COLUMN_METER_TOTAL_BILL;
import static com.shakil.barivara.utils.Constants.ElectricityBillTable.COLUMN_METER_TOTAL_UNIT;
import static com.shakil.barivara.utils.Constants.ElectricityBillTable.COLUMN_UNIT_PRICE;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_ASSOCIATE_ROOM_ID;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_ASSOCIATE_ROOM_NAME;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_METER_ID;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_METER_NAME;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_METER_PAST_UNIT;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_METER_PRESENT_UNIT;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_METER_TYPE_ID;
import static com.shakil.barivara.utils.Constants.MeterTable.COLUMN_METER_TYPE_NAME;
import static com.shakil.barivara.utils.Constants.RentTable.COLUMN_RENT_AMOUNT;
import static com.shakil.barivara.utils.Constants.RentTable.COLUMN_RENT_ID;
import static com.shakil.barivara.utils.Constants.RentTable.COLUMN_RENT_MONTH_ID;
import static com.shakil.barivara.utils.Constants.RentTable.COLUMN_RENT_MONTH_NAME;
import static com.shakil.barivara.utils.Constants.RentTable.COLUMN_RENT_ROOM_ID;
import static com.shakil.barivara.utils.Constants.RentTable.COLUMN_RENT_ROOM_NAME;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_ADVANCED_AMOUNT;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_ASSOCIATE_METER_ID;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_ASSOCIATE_METER_NAME;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_ROOM_ID;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_ROOM_NAME;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_START_MONTH_ID;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_START_MONTH_NAME;
import static com.shakil.barivara.utils.Constants.RoomTable.COLUMN_TENANT_NAME;
import static com.shakil.barivara.utils.Constants.TenantTable.COLUMN_TENANT_ID;

public class DbHelperParent extends SQLiteOpenHelper {
    private static String TAG = "DbHelperParent";

    //region meter table starts
    private String CREATE_METER_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_METER + "("
            + COLUMN_METER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_METER_NAME + " TEXT,"
            + COLUMN_ASSOCIATE_ROOM_NAME + " TEXT," + COLUMN_ASSOCIATE_ROOM_ID + " REAL,"
            + COLUMN_METER_TYPE_NAME + " TEXT," + COLUMN_METER_TYPE_ID + " REAL,"
            + COLUMN_METER_PRESENT_UNIT + " REAL,"
            + COLUMN_METER_PAST_UNIT + " REAL" + ")";

    private String DROP_METER_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME_METER;
    //endregion

    //region room table starts
    private String CREATE_ROOM_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_ROOM + "("
            + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ROOM_NAME + " TEXT,"
            + COLUMN_START_MONTH_NAME + " TEXT,"+ COLUMN_START_MONTH_ID + " REAL, "
            + COLUMN_ASSOCIATE_METER_NAME + " TEXT," + COLUMN_ASSOCIATE_METER_ID + " REAL,"
            + COLUMN_TENANT_NAME + " TEXT," + COLUMN_ADVANCED_AMOUNT + " REAL" + ")";

    private String DROP_ROOM_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME_ROOM;
    //endregion

    //region tenant table starts
    private String CREATE_TENANT_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_TENANT + "("
            + COLUMN_TENANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Constants.TenantTable.COLUMN_TENANT_NAME + " TEXT,"+ COLUMN_START_MONTH_NAME + " TEXT,"
            + COLUMN_START_MONTH_ID+ " REAL," + COLUMN_ASSOCIATE_ROOM_NAME+ " REAL,"+
            COLUMN_ASSOCIATE_ROOM_ID + " TEXT" + ")";

    private String DROP_TENANT_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME_TENANT;
    //endregion

    //region electricity bill table starts
    private String CREATE_ELECTRICITY_BILL_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_ELECTRICITY_BILL + "("
            + COLUMN_ELECTRICITY_BILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ROOM_ID + " INTEGER ,"
            + COLUMN_METER_ID + " INTEGER ,"
            + COLUMN_METER_NAME + " TEXT ," + COLUMN_ROOM_NAME + " TEXT ,"
            + COLUMN_METER_PRESENT_UNIT + " INTEGER ," + COLUMN_METER_PAST_UNIT + " INTEGER ," + COLUMN_UNIT_PRICE + " REAL ,"
            + COLUMN_METER_TOTAL_UNIT + " INTEGER,"
            + COLUMN_METER_TOTAL_BILL + " REAL" + ")";

    private String DROP_ELECTRICITY_BILL_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME_RENT;
    //endregion

    //region rent table starts
    private String CREATE_RENT_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_RENT + "("
            + COLUMN_RENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RENT_MONTH_NAME + " TEXT," + COLUMN_RENT_MONTH_ID + " INTEGER ,"
            + COLUMN_RENT_ROOM_NAME + " TEXT," + COLUMN_RENT_ROOM_ID + " INTEGER ,"
            + COLUMN_RENT_AMOUNT + " REAL" + ")";

    private String DROP_RENT_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME_RENT;
    //endregion

    //region DbHelperParent methods
    public DbHelperParent(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_METER_TABLE);
        sqLiteDatabase.execSQL(CREATE_ROOM_TABLE);
        sqLiteDatabase.execSQL(CREATE_TENANT_TABLE);
        sqLiteDatabase.execSQL(CREATE_RENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_ELECTRICITY_BILL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_METER_TABLE);
        sqLiteDatabase.execSQL(DROP_ROOM_TABLE);
        sqLiteDatabase.execSQL(DROP_TENANT_TABLE);
        sqLiteDatabase.execSQL(DROP_ELECTRICITY_BILL_TABLE);
        sqLiteDatabase.execSQL(DROP_RENT_TABLE);
    }
    //endregion

    //region add new meter
    public void addMeter(Meter meter) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_METER_NAME, meter.getMeterName());
        values.put(COLUMN_METER_NAME, meter.getMeterName());
        values.put(COLUMN_ASSOCIATE_ROOM_NAME, meter.getAssociateRoom());
        values.put(COLUMN_ASSOCIATE_ROOM_ID, meter.getAssociateRoomId());
        values.put(COLUMN_METER_TYPE_NAME, meter.getMeterTypeName());
        values.put(COLUMN_METER_TYPE_ID, meter.getMeterTypeId());
        values.put(COLUMN_METER_PRESENT_UNIT, meter.getPresentUnit());
        values.put(COLUMN_METER_PAST_UNIT, meter.getPastUnit());
        // Inserting Row
        db.insert(Constants.TABLE_NAME_METER, null, values);
        Log.v("----------------","Meter Data");
        Log.v(TAG,"");
        Log.v("Meter Name : ", meter.getMeterName());
        Log.v("Associate Room : ", meter.getAssociateRoom());
        Log.v("Meter Type : ", meter.getMeterTypeName());
        Log.v("----------------","");
        db.close();
    }
    //endregion

    //region get all meter count
    public int getTotalMeterRows(){
        String query = "select * from "+Constants.TABLE_NAME_METER;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query ,null);
        int count = cursor.getCount();
        return count;
    }
    //endregion

    //region get all meter details
    public ArrayList<Meter> getAllMeterDetails() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_METER_ID,
                COLUMN_METER_NAME,
                COLUMN_ASSOCIATE_ROOM_NAME,
                COLUMN_ASSOCIATE_ROOM_ID,
                COLUMN_METER_TYPE_NAME,
                COLUMN_METER_TYPE_ID,
                COLUMN_METER_PRESENT_UNIT,
                COLUMN_METER_PAST_UNIT
        };
        // sorting orders
        String sortOrder =
                COLUMN_METER_NAME + " ASC";
        ArrayList<Meter> meterList = new ArrayList<Meter>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_METER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Meter meter = new Meter();
                meter.setMeterId(cursor.getString(cursor.getColumnIndex(COLUMN_METER_ID)));
                meter.setMeterName(cursor.getString(cursor.getColumnIndex(COLUMN_METER_NAME)));
                meter.setAssociateRoom(cursor.getString(cursor.getColumnIndex(COLUMN_ASSOCIATE_ROOM_NAME)));
                meter.setAssociateRoomId(cursor.getInt(cursor.getColumnIndex(COLUMN_ASSOCIATE_ROOM_ID)));
                meter.setMeterTypeName(cursor.getString(cursor.getColumnIndex(COLUMN_METER_TYPE_NAME)));
                meter.setMeterTypeId(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_TYPE_ID)));
                meter.setPresentUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_PRESENT_UNIT)));
                meter.setPastUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_PAST_UNIT)));
                // Adding food item record to list
                meterList.add(meter);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return meterList;
    }
    //endregion

    //region get meter names
    public ArrayList<String> getMeterNames() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_METER_NAME,
        };
        // sorting orders
        String sortOrder =
                COLUMN_METER_NAME + " ASC";
        ArrayList<String> meterNameList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_METER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        meterNameList.add("Select Data");
        if (cursor.moveToFirst()) {
            do {
                // Adding food item record to list
                meterNameList.add(cursor.getString(cursor.getColumnIndex(COLUMN_METER_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return meterNameList;
    }
    //endregion

    //region update meter entry
    public void updateMeter(Meter record, int meterId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_METER_NAME, record.getMeterName());
        values.put(COLUMN_ASSOCIATE_ROOM_NAME, record.getAssociateRoom());
        values.put(COLUMN_ASSOCIATE_ROOM_ID, record.getAssociateRoomId());
        values.put(COLUMN_METER_TYPE_NAME, record.getMeterTypeName());
        values.put(COLUMN_METER_TYPE_ID, record.getMeterTypeId());
        values.put(COLUMN_METER_PRESENT_UNIT, record.getPresentUnit());
        values.put(COLUMN_METER_PAST_UNIT, record.getPastUnit());

        //updating Row
        sqLiteDatabase.update(Constants.TABLE_NAME_METER, values, COLUMN_METER_ID +" = "+meterId, null);
    }
    //endregion

    //region add room
    public void addRoom(Room room) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NAME, room.getRoomName());
        values.put(COLUMN_TENANT_NAME, room.getTenantName());
        values.put(COLUMN_START_MONTH_NAME, room.getStartMonthName());
        values.put(COLUMN_START_MONTH_ID, room.getStartMonthId());
        values.put(COLUMN_ASSOCIATE_METER_NAME, room.getAssociateMeterName());
        values.put(COLUMN_ASSOCIATE_METER_ID, room.getAssociateMeterId());
        values.put(COLUMN_ADVANCED_AMOUNT, room.getAdvancedAmount());
        // Inserting Row
        db.insert(Constants.TABLE_NAME_ROOM, null, values);
        Log.v("----------------","");
        Log.v(TAG,"Room Data");
        Log.v("Room Name : ", room.getRoomName());
        Log.v("Start Date : ", room.getStartMonthName());
        Log.v("Associate Meter : ", room.getAssociateMeterName());
        Log.v("Tenant Name : ",""+ room.getTenantName());
        Log.v("Advanced Amount : ",""+ room.getAdvancedAmount());
        Log.v("----------------","");
        db.close();
    }
    //endregion

    //region update room entry
    public void updateRoom(Room record, int roomId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NAME, record.getRoomName());
        values.put(COLUMN_TENANT_NAME, record.getTenantName());
        values.put(COLUMN_START_MONTH_NAME, record.getStartMonthName());
        values.put(COLUMN_START_MONTH_ID, record.getStartMonthId());
        values.put(COLUMN_ASSOCIATE_METER_NAME, record.getAssociateMeterName());
        values.put(COLUMN_ASSOCIATE_METER_ID, record.getAssociateMeterId());
        values.put(COLUMN_ADVANCED_AMOUNT, record.getAdvancedAmount());

        // updating Row
        sqLiteDatabase.update(Constants.TABLE_NAME_ROOM, values, COLUMN_ROOM_ID+" = "+roomId, null);
    }
    //endregion

    //region returns number of total rooms
    public int getTotalRoomRows(){
        String query = "select * from "+Constants.TABLE_NAME_ROOM;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query ,null);
        int count = cursor.getCount();
        return count;
    }
    //endregion

    //region get all rooms
    public ArrayList<Room> getAllRoomDetails() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ROOM_ID,
                COLUMN_ROOM_NAME,
                COLUMN_TENANT_NAME,
                COLUMN_START_MONTH_NAME,
                COLUMN_START_MONTH_ID,
                COLUMN_ASSOCIATE_METER_NAME,
                COLUMN_ASSOCIATE_METER_ID,
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
                room.setRoomId(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_ID)));
                room.setRoomName(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_NAME)));
                room.setTenantName(cursor.getString(cursor.getColumnIndex(COLUMN_TENANT_NAME)));
                room.setStartMonthName(cursor.getString(cursor.getColumnIndex(COLUMN_START_MONTH_NAME)));
                room.setStartMonthId(cursor.getInt(cursor.getColumnIndex(COLUMN_START_MONTH_ID)));
                room.setAssociateMeterName(cursor.getString(cursor.getColumnIndex(COLUMN_ASSOCIATE_METER_NAME)));
                room.setAssociateMeterId(cursor.getInt(cursor.getColumnIndex(COLUMN_ASSOCIATE_METER_ID)));
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
    //endregion

    //region get room names
    public ArrayList<String> getRoomNames() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ROOM_NAME,
        };
        // sorting orders
        String sortOrder =
                COLUMN_ROOM_NAME + " ASC";
        ArrayList<String> roomNameList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_ROOM, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        roomNameList.add("Select Room Name");
        if (cursor.moveToFirst()) {
            do {
                // Adding food item record to list
                roomNameList.add(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return roomNameList;
    }
    //endregion

    //region add a new tenant
    public void addTenant(Tenant tenant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.TenantTable.COLUMN_TENANT_NAME, tenant.getTenantName());
        values.put(COLUMN_START_MONTH_ID, tenant.getStartingMonthId());
        values.put(COLUMN_START_MONTH_NAME, tenant.getStartingMonth());
        values.put(COLUMN_ASSOCIATE_ROOM_NAME, tenant.getAssociateRoom());
        values.put(COLUMN_ASSOCIATE_ROOM_ID, tenant.getAssociateRoomId());
        // Inserting Row
        db.insert(Constants.TABLE_NAME_TENANT, null, values);
        Log.v("----------------","");
        Log.v(TAG,"Tenant Data");
        Log.v("Tenant Name : ", tenant.getTenantName());
        Log.v("Start Date : ", tenant.getStartingMonth());
        Log.v("----------------","");
        db.close();
    }
    //endregion

    //region update tenant entry
    public void updateTenant(Tenant record, int tenantId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TENANT_NAME, record.getTenantName());
        values.put(COLUMN_START_MONTH_NAME, record.getStartingMonth());
        values.put(COLUMN_START_MONTH_ID, record.getStartingMonthId());
        values.put(COLUMN_ASSOCIATE_ROOM_NAME, record.getAssociateRoom());
        values.put(COLUMN_ASSOCIATE_ROOM_ID, record.getAssociateRoomId());

        // updating Row
        sqLiteDatabase.update(Constants.TABLE_NAME_TENANT, values, COLUMN_TENANT_ID+" = "+tenantId, null);
    }
    //endregion

    //region get all tenants
    public ArrayList<Tenant> getAllTenantDetails() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_TENANT_ID,
                COLUMN_TENANT_NAME,
                COLUMN_START_MONTH_ID,
                COLUMN_START_MONTH_NAME,
                COLUMN_ASSOCIATE_ROOM_NAME,
                COLUMN_ASSOCIATE_ROOM_ID
        };
        // sorting orders
        String sortOrder =
                Constants.TenantTable.COLUMN_TENANT_NAME + " ASC";
        ArrayList<Tenant> tenants = new ArrayList<Tenant>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_TENANT, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tenant tenant = new Tenant();
                //tenant.setTenantId(cursor.getString(cursor.getColumnIndex(COLUMN_TENANT_ID)));
                tenant.setTenantName(cursor.getString(cursor.getColumnIndex(Constants.TenantTable.COLUMN_TENANT_NAME)));
                tenant.setStartingMonthId(cursor.getInt(cursor.getColumnIndex(COLUMN_START_MONTH_ID)));
                tenant.setStartingMonth(cursor.getString(cursor.getColumnIndex(COLUMN_START_MONTH_NAME)));
                tenant.setAssociateRoom(cursor.getString(cursor.getColumnIndex(COLUMN_ASSOCIATE_ROOM_NAME)));
                tenant.setAssociateRoomId(cursor.getInt(cursor.getColumnIndex(COLUMN_ASSOCIATE_ROOM_ID)));
                // Adding food item record to list
                tenants.add(tenant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return roomModelList list
        return tenants;
    }
    //endregion

    //region get all tenants name
    public ArrayList<String> getTenantNames() {
        // array of columns to fetch
        String[] columns = {
                Constants.TenantTable.COLUMN_TENANT_NAME,
        };
        // sorting orders
        String sortOrder =
                Constants.TenantTable.COLUMN_TENANT_NAME + " ASC";
        ArrayList<String> tenantNameList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_TENANT, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        tenantNameList.add("Select Tenant Name");
        if (cursor.moveToFirst()) {
            do {
                // Adding food item record to list
                tenantNameList.add(cursor.getString(cursor.getColumnIndex(Constants.TenantTable.COLUMN_TENANT_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return tenantNameList;
    }
    //endregion

    //region returns number of total tenants
    public int getTotalTenantRows(){
        String query = "select * from "+Constants.TABLE_NAME_TENANT;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query ,null);
        int count = cursor.getCount();
        return count;
    }
    //endregion

    //region add new rent
    public void addRent(Rent rent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RENT_MONTH_NAME, rent.getMonthName());
        values.put(COLUMN_RENT_MONTH_ID, rent.getMonthId());
        values.put(COLUMN_RENT_ROOM_NAME, rent.getAssociateRoomName());
        values.put(COLUMN_RENT_ROOM_ID, rent.getAssociateRoomId());
        values.put(COLUMN_RENT_AMOUNT, rent.getRentAmount());
        // Inserting Row
        db.insert(Constants.TABLE_NAME_RENT, null, values);
        Log.v("----------------","");
        Log.v(TAG,"Rent Data");
        Log.v("Rent month Name : ", rent.getMonthName());
        Log.v("Rent room : ", rent.getAssociateRoomName());
        Log.v("Amount : ",""+ rent.getRentAmount());
        Log.v("----------------","");
        db.close();
    }
    //endregion

    //region update a rent
    public void updateRent(Rent rent, int rentId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RENT_MONTH_NAME, rent.getMonthName());
        values.put(COLUMN_RENT_MONTH_ID, rent.getMonthId());
        values.put(COLUMN_RENT_ROOM_NAME, rent.getAssociateRoomName());
        values.put(COLUMN_RENT_ROOM_ID, rent.getAssociateRoomId());
        values.put(COLUMN_RENT_AMOUNT, rent.getRentAmount());

        //updating Row
        sqLiteDatabase.update(Constants.TABLE_NAME_RENT, values, COLUMN_RENT_ID +" = "+rentId, null);
    }
    //endregion

    //region get all rent details
    public ArrayList<Rent> getAllRentDetails() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_RENT_ID,
                COLUMN_RENT_MONTH_NAME,
                COLUMN_RENT_MONTH_ID,
                COLUMN_RENT_ROOM_NAME,
                COLUMN_RENT_ROOM_ID,
                COLUMN_RENT_AMOUNT
        };
        // sorting orders
        String sortOrder =
                COLUMN_RENT_ROOM_NAME + " ASC";
        ArrayList<Rent> rents = new ArrayList<Rent>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_RENT, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Rent rent = new Rent();
                rent.setRentId(cursor.getString(cursor.getColumnIndex(COLUMN_RENT_ID)));
                rent.setMonthName(cursor.getString(cursor.getColumnIndex(COLUMN_RENT_MONTH_NAME)));
                rent.setMonthId(cursor.getInt(cursor.getColumnIndex(COLUMN_RENT_MONTH_ID)));
                rent.setAssociateRoomName(cursor.getString(cursor.getColumnIndex(COLUMN_RENT_ROOM_NAME)));
                rent.setAssociateRoomId(cursor.getInt(cursor.getColumnIndex(COLUMN_RENT_ROOM_ID)));
                rent.setRentAmount(cursor.getInt(cursor.getColumnIndex(COLUMN_RENT_AMOUNT)));
                // Adding food item record to list
                rents.add(rent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return rents;
    }
    //endregion

    //region returns total rent amount
    public int getTotalRentAmount(){
        int rentAmount = 0;
        // array of columns to fetch
        String[] columns = {
                COLUMN_RENT_ID,
                COLUMN_RENT_AMOUNT
        };
        // sorting orders
        String sortOrder =
                COLUMN_RENT_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_RENT, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                rentAmount = rentAmount + cursor.getInt(cursor.getColumnIndex(COLUMN_RENT_AMOUNT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return roomModelList list
        return rentAmount;
    }
    //endregion

    //region add new electricity bill
    public void addElectricityBill(ElectricityBill electricityBill) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_METER_ID, electricityBill.getMeterId());
        values.put(COLUMN_ROOM_ID, electricityBill.getRoomId());
        values.put(COLUMN_ROOM_NAME, electricityBill.getRoomName());
        values.put(COLUMN_METER_NAME, electricityBill.getMeterName());
        values.put(COLUMN_UNIT_PRICE, electricityBill.getUnitPrice());
        values.put(COLUMN_METER_PRESENT_UNIT, electricityBill.getPresentUnit());
        values.put(COLUMN_METER_PAST_UNIT, electricityBill.getPastUnit());
        values.put(COLUMN_METER_TOTAL_UNIT, electricityBill.getTotalUnit());
        values.put(COLUMN_METER_TOTAL_BILL, electricityBill.getTotalBill());
        // Inserting Row
        db.insert(Constants.TABLE_NAME_ELECTRICITY_BILL, null, values);
        db.close();
    }
    //endregion

    //region update an electricity bill
    public void updateElectricityBill(ElectricityBill electricityBill, int billId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_METER_ID, electricityBill.getMeterId());
        values.put(COLUMN_ROOM_ID, electricityBill.getRoomId());
        values.put(COLUMN_ROOM_NAME, electricityBill.getRoomName());
        values.put(COLUMN_METER_NAME, electricityBill.getMeterName());
        values.put(COLUMN_UNIT_PRICE, electricityBill.getUnitPrice());
        values.put(COLUMN_METER_PRESENT_UNIT, electricityBill.getPresentUnit());
        values.put(COLUMN_METER_PAST_UNIT, electricityBill.getPastUnit());
        values.put(COLUMN_METER_TOTAL_UNIT, electricityBill.getTotalUnit());
        values.put(COLUMN_METER_PAST_UNIT, electricityBill.getTotalBill());

        //updating Row
        sqLiteDatabase.update(Constants.TABLE_NAME_ELECTRICITY_BILL, values, COLUMN_ELECTRICITY_BILL_ID +" = "+billId, null);
    }
    //endregion

    //region get all electricity bills
    public ArrayList<ElectricityBill> getAllElectricityBills() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ELECTRICITY_BILL_ID,
                COLUMN_METER_ID,
                COLUMN_ROOM_ID,
                COLUMN_METER_NAME,
                COLUMN_ROOM_NAME,
                COLUMN_UNIT_PRICE,
                COLUMN_METER_PRESENT_UNIT,
                COLUMN_METER_PAST_UNIT,
                COLUMN_METER_TOTAL_UNIT,
                COLUMN_METER_TOTAL_BILL
        };
        // sorting orders
        String sortOrder =
                COLUMN_ELECTRICITY_BILL_ID + " DESC";
        ArrayList<ElectricityBill> billList = new ArrayList<ElectricityBill>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_ELECTRICITY_BILL, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ElectricityBill electricityBill = new ElectricityBill();
                electricityBill.setBillId(cursor.getString(cursor.getColumnIndex(COLUMN_ELECTRICITY_BILL_ID)));
                electricityBill.setMeterId(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_ID)));
                electricityBill.setRoomId(cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID)));
                electricityBill.setMeterName(cursor.getString(cursor.getColumnIndex(COLUMN_METER_NAME)));
                electricityBill.setRoomName(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_NAME)));
                electricityBill.setUnitPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_UNIT_PRICE)));
                electricityBill.setPresentUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_PRESENT_UNIT)));
                electricityBill.setPastUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_PAST_UNIT)));
                electricityBill.setTotalUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_TOTAL_UNIT)));
                electricityBill.setTotalBill(cursor.getInt(cursor.getColumnIndex(COLUMN_METER_TOTAL_BILL)));
                // Adding food item record to list
                billList.add(electricityBill);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return roomModelList list
        return billList;
    }
    //endregion

    //region total electricity bill collection
    public int getTotalElectricityBillCollection(){
        int totalBill = 0;
        // array of columns to fetch
        String[] columns = {
                COLUMN_ELECTRICITY_BILL_ID,
                COLUMN_METER_TOTAL_BILL
        };
        // sorting orders
        String sortOrder =
                COLUMN_ELECTRICITY_BILL_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_ELECTRICITY_BILL, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totalBill = totalBill + cursor.getInt(cursor.getColumnIndex(COLUMN_METER_TOTAL_BILL));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return roomModelList list
        return totalBill;
    }
    //endregion

    //region total units
    public int getTotalUnit(){
        int totalUnit = 0;
        // array of columns to fetch
        String[] columns = {
                COLUMN_ELECTRICITY_BILL_ID,
                COLUMN_METER_TOTAL_UNIT
        };
        // sorting orders
        String sortOrder =
                COLUMN_ELECTRICITY_BILL_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME_ELECTRICITY_BILL, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totalUnit = totalUnit + cursor.getInt(cursor.getColumnIndex(COLUMN_METER_TOTAL_UNIT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return roomModelList list
        return totalUnit;
    }
    //endregion
}
