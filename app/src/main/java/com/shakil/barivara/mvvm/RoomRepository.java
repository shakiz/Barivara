package com.shakil.barivara.mvvm;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

//Repository is not a part of android architecture component library but it considers best practices.

public class RoomRepository {
    private RoomDAO roomDAO;
    private LiveData<List<RoomModelMVVM>> allRoomDetails;

    public  RoomRepository(Application application){
        RoomDatabaseMVVM roomDatabaseMVVM = RoomDatabaseMVVM.getInstance(application);
        roomDAO = roomDatabaseMVVM.roomDAO();
        allRoomDetails = roomDAO.getAllRoomDetails();
    }

    public void insert(RoomModelMVVM roomModelMVVM){
        new RoomInsertAsyncTask(roomDAO).execute(roomModelMVVM);
    }

    public void update(RoomModelMVVM roomModelMVVM){
        new RoomUpdateAsyncTask(roomDAO).execute(roomModelMVVM);
    }

    public void delete(RoomModelMVVM roomModelMVVM){
        new RoomDeleteAsyncTask(roomDAO).execute(roomModelMVVM);
    }

    public void deleteAllRooms(){
        new RoomDeleteAllAsyncTask(roomDAO).execute();
    }

    public LiveData<List<RoomModelMVVM>> getAllRoomDetails() {
        return allRoomDetails;
    }

    //AsyncTask for every operations insert ,update,delete,delete all

    private static class RoomInsertAsyncTask extends AsyncTask<RoomModelMVVM, Void, Void> {
        private RoomDAO roomDAO;

        public RoomInsertAsyncTask(RoomDAO roomDAO){
            this.roomDAO = roomDAO;
        }

        @Override
        protected Void doInBackground(RoomModelMVVM... roomModelMVVMS) {
            roomDAO.insert(roomModelMVVMS[0]);
            return null;
        }
    }

    private static class RoomUpdateAsyncTask extends AsyncTask<RoomModelMVVM, Void, Void> {
        private RoomDAO roomDAO;

        public RoomUpdateAsyncTask(RoomDAO roomDAO){
            this.roomDAO = roomDAO;
        }

        @Override
        protected Void doInBackground(RoomModelMVVM... roomModelMVVMS) {
            roomDAO.update(roomModelMVVMS[0]);
            return null;
        }
    }

    private static class RoomDeleteAsyncTask extends AsyncTask<RoomModelMVVM, Void, Void> {
        private RoomDAO roomDAO;

        public RoomDeleteAsyncTask(RoomDAO roomDAO){
            this.roomDAO = roomDAO;
        }

        @Override
        protected Void doInBackground(RoomModelMVVM... roomModelMVVMS) {
            roomDAO.delete(roomModelMVVMS[0]);
            return null;
        }
    }

    private static class RoomDeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private RoomDAO roomDAO;

        public RoomDeleteAllAsyncTask(RoomDAO roomDAO){
            this.roomDAO = roomDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            roomDAO.deleteAllRooms();
            return null;
        }
    }
}
