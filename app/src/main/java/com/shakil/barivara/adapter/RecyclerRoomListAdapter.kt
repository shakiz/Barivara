package com.shakil.barivara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.adapter.RecyclerRoomListAdapter.RoomItemViewHolder
import com.shakil.barivara.databinding.AdapterLayoutRoomListBinding
import com.shakil.barivara.model.room.Room

class RecyclerRoomListAdapter(private val arrayList: ArrayList<Room>) :
    RecyclerView.Adapter<RoomItemViewHolder>() {
    private var roomCallBacks: RoomCallBacks? = null
    fun setRoomCallBack(roomCallBacks: RoomCallBacks?) {
        this.roomCallBacks = roomCallBacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomItemViewHolder {
        val binding =
            AdapterLayoutRoomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomItemViewHolder(binding, roomCallBacks)
    }

    override fun onBindViewHolder(holder: RoomItemViewHolder, position: Int) {
        val room = arrayList[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class RoomItemViewHolder(
        var binding: AdapterLayoutRoomListBinding,
        private var roomCallBacks: RoomCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(room: Room) {
            binding.roomName.text = room.roomName
            binding.roomOwner.text = room.tenantName
            binding.startMonth.text = room.startMonthName
            binding.itemCardView.setOnClickListener {
                roomCallBacks?.onItemClick(room)
            }
            binding.editDeleteIncludeLayout.editButton.setOnClickListener {
                roomCallBacks?.onEdit(room)
            }
            binding.editDeleteIncludeLayout.deleteButton.setOnClickListener {
                roomCallBacks?.onDelete(room)
            }
        }
    }

    interface RoomCallBacks {
        fun onDelete(room: Room)
        fun onEdit(room: Room)
        fun onItemClick(room: Room)
    }
}
