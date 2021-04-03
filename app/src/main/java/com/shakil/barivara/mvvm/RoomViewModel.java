package com.shakil.barivara.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {
    private RoomRepository roomRepository;
    private LiveData<List<RoomModelMVVM>> allRoomData;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        roomRepository = new RoomRepository(application);
        allRoomData = roomRepository.getAllRoomDetails();
    }

    public void insert(RoomModelMVVM roomModelMVVM){
        roomRepository.insert(roomModelMVVM);
    }

    public void update(RoomModelMVVM roomModelMVVM){
        roomRepository.update(roomModelMVVM);
    }

    public void delete(RoomModelMVVM roomModelMVVM){
        roomRepository.delete(roomModelMVVM);
    }

    public void deleteAllRooms(RoomModelMVVM roomModelMVVM){
        roomRepository.deleteAllRooms();
    }

    public LiveData<List<RoomModelMVVM>> getAllRoomData() {
        return allRoomData;
    }
}
