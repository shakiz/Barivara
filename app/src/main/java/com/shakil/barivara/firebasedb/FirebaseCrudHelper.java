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

    //region fetch table data from firebase db
    private onDataFetch onDataFetch;
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
    //region fetch table data from firebase db
    public ArrayList<Room> fetchAllRoom(String path){
        ArrayList<Room> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Room object = dataSnapshot.getValue(Room.class);
                        Log.i(Constants.TAG+":fetchRoom",""+object.getRoomName());
                        objects.add(object);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
        return objects;
    }
    //region fetch table data from firebase db
    public ArrayList<Rent> fetchAllRent(String path){
        ArrayList<Rent> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Rent object = dataSnapshot.getValue(Rent.class);
                        Log.i(Constants.TAG+":fetchRent",""+object.getMonthName());
                        objects.add(object);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
        return objects;
    }
    //region fetch table data from firebase db
    public ArrayList<ElectricityBill> fetchAllElectricityBills(String path){
        ArrayList<ElectricityBill> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        ElectricityBill object = dataSnapshot.getValue(ElectricityBill.class);
                        Log.i(Constants.TAG+":fetchBill",""+object.getMeterName());
                        objects.add(object);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
        return objects;
    }
    //region fetch table data from firebase db
    public ArrayList<Tenant> fetchAllTenant(String path){
        ArrayList<Tenant> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Tenant object = dataSnapshot.getValue(Tenant.class);
                        Log.i(Constants.TAG+":fetchTenant",""+object.getTenantName());
                        objects.add(object);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
        return objects;
    }
    //endregion
}
