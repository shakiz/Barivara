package com.shakil.barivara.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.meter.ElectricityBill;

import java.util.ArrayList;

public class RecyclerElectricityBillListAdapter extends RecyclerView.Adapter<RecyclerElectricityBillListAdapter.ViewHolder> {

    private ArrayList<ElectricityBill> arrayList;
    private Context context;

    public RecyclerElectricityBillListAdapter(ArrayList<ElectricityBill> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    //region click adapter
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(ElectricityBill electricityBill);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    //region click listener
    private onRemoveClick onRemoveClick;
    public interface onRemoveClick {
        void itemClick(ElectricityBill electricityBill);
    }

    public void setOnRemoveClick(onRemoveClick onRemoveClick) {
        this.onRemoveClick = onRemoveClick;
    }
    //endregion

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_layout_electricity_bill_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ElectricityBill electricityBill = arrayList.get(position);
        holder.MeterId.setText(electricityBill.getMeterName());
        holder.RoomId.setText(electricityBill.getRoomName());
        holder.TotalBill.setText(String.valueOf(electricityBill.getTotalUnit() * electricityBill.getUnitPrice()));
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(electricityBill);
                }
            }
        });

        //remove product from cart
        if (onRemoveClick != null){
            holder.DeleteFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRemoveClick.itemClick(electricityBill);
                }
            });
        }
        //endregion
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MeterId, RoomId, TotalBill;
        CardView item_card_view;
        ImageView DeleteFromCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MeterId = itemView.findViewById(R.id.MeterId);
            RoomId = itemView.findViewById(R.id.RoomId);
            TotalBill = itemView.findViewById(R.id.TotalBill);
            item_card_view = itemView.findViewById(R.id.item_card_view);
            DeleteFromCart = itemView.findViewById(R.id.DeleteFromCart);
        }
    }
}
