package com.shakil.barivara.model.room;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Rent implements Parcelable {
    private String RentId;
    private String MonthName;
    private int MonthId;
    private String YearName;
    private int YearId;
    private String AssociateRoomName;
    private int AssociateRoomId;
    private int RentAmount;
    private String FireBaseKey;

    public Rent() {

    }

    protected Rent(Parcel in) {
        RentId = in.readString();
        MonthName = in.readString();
        MonthId = in.readInt();
        YearName = in.readString();
        YearId = in.readInt();
        AssociateRoomName = in.readString();
        AssociateRoomId = in.readInt();
        RentAmount = in.readInt();
        FireBaseKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RentId);
        dest.writeString(MonthName);
        dest.writeInt(MonthId);
        dest.writeString(YearName);
        dest.writeInt(YearId);
        dest.writeString(AssociateRoomName);
        dest.writeInt(AssociateRoomId);
        dest.writeInt(RentAmount);
        dest.writeString(FireBaseKey);
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

    public String getYearName() {
        return YearName;
    }

    public void setYearName(String yearName) {
        YearName = yearName;
    }

    public int getYearId() {
        return YearId;
    }

    public void setYearId(int yearId) {
        YearId = yearId;
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

    public String getFireBaseKey() {
        return FireBaseKey;
    }

    public void setFireBaseKey(String fireBaseKey) {
        FireBaseKey = fireBaseKey;
    }

    public static Creator<Rent> getCREATOR() {
        return CREATOR;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("rentId", RentId);
        result.put("monthName", MonthName);
        result.put("monthId", MonthId);
        result.put("yearName", YearName);
        result.put("yearId", YearId);
        result.put("associateRoomName", AssociateRoomName);
        result.put("associateRoomId", AssociateRoomId);
        result.put("rentAmount", RentAmount);

        return result;
    }
}
