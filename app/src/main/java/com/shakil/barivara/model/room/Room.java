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
    private int TenantNameId;
    private int NoOfRoom;
    private int NoOfBathroom;
    private int NoOfBalcony;
    private String StartMonthName;
    private int StartMonthId;
    private String AssociateMeterName;
    private int AssociateMeterId;
    private int AdvancedAmount;
    private String FireBaseKey;

    public Room() {
    }

    protected Room(Parcel in) {
        RoomId = in.readString();
        RoomName = in.readString();
        TenantName = in.readString();
        TenantNameId = in.readInt();
        NoOfRoom = in.readInt();
        NoOfBathroom = in.readInt();
        NoOfBalcony = in.readInt();
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
        dest.writeInt(TenantNameId);
        dest.writeInt(NoOfRoom);
        dest.writeInt(NoOfBathroom);
        dest.writeInt(NoOfBalcony);
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

    public int getTenantNameId() {
        return TenantNameId;
    }

    public void setTenantNameId(int tenantNameId) {
        TenantNameId = tenantNameId;
    }

    public int getNoOfRoom() {
        return NoOfRoom;
    }

    public void setNoOfRoom(int noOfRoom) {
        NoOfRoom = noOfRoom;
    }

    public int getNoOfBathroom() {
        return NoOfBathroom;
    }

    public void setNoOfBathroom(int noOfBathroom) {
        NoOfBathroom = noOfBathroom;
    }

    public int getNoOfBalcony() {
        return NoOfBalcony;
    }

    public void setNoOfBalcony(int noOfBalcony) {
        NoOfBalcony = noOfBalcony;
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

    public static Creator<Room> getCREATOR() {
        return CREATOR;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("roomId", RoomId);
        result.put("roomName", RoomName);
        result.put("tenantName", TenantName);
        result.put("tenantNameId", TenantNameId);
        result.put("startMonthName", StartMonthName);
        result.put("startMonthId", StartMonthId);
        result.put("associateMeterName", AssociateMeterName);
        result.put("associateMeterId", AssociateMeterId);
        result.put("advancedAmount", AdvancedAmount);
        result.put("noOfRoom", NoOfRoom);
        result.put("noOfBathroom", NoOfBathroom);
        result.put("noOfBalcony", NoOfBalcony);

        return result;
    }
}
