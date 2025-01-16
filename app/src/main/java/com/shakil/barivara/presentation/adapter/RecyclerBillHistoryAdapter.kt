package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.databinding.AdapterLayoutBillHistoryBinding
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.orZero

class RecyclerBillHistoryAdapter(private val utilsForAll: UtilsForAll) :
    RecyclerView.Adapter<RecyclerBillHistoryAdapter.BillItemViewHolder>() {
    private var list: List<BillHistory> = mutableListOf()

    fun setItems(list: List<BillHistory>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillItemViewHolder {
        val binding =
            AdapterLayoutBillHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BillItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillItemViewHolder, position: Int) {
        val room = list[position]
        holder.bind(room, utilsForAll)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class BillItemViewHolder(
        var binding: AdapterLayoutBillHistoryBinding,
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(billInfo: BillHistory, utilsForAll: UtilsForAll) {
            binding.RoomId.text = billInfo.room
            binding.TenantId.text = billInfo.tenantName
            binding.TotalBill.text = billInfo.rent.toString()
            binding.monthAndYear.text = binding.root.context.getString(
                R.string.x_comma_x,
                utilsForAll.getMonthNameFromNumber(billInfo.month.orZero()),
                billInfo.year
            )
            binding.billStatus.text = billInfo.status.toString().replaceFirstChar { it.uppercase() }
        }
    }
}
