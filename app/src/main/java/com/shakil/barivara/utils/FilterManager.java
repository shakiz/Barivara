package com.shakil.barivara.utils;

import android.content.Context;

import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;

import java.util.ArrayList;

public class FilterManager{
    private Context context;

    public FilterManager(Context context) {
        this.context = context;
    }

    //region room search listener
    public interface onFilterClick{
        void onClick(ArrayList<Room> objects);
    }
    public void onFilterClick(String searchText, ArrayList<Room> sourceList, onFilterClick onFilterClick){
        ArrayList<Room> roomList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getRoomName().contains(searchText)){
                roomList.add(sourceList.get(start));
            }
        }
        if (onFilterClick != null){
            onFilterClick.onClick(roomList);
        }
    }
    //endregion

    //region rent search listener
    public interface onRentFilterClick{
        void onClick(ArrayList<Rent> objects);
    }
    public void onFilterClick(String searchText, ArrayList<Rent> sourceList, onRentFilterClick onRentFilterClick){
        ArrayList<Rent> rentList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getMonthName().contains(searchText)){
                rentList.add(sourceList.get(start));
            }
        }
        if (onRentFilterClick != null){
            onRentFilterClick.onClick(rentList);
        }
    }
    //endregion

    //region meter search listener
    public interface onMeterFilterClick{
        void onClick(ArrayList<Meter> objects);
    }
    public void onFilterClick(String searchText, ArrayList<Meter> sourceList, onMeterFilterClick onMeterFilterClick){
        ArrayList<Meter> meterList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getMeterName().contains(searchText)){
                meterList.add(sourceList.get(start));
            }
        }
        if (onMeterFilterClick != null){
            onMeterFilterClick.onClick(meterList);
        }
    }
    //endregion

    //region electricity bill search listener
    public interface onBillFilterClick{
        void onClick(ArrayList<ElectricityBill> objects);
    }
    public void onFilterClick(String searchText, ArrayList<ElectricityBill> sourceList, onBillFilterClick onBillFilterClick){
        ArrayList<ElectricityBill> billList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getRoomName().contains(searchText)){
                billList.add(sourceList.get(start));
            }
        }
        if (onBillFilterClick != null){
            onBillFilterClick.onClick(billList);
        }
    }
    //endregion

    //region tenant search listener
    public interface onTenantFilterClick{
        void onClick(ArrayList<Tenant> objects);
    }
    public void onFilterClick(String searchText, ArrayList<Tenant> sourceList, onTenantFilterClick onTenantFilterClick){
        ArrayList<Tenant> billList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getTenantName().contains(searchText)){
                billList.add(sourceList.get(start));
            }
        }
        if (onTenantFilterClick != null){
            onTenantFilterClick.onClick(billList);
        }
    }
    //endregion
}
