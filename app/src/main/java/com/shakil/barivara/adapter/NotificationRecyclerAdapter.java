package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.notification.Notification;

import java.util.ArrayList;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder> {
    private ArrayList<Notification> notifications;

    public NotificationRecyclerAdapter(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.DateTime.setText(notification.getDateTime());
        holder.Title.setText(notification.getTitle());
        holder.Message.setText(notification.getMessage());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout item_card_view;
        TextView DateTime, Title, Message;
        ImageView SeenStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_card_view = itemView.findViewById(R.id.item_card_view);
            DateTime = itemView.findViewById(R.id.DateTime);
            Title = itemView.findViewById(R.id.Title);
            Message = itemView.findViewById(R.id.Message);
            SeenStatus = itemView.findViewById(R.id.SeenStatus);
        }
    }
}
