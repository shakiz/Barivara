package com.shakil.barivara.model.room;

import android.os.Parcel;
import android.os.Parcelable;

public class Rent implements Parcelable {
    private String RentId;
    private String MonthName;
    private int MonthId;
    private String AssociateRoomName;
    private int AssociateRoomId;
    private int RentAmount;

    public Rent(String rentForMonth, String rentRoom, int rentAmount) {
        this.MonthName = rentForMonth;
        this.AssociateRoomName = rentRoom;
        this.RentAmount = rentAmount;
    }

    public Rent() {

    }

    protected Rent(Parcel in) {
        RentId = in.readString();
        MonthName = in.readString();
        MonthId = in.readInt();
        AssociateRoomName = in.readString();
        AssociateRoomId = in.readInt();
        RentAmount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RentId);
        dest.writeString(MonthName);
        dest.writeInt(MonthId);
        dest.writeString(AssociateRoomName);
        dest.writeInt(AssociateRoomId);
        dest.writeInt(RentAmount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Rent> CREATOR = new Creator<Rent>() {
        @Override
        public Rent createFromParcel(Parcel in) {
            return new Rent(in);
        }

        @Override
        public Rent[] newArray(int size) {
            return new Rent[size];
        }
    };

    public String getRentId() {
        return RentId;
    }

    public void setRentId(String rentId) {
        RentId = rentId;
    }

    public String getMonthName() {
        return MonthName;
    }

    public void setMonthName(String monthName) {
        MonthName = monthName;
    }

    public int getMonthId() {
        return MonthId;
    }

    public void setMonthId(int monthId) {
        MonthId = monthId;
    }

    public String getAssociateRoomName() {
        return AssociateRoomName;
    }

    public void setAssociateRoomName(String associateRoomName) {
        AssociateRoomName = associateRoomName;
    }

    public int getAssociateRoomId() {
        return AssociateRoomId;
    }

    public void setAssociateRoomId(int associateRoomId) {
        AssociateRoomId = associateRoomId;
    }

    public int getRentAmount() {
        return RentAmount;
    }

    public void setRentAmount(int rentAmount) {
        RentAmount = rentAmount;
    }
}
