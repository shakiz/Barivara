package com.shakil.barivara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.adapter.RecyclerMeterListAdapter.MeterItemViewHolder
import com.shakil.barivara.databinding.AdapterLayoutMeterListBinding
import com.shakil.barivara.data.model.meter.Meter

class RecyclerMeterListAdapter(private val arrayList: ArrayList<Meter>) :
    RecyclerView.Adapter<MeterItemViewHolder>() {
    private var meterCallBacks: MeterCallBacks? = null
    fun setMeterCallBack(meterCallBacks: MeterCallBacks?) {
        this.meterCallBacks = meterCallBacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeterItemViewHolder {
        val binding = AdapterLayoutMeterListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MeterItemViewHolder(binding, meterCallBacks)
    }

    override fun onBindViewHolder(holder: MeterItemViewHolder, position: Int) {
        val meter = arrayList[position]
        holder.bind(meter)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class MeterItemViewHolder(
        var binding: AdapterLayoutMeterListBinding,
        private var meterCallBacks: MeterCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(meter: Meter) {
            binding.meterName.text = meter.meterName
            binding.roomName.text = meter.associateRoom
            binding.meterType.text = meter.meterTypeName
            binding.itemCardView.setOnClickListener {
                meterCallBacks?.onItemClick(meter)
            }
            binding.editDeleteIncludeLayout.editButton.setOnClickListener {
                meterCallBacks?.onEdit(meter)
            }
            binding.editDeleteIncludeLayout.deleteButton.setOnClickListener {
                meterCallBacks?.onDelete(meter)
            }
        }
    }

    interface MeterCallBacks {
        fun onDelete(meter: Meter)
        fun onEdit(meter: Meter)
        fun onItemClick(meter: Meter)
    }
}
