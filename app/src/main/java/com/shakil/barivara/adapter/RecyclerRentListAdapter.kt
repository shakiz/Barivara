package com.shakil.barivara.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.adapter.RecyclerRentListAdapter.RentItemViewHolder
import com.shakil.barivara.databinding.AdapterLayoutRentListBinding
import com.shakil.barivara.model.room.Rent

class RecyclerRentListAdapter(private val arrayList: ArrayList<Rent>) :
    RecyclerView.Adapter<RentItemViewHolder>() {
    private var rentCallBacks: RentCallBacks? = null
    fun setRentCallBack(rentCallBacks: RentCallBacks?) {
        this.rentCallBacks = rentCallBacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentItemViewHolder {
        val binding =
            AdapterLayoutRentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RentItemViewHolder(binding, rentCallBacks)
    }

    override fun onBindViewHolder(holder: RentItemViewHolder, position: Int) {
        val rent = arrayList[position]
        holder.bind(rent)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class RentItemViewHolder(
        var binding: AdapterLayoutRentListBinding,
        var rentCallBacks: RentCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(rent: Rent) {
            binding.MonthName.text = rent.monthName
            binding.AssociateRoomName.text = rent.associateRoomName
            binding.RentAmount.text = rent.rentAmount.toString()
            binding.itemCardView.setOnClickListener {
                rentCallBacks?.onItemClick(rent)
            }
            binding.editDeleteIncludeLayout.editButton.setOnClickListener {
                rentCallBacks?.onEdit(rent)
            }
            binding.editDeleteIncludeLayout.deleteButton.setOnClickListener {
                rentCallBacks?.onDelete(rent)
            }
        }
    }

    interface RentCallBacks {
        fun onDelete(rent: Rent)
        fun onEdit(rent: Rent)
        fun onItemClick(rent: Rent)
    }
}
