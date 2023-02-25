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
import com.shakil.barivara.model.meter.Meter;

import java.util.ArrayList;

public class RecyclerMeterListAdapter extends RecyclerView.Adapter<RecyclerMeterListAdapter.ViewHolder> {

    private final ArrayList<Meter> arrayList;

    public RecyclerMeterListAdapter(ArrayList<Meter> arrayList) {
        this.arrayList = arrayList;
    }

    public onItemClickListener onItemClickListener;
    public onDeleteListener onDeleteListener;
    public onEditListener onEditListener;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout_meter_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meter meter = arrayList.get(position);
        holder.meterName.setText(meter.getMeterName());
        holder.roomName.setText(meter.getAssociateRoom());
        holder.meterType.setText(meter.getMeterTypeName());
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(meter);
                }
            }
        });
        holder.listCount.setText(""+(position+1));

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditListener != null) onEditListener.onEdit(meter);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) onDeleteListener.onDelete(meter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView meterName, roomName, meterType;
        CardView item_card_view;
        TextView listCount;
        Button editButton, deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            meterName = itemView.findViewById(R.id.meterName);
            roomName = itemView.findViewById(R.id.roomName);
            meterType = itemView.findViewById(R.id.meterType);
            listCount = itemView.findViewById(R.id.listCount);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }

    public interface onItemClickListener{ void onItemClick(Meter meter);}
    public interface onEditListener{ void onEdit(Meter meter);}
    public interface onDeleteListener{ void onDelete(Meter meter);}
}
