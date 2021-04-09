package com.shakil.barivara.model.tenant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Tenant implements Parcelable {
    private String TenantId;
    private String TenantName;
    private String StartingMonth;
    private int StartingMonthId;
    private String AssociateRoom;
    private int AssociateRoomId;
    private String FireBaseKey;

    public Tenant() {
    }

    protected Tenant(Parcel in) {
        TenantId = in.readString();
        TenantName = in.readString();
        StartingMonth = in.readString();
        StartingMonthId = in.readInt();
        AssociateRoom = in.readString();
        AssociateRoomId = in.readInt();
        FireBaseKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TenantId);
        dest.writeString(TenantName);
        dest.writeString(StartingMonth);
        dest.writeInt(StartingMonthId);
        dest.writeString(AssociateRoom);
        dest.writeInt(AssociateRoomId);
        dest.writeString(FireBaseKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tenant> CREATOR = new Creator<Tenant>() {
        @Override
        public Tenant createFromParcel(Parcel in) {
            return new Tenant(in);
        }

        @Override
        public Tenant[] newArray(int size) {
            return new Tenant[size];
        }
    };

    public String getTenantId() {
        return TenantId;
    }

    public void setTenantId(String tenantId) {
        TenantId = tenantId;
    }

    public String getTenantName() {
        return TenantName;
    }

    public void setTenantName(String tenantName) {
        TenantName = tenantName;
    }

    public String getStartingMonth() {
        return StartingMonth;
    }

    public void setStartingMonth(String startingMonth) {
        StartingMonth = startingMonth;
    }

    public int getStartingMonthId() {
        return StartingMonthId;
    }

    public void setStartingMonthId(int startingMonthId) {
        StartingMonthId = startingMonthId;
    }

    public String getAssociateRoom() {
        return AssociateRoom;
    }

    public void setAssociateRoom(String associateRoom) {
        AssociateRoom = associateRoom;
    }

    public int getAssociateRoomId() {
        return AssociateRoomId;
    }

    public void setAssociateRoomId(int associateRoomId) {
        AssociateRoomId = associateRoomId;
    }

    public String getFireBaseKey() {
        return FireBaseKey;
    }

    public void setFireBaseKey(String fireBaseKey) {
        FireBaseKey = fireBaseKey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tenantId", TenantId);
        result.put("tenantName", TenantName);
        result.put("startingMonth", StartingMonth);
        result.put("startingMonthId", StartingMonthId);
        result.put("associateRoom", AssociateRoom);
        result.put("associateRoomId", AssociateRoomId);

        return result;
    }
}
