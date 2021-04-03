package com.shakil.barivara.mvvm;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RoomModelMVVM.class}, version = 1)
public abstract class RoomDatabaseMVVM extends RoomDatabase {

    private static RoomDatabaseMVVM instance;

    public abstract RoomDAO roomDAO();

    public static synchronized RoomDatabaseMVVM getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),RoomDatabaseMVVM.class,"room_database")
                        .fallbackToDestructiveMigration()
                        .build();
        }
        return instance;
    }

}
