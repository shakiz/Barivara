package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.data.model.generatebill.BillInfo
import com.shakil.barivara.data.model.room.NewRoom
import com.shakil.barivara.databinding.AdapterLayoutBilListBinding

class RecyclerBillInfoAdapter :
    RecyclerView.Adapter<RecyclerBillInfoAdapter.BillItemViewHolder>() {
    private var roomCallBacks: RoomCallBacks? = null
    private var list: List<BillInfo> = mutableListOf()

    fun setRoomCallBack(roomCallBacks: RoomCallBacks?) {
        this.roomCallBacks = roomCallBacks
    }

    fun setItems(list: List<BillInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillItemViewHolder {
        val binding =
            AdapterLayoutBilListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillItemViewHolder(binding, roomCallBacks)
    }

    override fun onBindViewHolder(holder: BillItemViewHolder, position: Int) {
        val room = list[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class BillItemViewHolder(
        var binding: AdapterLayoutBilListBinding,
        private var roomCallBacks: RoomCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(billInfo: BillInfo) {
            binding.RoomId.text = billInfo.room
            binding.TenantId.text = billInfo.tenant
            binding.TotalBill.text = billInfo.rent.toString()
        }
    }

    interface RoomCallBacks {
        fun onDelete(room: NewRoom)
        fun onEdit(room: NewRoom)
        fun onItemClick(room: NewRoom)
    }
}
