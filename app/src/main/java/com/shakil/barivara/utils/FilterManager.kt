package com.shakil.barivara.utils;

import com.shakil.barivara.model.meter.ElectricityBill;
import com.shakil.barivara.model.meter.Meter;
import com.shakil.barivara.model.room.Rent;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import java.util.ArrayList;

public class FilterManager{

    public void onFilterClick(String searchText, ArrayList<Room> sourceList, onFilterClick onFilterClick){
        ArrayList<Room> roomList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getRoomName().toLowerCase().contains(searchText.toLowerCase())){
                roomList.add(sourceList.get(start));
            }
        }
        if (onFilterClick != null){
            onFilterClick.onClick(roomList);
        }
    }

    public void onFilterClick(String searchText, ArrayList<Rent> sourceList, onRentFilterClick onRentFilterClick){
        ArrayList<Rent> rentList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getMonthName().toLowerCase().contains(searchText.toLowerCase())){
                rentList.add(sourceList.get(start));
            }
        }
        if (onRentFilterClick != null){
            onRentFilterClick.onClick(rentList);
        }
    }

    public void onFilterClick(String searchText, ArrayList<Meter> sourceList, onMeterFilterClick onMeterFilterClick){
        ArrayList<Meter> meterList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getMeterName().toLowerCase().contains(searchText.toLowerCase())){
                meterList.add(sourceList.get(start));
            }
        }
        if (onMeterFilterClick != null){
            onMeterFilterClick.onClick(meterList);
        }
    }

    public void onFilterClick(String searchText, ArrayList<ElectricityBill> sourceList, onBillFilterClick onBillFilterClick){
        ArrayList<ElectricityBill> billList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getRoomName().toLowerCase().contains(searchText.toLowerCase())){
                billList.add(sourceList.get(start));
            }
        }
        if (onBillFilterClick != null){
            onBillFilterClick.onClick(billList);
        }
    }

    public void onFilterClick(String searchText, ArrayList<Tenant> sourceList, onTenantFilterClick onTenantFilterClick){
        ArrayList<Tenant> billList = new ArrayList<>();
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getTenantName().toLowerCase().contains(searchText.toLowerCase())){
                billList.add(sourceList.get(start));
            }
        }
        if (onTenantFilterClick != null){
            onTenantFilterClick.onClick(billList);
        }
    }

    public interface onRentFilterClick{ void onClick(ArrayList<Rent> objects);}
    public interface onFilterClick{ void onClick(ArrayList<Room> objects);}
    public interface onMeterFilterClick{ void onClick(ArrayList<Meter> objects);}
    public interface onTenantFilterClick{ void onClick(ArrayList<Tenant> objects);}
    public interface onBillFilterClick{ void onClick(ArrayList<ElectricityBill> objects);}

}
