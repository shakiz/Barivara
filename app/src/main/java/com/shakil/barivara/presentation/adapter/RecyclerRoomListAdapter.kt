package com.shakil.barivara.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.databinding.AdapterLayoutRoomListBinding
import com.shakil.barivara.presentation.adapter.RecyclerRoomListAdapter.RoomItemViewHolder
import com.shakil.barivara.utils.boldAfterColon

class RecyclerRoomListAdapter :
    RecyclerView.Adapter<RoomItemViewHolder>() {
    private var roomCallBacks: RoomCallBacks? = null
    private var list: List<Room> = mutableListOf()

    fun setRoomCallBack(roomCallBacks: RoomCallBacks?) {
        this.roomCallBacks = roomCallBacks
    }

    fun setItems(list: List<Room>) {
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
        fun bind(room: Room) {
            val context: Context = binding.root.context
            binding.roomName.text =
                context.getString(R.string.room_name_x, room.name).boldAfterColon()
            val roomStatus = room.status?.split(" ")?.mapIndexed { index, word ->
                if (index == 0) word.replaceFirstChar { it.uppercase() } else word
            }?.joinToString(" ")
            binding.roomStatus.text =
                context.getString(R.string.room_status_x, roomStatus).boldAfterColon()
            binding.roomRent.text =
                context.getString(R.string.rent_amount_x, room.rent).boldAfterColon()

            binding.itemCardView.setOnClickListener {
                roomCallBacks?.onItemClick(room)
            }
            binding.editButton.setOnClickListener {
                roomCallBacks?.onEdit(room)
            }
            binding.deleteButton.setOnClickListener {
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
