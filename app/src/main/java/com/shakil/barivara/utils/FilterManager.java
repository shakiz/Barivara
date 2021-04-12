package com.shakil.barivara.utils;

import android.content.Context;

import com.shakil.barivara.model.room.Room;

import java.util.ArrayList;

public class FilterManager{
    private Context context;

    public FilterManager(Context context) {
        this.context = context;
    }

    public interface onFilterClick{
        void onClick(ArrayList<Room> objects);
    }
    public void onFilterClick(String searchText, ArrayList<Room> sourceList, onFilterClick onFilterClick){
        for (int start = 0; start < sourceList.size(); start++) {
            if (sourceList.get(start).getRoomName().contains(searchText)){
                sourceList.remove(start);
                if (onFilterClick != null){
                    onFilterClick.onClick(sourceList);
                }
                break;
            }
        }
    }
}
