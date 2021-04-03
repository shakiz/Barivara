package com.shakil.barivara.mvvm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_table")
public class RoomModelMVVM {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String roomName;
    private String tenantName;
    private String startMonth;
    private String lastPaidMonth;
    private String associateMeter;
    private int advancedAmount;

    public RoomModelMVVM(String roomName, String tenantName, String startMonth, String lastPaidMonth, String associateMeter, int advancedAmount) {
        this.roomName = roomName;
        this.tenantName = tenantName;
        this.startMonth = startMonth;
        this.lastPaidMonth = lastPaidMonth;
        this.associateMeter = associateMeter;
        this.advancedAmount = advancedAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public String getLastPaidMonth() {
        return lastPaidMonth;
    }

    public String getAssociateMeter() {
        return associateMeter;
    }

    public int getAdvancedAmount() {
        return advancedAmount;
    }
}
