package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.databinding.AdapterLayoutElectricityBillListBinding;
import com.shakil.barivara.model.meter.ElectricityBill;

import java.util.ArrayList;

public class RecyclerElectricityBillListAdapter extends RecyclerView.Adapter<RecyclerElectricityBillListAdapter.ElectricityBillViewHolder> {

    private final ArrayList<ElectricityBill> arrayList;

    public RecyclerElectricityBillListAdapter(ArrayList<ElectricityBill> arrayList) {
        this.arrayList = arrayList;
    }

    private ElectricityBillBacks electricityBillBacks;

    public void setElectricityBillBack(ElectricityBillBacks electricityBillBacks) {
        this.electricityBillBacks = electricityBillBacks;
    }

    @NonNull
    @Override
    public ElectricityBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLayoutElectricityBillListBinding binding = AdapterLayoutElectricityBillListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ElectricityBillViewHolder(binding, electricityBillBacks);
    }

    @Override
    public void onBindViewHolder(@NonNull ElectricityBillViewHolder holder, int position) {
        ElectricityBill electricityBill = arrayList.get(position);
        holder.bind(electricityBill);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ElectricityBillViewHolder extends RecyclerView.ViewHolder {
        AdapterLayoutElectricityBillListBinding binding;
        ElectricityBillBacks electricityBillBacks;
        public ElectricityBillViewHolder(@NonNull AdapterLayoutElectricityBillListBinding binding, ElectricityBillBacks electricityBillBacks) {
            super(binding.getRoot());
            this.binding = binding;
            this.electricityBillBacks = electricityBillBacks;
        }

        void bind(ElectricityBill electricityBill){
            binding.MeterId.setText(electricityBill.getMeterName());
            binding.RoomId.setText(electricityBill.getRoomName());
            binding.TotalBill.setText(String.valueOf(electricityBill.getTotalUnit() * electricityBill.getUnitPrice()));
            binding.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (electricityBillBacks != null){
                        electricityBillBacks.onItemClick(electricityBill);
                    }
                }
            });

            if (electricityBillBacks != null){
                binding.DeleteFromCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        electricityBillBacks.onDelete(electricityBill);
                    }
                });
            }
        }
    }

    public interface ElectricityBillBacks {
        void onDelete(ElectricityBill electricityBill);
        void onItemClick(ElectricityBill electricityBill);
    }
}
