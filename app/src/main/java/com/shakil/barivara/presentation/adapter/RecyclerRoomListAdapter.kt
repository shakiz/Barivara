package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.data.model.room.NewRoom
import com.shakil.barivara.databinding.AdapterLayoutRoomListBinding
import com.shakil.barivara.presentation.adapter.RecyclerRoomListAdapter.RoomItemViewHolder

class RecyclerRoomListAdapter :
    RecyclerView.Adapter<RoomItemViewHolder>() {
    private var roomCallBacks: RoomCallBacks? = null
    private var list: List<NewRoom> = mutableListOf()

    fun setRoomCallBack(roomCallBacks: RoomCallBacks?) {
        this.roomCallBacks = roomCallBacks
    }

    fun setItems(list: List<NewRoom>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomItemViewHolder {
        val binding =
            AdapterLayoutRoomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomItemViewHolder(binding, roomCallBacks)
    }

    override fun onBindViewHolder(holder: RoomItemViewHolder, position: Int) {
        val room = list[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class RoomItemViewHolder(
        var binding: AdapterLayoutRoomListBinding,
        private var roomCallBacks: RoomCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(room: NewRoom) {
            binding.roomName.text = room.name
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
        fun onDelete(room: NewRoom)
        fun onEdit(room: NewRoom)
        fun onItemClick(room: NewRoom)
    }
}
