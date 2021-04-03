package com.shakil.barivara.model.room;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private int RoomId;
    private String RoomName;
    private String TenantName;
    private String StartMonthName;
    private int StartMonthId;
    private String AssociateMeterName;
    private int AssociateMeterId;
    private int AdvancedAmount;

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
        RoomId = in.readInt();
        RoomName = in.readString();
        TenantName = in.readString();
        StartMonthName = in.readString();
        StartMonthId = in.readInt();
        AssociateMeterName = in.readString();
        AssociateMeterId = in.readInt();
        AdvancedAmount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RoomId);
        dest.writeString(RoomName);
        dest.writeString(TenantName);
        dest.writeString(StartMonthName);
        dest.writeInt(StartMonthId);
        dest.writeString(AssociateMeterName);
        dest.writeInt(AssociateMeterId);
        dest.writeInt(AdvancedAmount);
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

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
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
}
