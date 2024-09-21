package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.data.model.generatebill.BillInfo
import com.shakil.barivara.databinding.AdapterLayoutBilListBinding

class RecyclerBillInfoAdapter :
    RecyclerView.Adapter<RecyclerBillInfoAdapter.BillItemViewHolder>() {
    private var generateBillCallBacks: GenerateBillCallBacks? = null
    private var list: List<BillInfo> = mutableListOf()

    fun setGenerateBillCallBacks(generateBillCallBacks: GenerateBillCallBacks?) {
        this.generateBillCallBacks = generateBillCallBacks
    }

    fun setItems(list: List<BillInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillItemViewHolder {
        val binding =
            AdapterLayoutBilListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillItemViewHolder(binding, generateBillCallBacks)
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
        private var generateBillCallBacks: GenerateBillCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(billInfo: BillInfo) {
            binding.RoomId.text = billInfo.room
            binding.TenantId.text = billInfo.tenant
            binding.TotalBill.text = billInfo.rent.toString()

            binding.markAsPaid.setOnClickListener {
                generateBillCallBacks?.onNotify(billInfo)
            }

            binding.notifyUser.setOnClickListener {
                generateBillCallBacks?.onMarkAsPaid(billInfo)
            }
        }
    }

    interface GenerateBillCallBacks {
        fun onNotify(billInfo: BillInfo)
        fun onMarkAsPaid(billInfo: BillInfo)
    }
}
