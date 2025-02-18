package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.databinding.AdapterLayoutBillHistoryBinding
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.boldAfterColon
import com.shakil.barivara.utils.formatDateIntoAppDate
import com.shakil.barivara.utils.orZero

class RecyclerBillHistoryAdapter(private val utilsForAll: UtilsForAll) :
    RecyclerView.Adapter<RecyclerBillHistoryAdapter.BillItemViewHolder>() {
    private var generateDBillHistoryCallBacks: GenerateDBillHistoryCallBacks? = null
    private var list: List<BillHistory> = mutableListOf()

    fun setItems(list: List<BillHistory>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setGenerateBillCallBacks(generateDBillHistoryCallBacks: GenerateDBillHistoryCallBacks?) {
        this.generateDBillHistoryCallBacks = generateDBillHistoryCallBacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillItemViewHolder {
        val binding =
            AdapterLayoutBillHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BillItemViewHolder(binding, generateDBillHistoryCallBacks)
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
        private var generateDBillHistoryCallBacks: GenerateDBillHistoryCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(billInfo: BillHistory, utilsForAll: UtilsForAll) {
            val context = binding.root.context
            binding.roomName.text = billInfo.room
            binding.tenantName.text = context.getString(R.string.tenant_x, billInfo.tenantName)
            binding.totalBill.text = context.getString(R.string.collected_x, billInfo.rent)
            binding.monthAndYear.text = context.getString(
                R.string.x_comma_x,
                utilsForAll.getMonthNameFromNumber(billInfo.month.orZero()),
                billInfo.year
            )
            binding.billStatus.text = context.getString(
                R.string.status_x,
                billInfo.status.toString().replaceFirstChar { it.uppercase() })

            if (billInfo.status.orEmpty() == "paid") {
                binding.paymentReceivedDate.visibility = View.VISIBLE
                val formattedDate = billInfo.paymentReceived?.formatDateIntoAppDate()
                binding.paymentReceivedDate.text = context.getString(
                    R.string.payment_received_on_x,
                    formattedDate
                ).boldAfterColon()

                binding.billStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_green_800
                    )
                )
                binding.actionButtonLayout.visibility = View.GONE
            } else {
                binding.billStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorRed
                    )
                )
                binding.paymentReceivedDate.visibility = View.GONE
                binding.actionButtonLayout.visibility = View.VISIBLE
            }

            binding.markAsPaid.setOnClickListener {
                generateDBillHistoryCallBacks?.onMarkAsPaid(billInfo)
            }

            binding.notifyUser.setOnClickListener {
                generateDBillHistoryCallBacks?.onNotify(billInfo)
            }
        }
    }

    interface GenerateDBillHistoryCallBacks {
        fun onNotify(billHistory: BillHistory)
        fun onMarkAsPaid(billHistory: BillHistory)
    }
}
