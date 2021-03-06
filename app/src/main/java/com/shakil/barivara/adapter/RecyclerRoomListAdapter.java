package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.room.Room;

import java.util.ArrayList;

public class RecyclerRoomListAdapter extends RecyclerView.Adapter<RecyclerRoomListAdapter.ViewHolder> {

    private final ArrayList<Room> arrayList;

    public RecyclerRoomListAdapter(ArrayList<Room> arrayList) {
        this.arrayList = arrayList;
    }

    //region edit, delete and item click interfaces
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(Room room);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public onEditListener onEditListener;
    public interface onEditListener{
        void onEdit(Room room);
    }

    public void onEditListener(onEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }
    public onDeleteListener onDeleteListener;
    public interface onDeleteListener{
        void onDelete(Room room);
    }

    public void onDeleteListener(onDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
    //endregion

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout_room_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = arrayList.get(position);
        holder.roomName.setText(room.getRoomName());
        holder.ownerName.setText(room.getTenantName());
        holder.startDate.setText(room.getStartMonthName());
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(room);
                }
            }
        });
        holder.listCount.setText(""+(position+1));

        //region edit and delete click listeners
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditListener != null) onEditListener.onEdit(room);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) onDeleteListener.onDelete(room);
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, ownerName, startDate;
        CardView item_card_view;
        TextView listCount;
        Button editButton, deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            ownerName = itemView.findViewById(R.id.roomOwner);
            startDate = itemView.findViewById(R.id.startMonth);
            listCount = itemView.findViewById(R.id.listCount);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }
}
