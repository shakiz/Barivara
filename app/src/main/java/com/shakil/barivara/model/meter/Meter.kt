package com.shakil.barivara.model.meter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Meter implements Parcelable {
    private String MeterId;
    private String MeterName;
    private String AssociateRoom;
    private int AssociateRoomId;
    private String MeterTypeName;
    private int MeterTypeId;
    private int PresentUnit;
    private int PastUnit;
    private String FireBaseKey;

    public Meter(String meterName, String associateRoom, String meterType, int presentUnit, int pastUnit) {
        this.MeterName = meterName;
        this.AssociateRoom = associateRoom;
        this.MeterTypeName = meterType;
        this.PresentUnit = presentUnit;
        this.PastUnit = pastUnit;
    }

    public Meter(String meterName, String associateRoom, String meterType) {
        this.MeterName = meterName;
        this.AssociateRoom = associateRoom;
        this.MeterTypeName = meterType;
    }

    public Meter(String meterName, String owner, int presentUnit, int pastUnit) {
        this.MeterName = meterName;
        this.PresentUnit = presentUnit;
        this.PastUnit = pastUnit;
    }

    public Meter() {

    }

    protected Meter(Parcel in) {
        MeterId = in.readString();
        MeterName = in.readString();
        AssociateRoom = in.readString();
        AssociateRoomId = in.readInt();
        MeterTypeName = in.readString();
        MeterTypeName = in.readString();
        MeterTypeId = in.readInt();
        PresentUnit = in.readInt();
        PastUnit = in.readInt();
        FireBaseKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MeterId);
        dest.writeString(MeterName);
        dest.writeString(AssociateRoom);
        dest.writeInt(AssociateRoomId);
        dest.writeString(MeterTypeName);
        dest.writeString(MeterTypeName);
        dest.writeInt(MeterTypeId);
        dest.writeInt(PresentUnit);
        dest.writeInt(PastUnit);
        dest.writeString(FireBaseKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Meter> CREATOR = new Creator<Meter>() {
        @Override
        public Meter createFromParcel(Parcel in) {
            return new Meter(in);
        }

        @Override
        public Meter[] newArray(int size) {
            return new Meter[size];
        }
    };

    public String getMeterId() {
        return MeterId;
    }

    public void setMeterId(String meterId) {
        MeterId = meterId;
    }

    public String getMeterName() {
        return MeterName;
    }

    public void setMeterName(String meterName) {
        MeterName = meterName;
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

    public String getMeterTypeName() {
        return MeterTypeName;
    }

    public void setMeterTypeName(String meterTypeName) {
        MeterTypeName = meterTypeName;
    }

    public int getMeterTypeId() {
        return MeterTypeId;
    }

    public void setMeterTypeId(int meterTypeId) {
        MeterTypeId = meterTypeId;
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


    public String getFireBaseKey() {
        return FireBaseKey;
    }

    public void setFireBaseKey(String fireBaseKey) {
        FireBaseKey = fireBaseKey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("meterId", MeterId);
        result.put("meterName", MeterName);
        result.put("associateRoom", AssociateRoom);
        result.put("associateRoomId", AssociateRoomId);
        result.put("meterTypeName", MeterTypeName);
        result.put("meterTypeId", MeterTypeId);
        result.put("presentUnit", PresentUnit);
        result.put("pastUnit", PastUnit);

        return result;
    }
}
