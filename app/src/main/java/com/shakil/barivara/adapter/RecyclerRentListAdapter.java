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
import com.shakil.barivara.model.room.Room;

import java.util.ArrayList;

public class RecyclerRentListAdapter extends RecyclerView.Adapter<RecyclerRentListAdapter.ViewHolder> {

    private final ArrayList<Rent> arrayList;

    public RecyclerRentListAdapter(ArrayList<Rent> arrayList) {
        this.arrayList = arrayList;
    }

    public onItemClickListener onItemClickListener;
    public onEditListener onEditListener;
    public onDeleteListener onDeleteListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void onEditListener(onEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }
    public void onDeleteListener(onDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
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
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(rent);
                }
            }
        });
        holder.listCount.setText(""+(position+1));

        //region edit and delete click listeners
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditListener != null) onEditListener.onEdit(rent);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) onDeleteListener.onDelete(rent);
            }
        });
        //endregion
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

    public interface onItemClickListener{ void onItemClick(Rent rent);}
    public interface onEditListener{ void onEdit(Rent rent);}
    public interface onDeleteListener{ void onDelete(Rent rent);}
}
