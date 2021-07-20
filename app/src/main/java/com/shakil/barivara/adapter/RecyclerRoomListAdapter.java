package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.room.Room;

import java.util.ArrayList;

public class RecyclerRoomListAdapter extends RecyclerView.Adapter<RecyclerRoomListAdapter.ViewHolder> {

    private ArrayList<Room> arrayList;

    public RecyclerRoomListAdapter(ArrayList<Room> arrayList) {
        this.arrayList = arrayList;
    }

    //region click adapter
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(Room room);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, ownerName, startDate;
        CardView item_card_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            ownerName = itemView.findViewById(R.id.roomOwner);
            startDate = itemView.findViewById(R.id.startMonth);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }
}
