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
import com.shakil.barivara.model.room.Rent;

import java.util.ArrayList;

public class RecyclerRentListAdapter extends RecyclerView.Adapter<RecyclerRentListAdapter.ViewHolder> {

    private final ArrayList<Rent> arrayList;

    public RecyclerRentListAdapter(ArrayList<Rent> arrayList) {
        this.arrayList = arrayList;
    }

    public RentCallBacks rentCallBacks;

    public void setRentCallBack(RentCallBacks rentCallBacks) {
        this.rentCallBacks = rentCallBacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout_rent_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rent rent = arrayList.get(position);
        holder.MonthName.setText(rent.getMonthName());
        holder.AssociateRoomName.setText(rent.getAssociateRoomName());
        holder.RentAmount.setText(String.valueOf(rent.getRentAmount()));
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rentCallBacks != null){
                    rentCallBacks.onItemClick(rent);
                }
            }
        });
        holder.listCount.setText(""+(position+1));

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rentCallBacks != null) rentCallBacks.onEdit(rent);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rentCallBacks != null) rentCallBacks.onDelete(rent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView MonthName, AssociateRoomName, RentAmount;
        CardView item_card_view;
        TextView listCount;
        Button editButton, deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MonthName = itemView.findViewById(R.id.MonthName);
            AssociateRoomName = itemView.findViewById(R.id.AssociateRoomName);
            RentAmount = itemView.findViewById(R.id.RentAmount);
            listCount = itemView.findViewById(R.id.listCount);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }

    public interface RentCallBacks {
        void onDelete(Rent rent);

        void onEdit(Rent rent);

        void onItemClick(Rent rent);
    }
}
