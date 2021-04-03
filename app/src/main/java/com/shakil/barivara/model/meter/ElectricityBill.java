package com.shakil.barivara.model.meter;

import android.os.Parcel;
import android.os.Parcelable;

public class ElectricityBill implements Parcelable {
    private int BillId;
    private int MeterId;
    private String MeterName;
    private int RoomId;
    private String RoomName;
    private double UnitPrice;
    private int PresentUnit;
    private int PastUnit;
    private int TotalUnit;
    private int TotalBill;

    public ElectricityBill() {
    }

    protected ElectricityBill(Parcel in) {
        BillId = in.readInt();
        MeterId = in.readInt();
        MeterName = in.readString();
        RoomId = in.readInt();
        RoomName = in.readString();
        UnitPrice = in.readDouble();
        PresentUnit = in.readInt();
        PastUnit = in.readInt();
        TotalUnit = in.readInt();
        TotalBill = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(BillId);
        dest.writeInt(MeterId);
        dest.writeString(MeterName);
        dest.writeInt(RoomId);
        dest.writeString(RoomName);
        dest.writeDouble(UnitPrice);
        dest.writeInt(PresentUnit);
        dest.writeInt(PastUnit);
        dest.writeInt(TotalUnit);
        dest.writeInt(TotalBill);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ElectricityBill> CREATOR = new Creator<ElectricityBill>() {
        @Override
        public ElectricityBill createFromParcel(Parcel in) {
            return new ElectricityBill(in);
        }

        @Override
        public ElectricityBill[] newArray(int size) {
            return new ElectricityBill[size];
        }
    };

    public int getBillId() {
        return BillId;
    }

    public void setBillId(int billId) {
        BillId = billId;
    }

    public int getMeterId() {
        return MeterId;
    }

    public void setMeterId(int meterId) {
        MeterId = meterId;
    }

    public String getMeterName() {
        return MeterName;
    }

    public void setMeterName(String meterName) {
        MeterName = meterName;
    }

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

    public double getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        UnitPrice = unitPrice;
    }

    public int getPresentUnit() {
        return PresentUnit;
    }

    public void setPresentUnit(int presentUnit) {
        PresentUnit = presentUnit;
    }

    public int getPastUnit() {
        return PastUnit;
    }

    public void setPastUnit(int pastUnit) {
        PastUnit = pastUnit;
    }

    public int getTotalUnit() {
        return TotalUnit;
    }

    public void setTotalUnit(int totalUnit) {
        TotalUnit = totalUnit;
    }

    public int getTotalBill() {
        return TotalBill;
    }

    public void setTotalBill(int totalBill) {
        TotalBill = totalBill;
    }
}
