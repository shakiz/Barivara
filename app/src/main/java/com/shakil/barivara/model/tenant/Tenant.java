package com.shakil.barivara.model.tenant;

import android.os.Parcel;
import android.os.Parcelable;

public class Tenant implements Parcelable {
    private String TenantId;
    private String TenantName;
    private String StartingMonth;
    private int StartingMonthId;
    private String AssociateRoom;
    private int AssociateRoomId;

    public Tenant() {
    }

    protected Tenant(Parcel in) {
        TenantId = in.readString();
        TenantName = in.readString();
        StartingMonth = in.readString();
        StartingMonthId = in.readInt();
        AssociateRoom = in.readString();
        AssociateRoomId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TenantId);
        dest.writeString(TenantName);
        dest.writeString(StartingMonth);
        dest.writeInt(StartingMonthId);
        dest.writeString(AssociateRoom);
        dest.writeInt(AssociateRoomId);
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
}
