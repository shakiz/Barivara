package com.shakil.barivara.firebasedb;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCrudHelper {
    private Context context;
    private DatabaseReference databaseReference;

    public FirebaseCrudHelper(Context context) {
        this.context = context;
    }

    //region add object in firebase
    public void add(Object object, String path){
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        Log.i(Constants.TAG,""+databaseReference.getParent());
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(object);
    }
    //endregion

    //region update object in firebase
    public void update(Object object, String firebaseId, String path){
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        Map<String, Object> postValues = new HashMap<>();
        if (path.equals("tenant")){
            Tenant tenant = (Tenant) object;
            postValues = tenant.toMap();
        }
        else if (path.equals("meter")){
            Meter meter = (Meter) object;
            postValues = meter.toMap();
        }
        else if (path.equals("rent")){
            Rent rent = (Rent) object;
            postValues = rent.toMap();
        }
        else if (path.equals("room")){
            Room room = (Room) object;
            postValues = room.toMap();
        }
        else if (path.equals("electricityBill")){
            ElectricityBill electricityBill = (ElectricityBill) object;
            postValues = electricityBill.toMap();
        }
        databaseReference.child(firebaseId).updateChildren(postValues);
    }
    //endregion

    //region fetch  meter table data from firebase db
    public interface onDataFetch{
        void onFetch(ArrayList<Meter> objects);
    }
    public void fetchAllMeter(String path, onDataFetch onDataFetch){
        ArrayList<Meter> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        Log.i(Constants.TAG,""+databaseReference.getParent());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Meter meter = dataSnapshot.getValue(Meter.class);
                        meter.setFireBaseKey(dataSnapshot.getKey());
                        objects.add(meter);
                        Log.i(Constants.TAG+":fetchMeter",""+meter.getMeterName());
                    }
                    onDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }
    //region fetch room table data from firebase db
    public interface onRoomDataFetch{
        void onFetch(ArrayList<Room> objects);
    }
    public void fetchAllRoom(String path, onRoomDataFetch onRoomDataFetch){
        ArrayList<Room> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Room object = dataSnapshot.getValue(Room.class);
                        object.setFireBaseKey(dataSnapshot.getKey());
                        Log.i(Constants.TAG+":fetchRoom",""+object.getRoomName());
                        objects.add(object);
                    }
                    onRoomDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }
    //region fetch rent table data from firebase db
    public interface onRentDataFetch{
        void onFetch(ArrayList<Rent> objects);
    }
    public void fetchAllRent(String path, onRentDataFetch onRentDataFetch){
        ArrayList<Rent> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Rent object = dataSnapshot.getValue(Rent.class);
                        object.setFireBaseKey(dataSnapshot.getKey());
                        Log.i(Constants.TAG+":fetchRent",""+object.getMonthName());
                        objects.add(object);
                    }
                    onRentDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }
    //region fetch electricity bill table data from firebase db
    public interface onElectricityBillDataFetch{
        void onFetch(ArrayList<ElectricityBill> objects);
    }
    public void fetchAllElectricityBills(String path, onElectricityBillDataFetch onElectricityBillDataFetch){
        ArrayList<ElectricityBill> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        ElectricityBill object = dataSnapshot.getValue(ElectricityBill.class);
                        Log.i(Constants.TAG+":fetchBill",""+object.getMeterName());
                        object.setFireBaseKey(dataSnapshot.getKey());
                        objects.add(object);
                    }
                    onElectricityBillDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }
    //region fetch tenant table data from firebase db
    public interface onTenantDataFetch{
        void onFetch(ArrayList<Tenant> objects);
    }
    public void fetchAllTenant(String path, onTenantDataFetch onTenantDataFetch){
        ArrayList<Tenant> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Tenant object = dataSnapshot.getValue(Tenant.class);
                        object.setFireBaseKey(dataSnapshot.getKey());
                        Log.i(Constants.TAG+":fetchTenant",""+object.getTenantName());
                        objects.add(object);
                    }
                    onTenantDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }
    //endregion
}
