package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.drawer.DrawerItem;

import java.util.ArrayList;

public class RecyclerNavDrawerAdapter extends RecyclerView.Adapter<RecyclerNavDrawerAdapter.ViewHolder> {

    private final ArrayList<DrawerItem> arrayList;

    public RecyclerNavDrawerAdapter(ArrayList<DrawerItem> arrayList) {
        this.arrayList = arrayList;
    }

    //region click adapter
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(DrawerItem drawerItem);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DrawerItem drawerItem = arrayList.get(position);
        holder.icon.setImageResource(drawerItem.getIcon());
        holder.title.setText(drawerItem.getTitle());
        holder.item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(drawerItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        CardView item_card_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            item_card_view = itemView.findViewById(R.id.item_card_view);
        }
    }
}
