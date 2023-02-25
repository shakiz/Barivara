package com.shakil.barivara.firebasedb;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakil.barivara.R;
import com.shakil.barivara.model.User;
import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.note.Note;
import com.shakil.barivara.model.notification.Notification;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.PrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.shakil.barivara.utils.Constants.mUserId;

public class FirebaseCrudHelper {
    private final Context context;
    private DatabaseReference databaseReference;
    private final PrefManager prefManager;

    public FirebaseCrudHelper(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);
    }

    public void add(Object object, String path){
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        Log.i(Constants.TAG,""+databaseReference.getParent());
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(object);
    }

    public void addNotification(Notification object, String path){
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        Log.i(Constants.TAG,""+databaseReference.getParent());
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(object);
    }

    public void update(Object object, String firebaseId, String path){
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        Map<String, Object> postValues = new HashMap<>();
        if (path.equals("tenant")) {
            Tenant tenant = (Tenant) object;
            postValues = tenant.toMap();
        } else if (path.equals("meter")) {
            Meter meter = (Meter) object;
            postValues = meter.toMap();
        } else if (path.equals("rent")) {
            Rent rent = (Rent) object;
            postValues = rent.toMap();
        } else if (path.equals("room")) {
            Room room = (Room) object;
            postValues = room.toMap();
        } else if (path.equals("note")) {
            Note note = (Note) object;
            postValues = note.toMap();
        } else if (path.equals("electricityBill")) {
            ElectricityBill electricityBill = (ElectricityBill) object;
            postValues = electricityBill.toMap();
        } else if (path.equals("user")) {
            User user = (User) object;
            postValues = user.toMap();
        } else {
            //empty implementation
            Log.i(Constants.TAG,"Update Failed:: Not Table Found");
        }
        databaseReference.child(firebaseId).updateChildren(postValues);
    }

    public void deleteRecord(String path, String firebaseId){
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId)).child(firebaseId);
        databaseReference.removeValue();
    }

    public void fetchProfile(String path, onProfileFetch onProfileFetch){
        ArrayList<User> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        Log.i(Constants.TAG,""+databaseReference.getParent());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        user.setFirebaseKey(dataSnapshot.getKey());
                        objects.add(user);
                        Log.i(Constants.TAG+":fetchProfile",""+user.getFirebaseKey());
                    }
                    if (objects != null && objects.size() > 0) {
                        onProfileFetch.onFetch(objects.get(0));
                    } else {
                        onProfileFetch.onFetch(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }

    public void fetchAllMeter(String path, onDataFetch onDataFetch){
        ArrayList<Meter> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
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

    public void fetchAllRoom(String path, onRoomDataFetch onRoomDataFetch){
        ArrayList<Room> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
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

    public void fetchAllRent(String path, onRentDataFetch onRentDataFetch){
        ArrayList<Rent> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
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

    public void fetchAllElectricityBills(String path, onElectricityBillDataFetch onElectricityBillDataFetch){
        ArrayList<ElectricityBill> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
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

    public void fetchAllTenant(String path, onTenantDataFetch onTenantDataFetch){
        ArrayList<Tenant> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
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

    public void fetchAllNotification(String path, onNotificationDataFetch onNotificationDataFetch){
        ArrayList<Notification> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Notification object = dataSnapshot.getValue(Notification.class);
                        Log.i(Constants.TAG+":fetchAllNotification",""+object.getTitle());
                        objects.add(object);
                    }
                    onNotificationDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }

    public void fetchAllNote(String path, onNoteDataFetch onNoteDataFetch){
        ArrayList<Note> objects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Note object = dataSnapshot.getValue(Note.class);
                        object.setFireBaseKey(dataSnapshot.getKey());
                        Log.i(Constants.TAG+":fetchNote",""+object.getTitle());
                        objects.add(object);
                    }
                    onNoteDataFetch.onFetch(objects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }

    public void getAllName(String path, String fieldName, onNameFetch onNameFetch){
        ArrayList<String> roomNameList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null){
                    roomNameList.add(context.getString(R.string.select_data));
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.i(Constants.TAG,fieldName+ ":" +dataSnapshot.child(fieldName).getValue(String.class));
                        roomNameList.add(dataSnapshot.child(fieldName).getValue(String.class));
                    }
                }
                if (onNameFetch != null){
                    onNameFetch.onFetched(roomNameList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                roomNameList.add(context.getString(R.string.no_data_message));
                if (onNameFetch != null){
                    onNameFetch.onFetched(roomNameList);
                }
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }

    public void getAdditionalInfo(String path, String fieldName, onAdditionalInfoFetch onAdditionalInfoFetch){
        final double[] data = {0};
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.i(Constants.TAG,fieldName+ ":" +dataSnapshot.child(fieldName).getValue(double.class));
                        data[0] = data[0] + (dataSnapshot.child(fieldName).getValue(double.class));
                    }
                }
                if (onAdditionalInfoFetch != null){
                    onAdditionalInfoFetch.onFetched(data[0]);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onAdditionalInfoFetch != null){
                    onAdditionalInfoFetch.onFetched(data[0]);
                }
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }

    public void getSingleColumnValue(String path, String fieldName, onSingleDataFetch onSingleDataFetch){
        final Object[] object = {0};
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null){
                    object[0] = (snapshot.child(fieldName).getValue(Integer.class));
                }
                if (onSingleDataFetch != null){
                    onSingleDataFetch.onFetched(object[0]);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onSingleDataFetch != null){
                    onSingleDataFetch.onFetched(object[0]);
                }
                Log.i(Constants.TAG,""+error.getMessage());
            }
        });
    }

    public void getRentDataByMonth(String path, String queryValue, String fieldName, onDataQuery onDataQuery){
        final int[] object = {0};

        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Rent rent = dataSnapshot.getValue(Rent.class);
                        if (rent.getMonthName().equals(queryValue) || rent.getMonthName().contains(queryValue) ){
                            Log.i(Constants.TAG,fieldName+ ":" +rent.getRentAmount());
                            object[0] = object[0] + rent.getRentAmount();
                        }
                    }
                    Log.i(Constants.TAG,"getDataByQuery: Monthly :"+object[0]);
                }
                if (onDataQuery != null){
                    onDataQuery.onQueryFinished(object[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onDataQuery != null){
                    onDataQuery.onQueryFinished(object[0]);
                }
                Log.i(Constants.TAG,"getDataByQuery: Monthly :"+error.getMessage());
            }
        });
    }

    public void getRentDataByYear(String path, String queryValue, String fieldName, onDataQueryYearlyRent onDataQueryYearlyRent){
        final int[] object = {0};

        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(prefManager.getString(mUserId));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren() != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Rent rent = dataSnapshot.getValue(Rent.class);
                        if (rent.getYearName().equals(queryValue) || rent.getYearName().contains(queryValue) ){
                            Log.i(Constants.TAG,fieldName+ ":" +rent.getRentAmount());
                            object[0] = object[0] + rent.getRentAmount();
                        }
                    }
                    Log.i(Constants.TAG,"getDataByQuery: Yearly :"+object[0]);
                }
                if (onDataQueryYearlyRent != null){
                    onDataQueryYearlyRent.onQueryFinished(object[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onDataQueryYearlyRent != null){
                    onDataQueryYearlyRent.onQueryFinished(object[0]);
                }
                Log.i(Constants.TAG,"getDataByQuery: Yearly :"+error.getMessage());
            }
        });
    }


    public interface onProfileFetch{ void onFetch(User user);}
    public interface onDataFetch{ void onFetch(ArrayList<Meter> objects);}
    public interface onRoomDataFetch{ void onFetch(ArrayList<Room> objects);}
    public interface onRentDataFetch{ void onFetch(ArrayList<Rent> objects);}
    public interface onTenantDataFetch{ void onFetch(ArrayList<Tenant> objects);}
    public interface onElectricityBillDataFetch{ void onFetch(ArrayList<ElectricityBill> objects);}
    public interface onNotificationDataFetch{ void onFetch(ArrayList<Notification> objects);}
    public interface onNoteDataFetch{ void onFetch(ArrayList<Note> objects);}
    public interface onNameFetch{ void onFetched(ArrayList<String> nameList);}
    public interface onAdditionalInfoFetch{ void onFetched(double data);}
    public interface onSingleDataFetch{ void onFetched(Object data);}
    public interface onDataQuery{ void onQueryFinished(int data);}
    public interface onDataQueryYearlyRent{ void onQueryFinished(int data);}
}
