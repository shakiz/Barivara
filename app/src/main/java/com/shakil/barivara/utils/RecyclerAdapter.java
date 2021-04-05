package com.shakil.barivara.utils;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.adapter.RecyclerMeterListAdapter;
import com.shakil.barivara.adapter.RecyclerRoomListAdapter;

public class RecyclerAdapter {
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Context context;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setMeterRecyclerAdapter(RecyclerView recyclerView , int orientation, RecyclerMeterListAdapter recyclerMeterListAdapter){
        layoutManager = new LinearLayoutManager(context,orientation,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerMeterListAdapter);
        recyclerMeterListAdapter.notifyDataSetChanged();
    }

    public void setRoomRecyclerAdapter(RecyclerView recyclerView , int orientation, RecyclerRoomListAdapter recyclerRoomListAdapter){
        layoutManager = new LinearLayoutManager(context,orientation,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerRoomListAdapter);
        recyclerRoomListAdapter.notifyDataSetChanged();
    }
}
