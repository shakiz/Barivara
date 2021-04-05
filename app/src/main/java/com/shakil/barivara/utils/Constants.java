package com.shakil.barivara.utils;

public class Constants {
    public static String DATABASE_NAME = "home_rent";
    public static int DATABASE_VERSION = 3;
    public static String TAG = "shakil-dev";

    public static String TABLE_NAME_METER = "meter";
    public static String TABLE_NAME_ROOM = "room";
    public static String TABLE_NAME_TENANT = "tenant";
    public static String TABLE_NAME_ELECTRICITY_BILL = "electricity_bill";
    public static String TABLE_NAME_RENT = "rent";

    public class RoomTable{
        public static final String COLUMN_ROOM_ID = "room_id";
        public static final String COLUMN_ROOM_NAME = "room_name";
        public static final String COLUMN_TENANT_NAME = "tenant_name";
        public static final String COLUMN_START_MONTH_NAME = "start_month_name";
        public static final String COLUMN_START_MONTH_ID = "start_month_id";
        public static final String COLUMN_ASSOCIATE_METER_NAME = "associate_meter_name";
        public static final String COLUMN_ASSOCIATE_METER_ID = "associate_meter_id";
        public static final String COLUMN_ADVANCED_AMOUNT = "advanced_amount";
    }

    public class MeterTable{
        public static final String COLUMN_METER_ID = "meter_id";
        public static final String COLUMN_METER_NAME = "meter_name";
        public static final String COLUMN_ASSOCIATE_ROOM_NAME = "associate_room_name";
        public static final String COLUMN_ASSOCIATE_ROOM_ID = "associate_room_id";
        public static final String COLUMN_METER_TYPE_NAME = "meter_type_name";
        public static final String COLUMN_METER_TYPE_ID = "meter_type_id";
        public static final String COLUMN_METER_PRESENT_UNIT = "present_unit";
        public static final String COLUMN_METER_PAST_UNIT = "past_unit";
    }

    public class TenantTable{
        public static final String COLUMN_TENANT_ID = "tenant_id";
        public static final String COLUMN_TENANT_NAME = "tenant_name";
        public static final String COLUMN_START_MONTH_NAME = "start_month_name";
        public static final String COLUMN_START_MONTH_ID = "start_month_id";
        public static final String COLUMN_ASSOCIATE_ROOM_NAME = "associate_room_name";
        public static final String COLUMN_ASSOCIATE_ROOM_ID = "associate_room_id";
    }

    public class ElectricityBillTable{
        public static final String COLUMN_ELECTRICITY_BILL_ID = "bill_id";
        public static final String COLUMN_ROOM_ID = "room_id";
        public static final String COLUMN_ROOM_NAME = "room_name";
        public static final String COLUMN_METER_ID = "meter_id";
        public static final String COLUMN_METER_NAME = "meter_name";
        public static final String COLUMN_METER_PRESENT_UNIT = "present_unit";
        public static final String COLUMN_METER_PAST_UNIT = "past_unit";
        public static final String COLUMN_UNIT_PRICE = "unit_price";
        public static final String COLUMN_METER_TOTAL_UNIT = "total_unit";
        public static final String COLUMN_METER_TOTAL_BILL = "total_bill";
    }

    public class RentTable{
        public static final String COLUMN_RENT_ID = "rent_id";
        public static final String COLUMN_RENT_MONTH_NAME = "rent_month_name";
        public static final String COLUMN_RENT_MONTH_ID = "rent_month_id";
        public static final String COLUMN_RENT_ROOM_NAME = "rent_room_name";
        public static final String COLUMN_RENT_ROOM_ID = "rent_room_id";
        public static final String COLUMN_RENT_AMOUNT = "rent_amount";
    }
}
