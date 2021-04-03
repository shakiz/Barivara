package com.shakil.barivara.mvvm;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomDAO {
    @Insert
    void insert(RoomModelMVVM roomModel);

    @Update
    void update(RoomModelMVVM roomModel);

    @Delete
    void delete(RoomModelMVVM roomModel);

    @Query("delete from room_table")
    void deleteAllRooms();

    @Query("select * from room_table order by roomName")
    LiveData<List<RoomModelMVVM>> getAllRoomDetails();
}
