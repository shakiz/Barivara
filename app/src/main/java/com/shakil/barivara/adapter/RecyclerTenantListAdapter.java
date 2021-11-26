package com.shakil.barivara.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.room.Room;
import com.shakil.barivara.model.tenant.Tenant;
import com.shakil.barivara.utils.Tools;

import java.util.ArrayList;

public class RecyclerTenantListAdapter extends RecyclerView.Adapter<RecyclerTenantListAdapter.ViewHolder> {

    private final ArrayList<Tenant> arrayList;
    private final Context context;

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
    public onEditListener onEditListener;
    public interface onEditListener{
        void onEdit(Tenant tenant);
    }

    public void onEditListener(onEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }
    public onDeleteListener onDeleteListener;
    public interface onDeleteListener{
        void onDelete(Tenant tenant);
    }

    public void onDeleteListener(onDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
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
        //region call and message listeners
        holder.callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    String uri = "tel:"+tenant.getMobileNo();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(uri));
                    Toast.makeText(context, context.getString(R.string.calling)+" "+tenant.getTenantName()+"....", Toast.LENGTH_SHORT).show();
                    context.startActivity(callIntent);
                } else {
                    Toast.makeText(context, context.getString(R.string.please_allow_call_permission), Toast.LENGTH_SHORT).show();
                    new Tools(context).askCallPermission((Activity) context);
                }
            }
        });

        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, context.getString(R.string.taking_into_message_section), Toast.LENGTH_SHORT).show();
                new Tools(context).sendMessage(tenant.getMobileNo());
            }
        });
        //endregion

        //region set tenant status
        if (tenant.getIsActiveValue() != null && !TextUtils.isEmpty(tenant.getIsActiveValue())) {
            if (tenant.getIsActiveValue().equals(context.getString(R.string.yes))){
                holder.IsActive.setText(context.getString(R.string.running));
                holder.IsActive.setBackgroundResource(R.drawable.rectangle_green_on_side_curve);
            }
            else{
                holder.IsActive.setText(context.getString(R.string.left));
                holder.IsActive.setBackgroundResource(R.drawable.rectangle_light_red_on_sided_curve);
            }
        }
        //endregion

        //region edit and delete click listeners
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditListener != null) onEditListener.onEdit(tenant);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) onDeleteListener.onDelete(tenant);
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView TenantName, StartingMonth, AssociateRoom, IsActive;
        CardView item_card_view;
        LinearLayout callLayout, messageLayout;
        Button editButton, deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TenantName = itemView.findViewById(R.id.TenantName);
            StartingMonth = itemView.findViewById(R.id.StartingMonth);
            AssociateRoom = itemView.findViewById(R.id.AssociateRoom);
            callLayout = itemView.findViewById(R.id.callLayout);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            IsActive = itemView.findViewById(R.id.IsActive);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }
}
