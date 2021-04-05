package com.shakil.barivara.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.room.Rent;

import java.util.ArrayList;

public class RecyclerRentListAdapter extends RecyclerView.Adapter<RecyclerRentListAdapter.ViewHolder> {

    private ArrayList<Rent> arrayList;
    private Context context;

    public RecyclerRentListAdapter(ArrayList<Rent> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    //region click adapter
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(Rent rent);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_layout_rent_list,parent,false);
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MonthName, AssociateRoomName, RentAmount;
        CardView item_card_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MonthName = itemView.findViewById(R.id.MonthName);
            AssociateRoomName = itemView.findViewById(R.id.AssociateRoomName);
            RentAmount = itemView.findViewById(R.id.RentAmount);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }
}
