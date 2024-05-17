package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.notification.Notification

class NotificationRecyclerAdapter(private val notifications: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_recycler_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.DateTime.text = notification.dateTime
        holder.Title.text = notification.title
        holder.Message.text = notification.message
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_card_view: RelativeLayout
        var DateTime: TextView
        var Title: TextView
        var Message: TextView
        var SeenStatus: ImageView

        init {
            item_card_view = itemView.findViewById(R.id.item_card_view)
            DateTime = itemView.findViewById(R.id.DateTime)
            Title = itemView.findViewById(R.id.Title)
            Message = itemView.findViewById(R.id.Message)
            SeenStatus = itemView.findViewById(R.id.SeenStatus)
        }
    }
}
