package com.shakil.barivara.model.room;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Room implements Parcelable {
    private String RoomId;
    private String RoomName;
    private String TenantName;
    private String StartMonthName;
    private int StartMonthId;
    private String AssociateMeterName;
    private int AssociateMeterId;
    private int AdvancedAmount;
    private String FireBaseKey;

    public Room() {
    }

    public Room(String roomName, String tenantName, String startMonth, String associateMeter, int advancedAmount) {
        this.RoomName = roomName;
        this.TenantName = tenantName;
        this.StartMonthName = startMonth;
        this.AssociateMeterName = associateMeter;
        this.AdvancedAmount = advancedAmount;
    }

    public Room(String roomName, String tenantName, String startMonth) {
        this.RoomName = roomName;
        this.TenantName = tenantName;
        this.StartMonthName = startMonth;
    }

    protected Room(Parcel in) {
        RoomId = in.readString();
        RoomName = in.readString();
        TenantName = in.readString();
        StartMonthName = in.readString();
        StartMonthId = in.readInt();
        AssociateMeterName = in.readString();
        AssociateMeterId = in.readInt();
        AdvancedAmount = in.readInt();
        FireBaseKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RoomId);
        dest.writeString(RoomName);
        dest.writeString(TenantName);
        dest.writeString(StartMonthName);
        dest.writeInt(StartMonthId);
        dest.writeString(AssociateMeterName);
        dest.writeInt(AssociateMeterId);
        dest.writeInt(AdvancedAmount);
        dest.writeString(FireBaseKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public String getTenantName() {
        return TenantName;
    }

    public void setTenantName(String tenantName) {
        TenantName = tenantName;
    }

    public String getStartMonthName() {
        return StartMonthName;
    }

    public void setStartMonthName(String startMonthName) {
        StartMonthName = startMonthName;
    }

    public int getStartMonthId() {
        return StartMonthId;
    }

    public void setStartMonthId(int startMonthId) {
        StartMonthId = startMonthId;
    }

    public String getAssociateMeterName() {
        return AssociateMeterName;
    }

    public void setAssociateMeterName(String associateMeterName) {
        AssociateMeterName = associateMeterName;
    }

    public int getAssociateMeterId() {
        return AssociateMeterId;
    }

    public void setAssociateMeterId(int associateMeterId) {
        AssociateMeterId = associateMeterId;
    }

    public int getAdvancedAmount() {
        return AdvancedAmount;
    }

    public void setAdvancedAmount(int advancedAmount) {
        AdvancedAmount = advancedAmount;
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
        result.put("roomId", RoomId);
        result.put("roomName", RoomName);
        result.put("tenantName", TenantName);
        result.put("startMonthName", StartMonthName);
        result.put("startMonthId", StartMonthId);
        result.put("associateMeterName", AssociateMeterName);
        result.put("associateMeterId", AssociateMeterId);
        result.put("advancedAmount", AdvancedAmount);

        return result;
    }
}
