package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.databinding.AdapterLayoutRoomListBinding;
import com.shakil.barivara.model.room.Room;

import java.util.ArrayList;

public class RecyclerRoomListAdapter extends RecyclerView.Adapter<RecyclerRoomListAdapter.RoomItemViewHolder> {

    private final ArrayList<Room> arrayList;

    public RecyclerRoomListAdapter(ArrayList<Room> arrayList) {
        this.arrayList = arrayList;
    }

    public RoomCallBacks roomCallBacks;

    public void setRoomCallBack(RoomCallBacks roomCallBacks) {
        this.roomCallBacks = roomCallBacks;
    }

    @NonNull
    @Override
    public RoomItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLayoutRoomListBinding binding = AdapterLayoutRoomListBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);
        return new RoomItemViewHolder(binding, roomCallBacks);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomItemViewHolder holder, int position) {
        Room room = arrayList.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RoomItemViewHolder extends RecyclerView.ViewHolder {
        AdapterLayoutRoomListBinding binding;
        RoomCallBacks roomCallBacks;
        public RoomItemViewHolder(@NonNull AdapterLayoutRoomListBinding binding, RoomCallBacks roomCallBacks) {
            super(binding.getRoot());
            this.binding = binding;
            this.roomCallBacks = roomCallBacks;
        }

        void bind(Room room){
            binding.roomName.setText(room.getRoomName());
            binding.roomOwner.setText(room.getTenantName());
            binding.startMonth.setText(room.getStartMonthName());
            binding.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (roomCallBacks != null){
                        roomCallBacks.onItemClick(room);
                    }
                }
            });

            binding.editDeleteIncludeLayout.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (roomCallBacks != null) roomCallBacks.onEdit(room);
                }
            });
            binding.editDeleteIncludeLayout.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (roomCallBacks != null) roomCallBacks.onDelete(room);
                }
            });
        }
    }

    public interface RoomCallBacks {
        void onDelete(Room room);
        void onEdit(Room room);
        void onItemClick(Room room);
    }
}
