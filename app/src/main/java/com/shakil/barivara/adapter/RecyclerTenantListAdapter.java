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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
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
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_layout_tenant_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tenant tenant = arrayList.get(position);
        holder.TenantName.setText(tenant.getTenantName());
        holder.StartingMonth.setText(tenant.getStartingMonth());
        holder.AssociateRoom.setText(tenant.getAssociateRoom());
        holder.TenantTypeStr.setText(tenant.getTenantTypeStr());
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(tenant);
                }
            }
        });

        holder.callIcon.setOnClickListener(new View.OnClickListener() {
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

        holder.messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, context.getString(R.string.taking_into_message_section), Toast.LENGTH_SHORT).show();
                new Tools(context).sendMessage(tenant.getMobileNo());
            }
        });

        if (tenant.getIsActiveValue() != null && !TextUtils.isEmpty(tenant.getIsActiveValue())) {
            if (tenant.getIsActiveValue().equals(context.getString(R.string.yes))){
                holder.activeColorIndicator.setBackgroundColor(context.getResources().getColor(R.color.md_green_400));
            }
            else{
                holder.activeColorIndicator.setBackgroundColor(context.getResources().getColor(R.color.md_red_400));
            }
        }

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditListener != null) onEditListener.onEdit(tenant);
            }
        });
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) onDeleteListener.onDelete(tenant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView TenantName, StartingMonth, AssociateRoom, TenantTypeStr;
        CardView item_card_view;
        ImageView callIcon, messageIcon, editIcon, deleteIcon;
        RelativeLayout activeColorIndicator;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TenantName = itemView.findViewById(R.id.TenantName);
            StartingMonth = itemView.findViewById(R.id.StartingMonth);
            AssociateRoom = itemView.findViewById(R.id.AssociateRoom);
            TenantTypeStr = itemView.findViewById(R.id.TenantTypeStr);
            activeColorIndicator = itemView.findViewById(R.id.activeColorIndicator);
            callIcon = itemView.findViewById(R.id.callIcon);
            messageIcon = itemView.findViewById(R.id.messageIcon);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }

    public interface onDeleteListener{ void onDelete(Tenant tenant);}
    public interface onEditListener{ void onEdit(Tenant tenant);}
    public interface onItemClickListener{ void onItemClick(Tenant tenant);}
}
