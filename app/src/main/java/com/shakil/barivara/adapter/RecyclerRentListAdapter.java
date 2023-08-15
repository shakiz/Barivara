package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.databinding.AdapterLayoutRentListBinding;
import com.shakil.barivara.model.room.Rent;

import java.util.ArrayList;

public class RecyclerRentListAdapter extends RecyclerView.Adapter<RecyclerRentListAdapter.RentItemViewHolder> {

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
    public RentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLayoutRentListBinding binding = AdapterLayoutRentListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RentItemViewHolder(binding, rentCallBacks);
    }

    @Override
    public void onBindViewHolder(@NonNull RentItemViewHolder holder, int position) {
        Rent rent = arrayList.get(position);
        holder.bind(rent);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RentItemViewHolder extends RecyclerView.ViewHolder {
        AdapterLayoutRentListBinding binding;
        RentCallBacks rentCallBacks;
        public RentItemViewHolder(@NonNull AdapterLayoutRentListBinding binding, RentCallBacks rentCallBacks) {
            super(binding.getRoot());
            this.binding = binding;
            this.rentCallBacks = rentCallBacks;
        }

        void bind(Rent rent){
            binding.MonthName.setText(rent.getMonthName());
            binding.AssociateRoomName.setText(rent.getAssociateRoomName());
            binding.RentAmount.setText(String.valueOf(rent.getRentAmount()));
            binding.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rentCallBacks != null){
                        rentCallBacks.onItemClick(rent);
                    }
                }
            });
            binding.editDeleteIncludeLayout.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rentCallBacks != null) rentCallBacks.onEdit(rent);
                }
            });
            binding.editDeleteIncludeLayout.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rentCallBacks != null) rentCallBacks.onDelete(rent);
                }
            });
        }
    }

    public interface RentCallBacks {
        void onDelete(Rent rent);
        void onEdit(Rent rent);
        void onItemClick(Rent rent);
    }
}
