package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.databinding.AdapterLayoutMeterListBinding;
import com.shakil.barivara.model.meter.Meter;

import java.util.ArrayList;

public class RecyclerMeterListAdapter extends RecyclerView.Adapter<RecyclerMeterListAdapter.MeterItemViewHolder> {

    private final ArrayList<Meter> arrayList;

    public RecyclerMeterListAdapter(ArrayList<Meter> arrayList) {
        this.arrayList = arrayList;
    }

    public MeterCallBacks meterCallBacks;

    public void setMeterCallBack(MeterCallBacks meterCallBacks) {
        this.meterCallBacks = meterCallBacks;
    }

    @NonNull
    @Override
    public MeterItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLayoutMeterListBinding binding = AdapterLayoutMeterListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeterItemViewHolder(binding, meterCallBacks);
    }

    @Override
    public void onBindViewHolder(@NonNull MeterItemViewHolder holder, int position) {
        Meter meter = arrayList.get(position);
        holder.bind(meter);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MeterItemViewHolder extends RecyclerView.ViewHolder {
        AdapterLayoutMeterListBinding binding;
        MeterCallBacks meterCallBacks;
        public MeterItemViewHolder(@NonNull AdapterLayoutMeterListBinding binding, MeterCallBacks meterCallBacks) {
            super(binding.getRoot());
            this.binding = binding;
            this.meterCallBacks = meterCallBacks;
        }

        void bind(Meter meter){
            binding.meterName.setText(meter.getMeterName());
            binding.roomName.setText(meter.getAssociateRoom());
            binding.meterType.setText(meter.getMeterTypeName());
            binding.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (meterCallBacks != null){
                        meterCallBacks.onItemClick(meter);
                    }
                }
            });

            binding.editDeleteIncludeLayout.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (meterCallBacks != null) meterCallBacks.onEdit(meter);
                }
            });
            binding.editDeleteIncludeLayout.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (meterCallBacks != null) meterCallBacks.onDelete(meter);
                }
            });
        }
    }

    public interface MeterCallBacks {
        void onDelete(Meter meter);

        void onEdit(Meter meter);

        void onItemClick(Meter meter);
    }
}
