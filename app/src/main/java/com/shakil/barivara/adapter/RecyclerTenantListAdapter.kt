package com.shakil.barivara.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.AdapterLayoutTenantListBinding;
import com.shakil.barivara.model.tenant.Tenant;

import java.util.ArrayList;

public class RecyclerTenantListAdapter extends RecyclerView.Adapter<RecyclerTenantListAdapter.TenantItemViewHolder> {
    private final ArrayList<Tenant> arrayList;

    public RecyclerTenantListAdapter(ArrayList<Tenant> arrayList) {
        this.arrayList = arrayList;
    }

    public TenantCallBacks tenantCallback;

    public void setOnTenantCallback(TenantCallBacks tenantCallback) {
        this.tenantCallback = tenantCallback;
    }

    @NonNull
    @Override
    public TenantItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLayoutTenantListBinding binding = AdapterLayoutTenantListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TenantItemViewHolder(binding, tenantCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull TenantItemViewHolder holder, int position) {
        Tenant tenant = arrayList.get(position);
        holder.bind(tenant);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class TenantItemViewHolder extends RecyclerView.ViewHolder {
        AdapterLayoutTenantListBinding binding;
        TenantCallBacks tenantCallBack;

        public TenantItemViewHolder(@NonNull AdapterLayoutTenantListBinding binding, TenantCallBacks tenantCallBacks) {
            super(binding.getRoot());
            this.binding = binding;
            this.tenantCallBack = tenantCallBacks;
        }

        void bind(Tenant tenant){
            binding.TenantName.setText(tenant.getTenantName());
            binding.StartingMonth.setText(tenant.getStartingMonth());
            binding.AssociateRoom.setText(tenant.getAssociateRoom());
            binding.TenantTypeStr.setText(tenant.getTenantTypeStr());
            binding.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tenantCallBack != null) {
                        tenantCallBack.onItemClick(tenant);
                    }
                }
            });

            binding.callIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tenantCallBack != null) {
                        tenantCallBack.onCallClicked(tenant.getMobileNo(), tenant.getTenantName());
                    }
                }
            });

            binding.messageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tenantCallBack != null) {
                        tenantCallBack.onMessageClicked(tenant.getMobileNo());
                    }
                }
            });

            if (tenant.getIsActiveValue() != null && !TextUtils.isEmpty(tenant.getIsActiveValue())) {
                if (tenant.getIsActiveValue().equals(binding.getRoot().getContext().getString(R.string.yes))) {
                    binding.activeColorIndicator.setBackgroundColor(binding.getRoot().getContext().getResources().getColor(R.color.md_green_400));
                } else {
                    binding.activeColorIndicator.setBackgroundColor(binding.getRoot().getContext().getResources().getColor(R.color.md_red_400));
                }
            }

            binding.editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tenantCallBack != null) tenantCallBack.onEdit(tenant);
                }
            });
            binding.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tenantCallBack != null) tenantCallBack.onDelete(tenant);
                }
            });
        }
    }

    public interface TenantCallBacks {
        void onCallClicked(String mobileNo, String tenantName);
        void onMessageClicked(String mobileNo);
        void onDelete(Tenant tenant);
        void onEdit(Tenant tenant);
        void onItemClick(Tenant tenant);
    }
}
