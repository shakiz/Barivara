package com.shakil.barivara.model.bill;

public class GenerateBill {
    private String TenantName;
    private String MobileNo;
    private String MonthAndYear;
    private String AssociateRoom;
    private int RentAmount;
    private int GasBill;
    private int WaterBill;
    private int ElectricityBill;
    private int ServiceCharge;

    public GenerateBill(String tenantName, String mobileNo, String monthAndYear, String associateRoom, int rentAmount, int gasBill, int waterBill, int electricityBill, int serviceCharge) {
        TenantName = tenantName;
        MobileNo = mobileNo;
        MonthAndYear = monthAndYear;
        AssociateRoom = associateRoom;
        RentAmount = rentAmount;
        GasBill = gasBill;
        WaterBill = waterBill;
        ElectricityBill = electricityBill;
        ServiceCharge = serviceCharge;
    }

    public String getTenantName() {
        return TenantName;
    }

    public void setTenantName(String tenantName) {
        TenantName = tenantName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getMonthAndYear() {
        return MonthAndYear;
    }

    public void setMonthAndYear(String monthAndYear) {
        MonthAndYear = monthAndYear;
    }

    public String getAssociateRoom() {
        return AssociateRoom;
    }

    public void setAssociateRoom(String associateRoom) {
        AssociateRoom = associateRoom;
    }

    public int getRentAmount() {
        return RentAmount;
    }

    public void setRentAmount(int rentAmount) {
        RentAmount = rentAmount;
    }

    public int getGasBill() {
        return GasBill;
    }

    public void setGasBill(int gasBill) {
        GasBill = gasBill;
    }

    public int getWaterBill() {
        return WaterBill;
    }

    public void setWaterBill(int waterBill) {
        WaterBill = waterBill;
    }

    public int getElectricityBill() {
        return ElectricityBill;
    }

    public void setElectricityBill(int electricityBill) {
        ElectricityBill = electricityBill;
    }

    public int getServiceCharge() {
        return ServiceCharge;
    }

    public void setServiceCharge(int serviceCharge) {
        ServiceCharge = serviceCharge;
    }
}
