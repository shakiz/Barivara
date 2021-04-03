package com.shakil.barivara.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shakil.homeapp.activities.model.tenant.Tenant;

public class FirebaseCRUDHelperTenant {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    public FirebaseCRUDHelperTenant() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("tenants");
    }

    public void insert(String userID, Tenant tenant) {
        //databaseReference.child("tenant").child(userID).child("TenantId").setValue(tenant.getTenantId());
        databaseReference.child("tenant").child(userID).child("TenantName").setValue(tenant.getTenantName());
        databaseReference.child("tenant").child(userID).child("StartingMonth").setValue(tenant.getStartingMonth());
        databaseReference.child("tenant").child(userID).child("StartingMonthId").setValue(tenant.getStartingMonthId());
        databaseReference.child("tenant").child(userID).child("AssociateRoom").setValue(tenant.getAssociateRoom());
        databaseReference.child("tenant").child(userID).child("AssociateRoomId").setValue(tenant.getAssociateRoomId());
    }

    public void select() {

    }

    public void update(Tenant tenant) {

    }

    public void delete(Tenant tenant) {

    }
}
