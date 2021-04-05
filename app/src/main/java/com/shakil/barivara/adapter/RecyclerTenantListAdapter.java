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
import com.shakil.barivara.model.tenant.Tenant;

import java.util.ArrayList;

public class RecyclerTenantListAdapter extends RecyclerView.Adapter<RecyclerTenantListAdapter.ViewHolder> {

    private ArrayList<Tenant> arrayList;
    private Context context;

    public ArrayList<Tenant> getTenantList() {
        return arrayList;
    }

    public void setTenantList(ArrayList<Tenant> arrayList) {
        this.arrayList = arrayList;
    }

    public RecyclerTenantListAdapter(ArrayList<Tenant> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    //region click adapter
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(Tenant tenant);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_layout_tenant_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tenant tenant = arrayList.get(position);
        holder.TenantName.setText(tenant.getTenantName());
        holder.StartingMonth.setText(tenant.getStartingMonth());
        holder.AssociateRoom.setText(tenant.getAssociateRoom());
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(tenant);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView TenantName, StartingMonth, AssociateRoom;
        CardView item_card_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TenantName = itemView.findViewById(R.id.TenantName);
            StartingMonth = itemView.findViewById(R.id.StartingMonth);
            AssociateRoom = itemView.findViewById(R.id.AssociateRoom);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }
}
