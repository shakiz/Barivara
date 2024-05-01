package com.shakil.barivara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.adapter.RecyclerElectricityBillListAdapter.ElectricityBillViewHolder
import com.shakil.barivara.databinding.AdapterLayoutElectricityBillListBinding
import com.shakil.barivara.model.meter.ElectricityBill

class RecyclerElectricityBillListAdapter(private val arrayList: ArrayList<ElectricityBill>) :
    RecyclerView.Adapter<ElectricityBillViewHolder>() {
    private var electricityBillBacks: ElectricityBillBacks? = null
    fun setElectricityBillBack(electricityBillBacks: ElectricityBillBacks?) {
        this.electricityBillBacks = electricityBillBacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectricityBillViewHolder {
        val binding = AdapterLayoutElectricityBillListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ElectricityBillViewHolder(binding, electricityBillBacks)
    }

    override fun onBindViewHolder(holder: ElectricityBillViewHolder, position: Int) {
        val electricityBill = arrayList[position]
        holder.bind(electricityBill)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ElectricityBillViewHolder(
        var binding: AdapterLayoutElectricityBillListBinding,
        private var electricityBillBacks: ElectricityBillBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(electricityBill: ElectricityBill) {
            binding.MeterId.text = electricityBill.meterName
            binding.RoomId.text = electricityBill.roomName
            binding.TotalBill.text =
                (electricityBill.totalUnit * electricityBill.unitPrice).toString()
            binding.itemCardView.setOnClickListener {
                electricityBillBacks?.onItemClick(electricityBill)
            }
            binding.DeleteFromCart.setOnClickListener {
                electricityBillBacks?.onDelete(
                    electricityBill
                )
            }
        }
    }

    interface ElectricityBillBacks {
        fun onDelete(electricityBill: ElectricityBill?)
        fun onItemClick(electricityBill: ElectricityBill?)
    }
}
