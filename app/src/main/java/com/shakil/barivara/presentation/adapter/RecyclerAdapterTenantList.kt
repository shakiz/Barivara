package com.shakil.barivara.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.databinding.AdapterLayoutTenantListBinding
import com.shakil.barivara.presentation.adapter.RecyclerAdapterTenantList.TenantItemViewHolder
import com.shakil.barivara.utils.boldAfterColon
import com.shakil.barivara.utils.formatDateTimeToAppText

class RecyclerAdapterTenantList :
    RecyclerView.Adapter<TenantItemViewHolder>() {
    private var tenantCallback: TenantCallBacks? = null
    private var list: List<Tenant> = mutableListOf()

    fun setOnTenantCallback(tenantCallback: TenantCallBacks?) {
        this.tenantCallback = tenantCallback
    }

    fun setItems(list: List<Tenant>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantItemViewHolder {
        val binding = AdapterLayoutTenantListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TenantItemViewHolder(binding, tenantCallback)
    }

    override fun onBindViewHolder(holder: TenantItemViewHolder, position: Int) {
        val tenant = list[position]
        holder.bind(tenant)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class TenantItemViewHolder(
        var binding: AdapterLayoutTenantListBinding,
        private var tenantCallBack: TenantCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(tenant: Tenant) {
            val context: Context = binding.root.context
            binding.TenantName.text =
                context.getString(R.string.tenant_name_x, tenant.name).boldAfterColon()
            binding.MobileNo.text =
                context.getString(R.string.phone_no_x, tenant.phone?.toInt()).boldAfterColon()
            binding.StartDate.text =
                context.getString(R.string.start_date_x, formatDateTimeToAppText(tenant.startDate))
                    .boldAfterColon()

            binding.itemCardView.setOnClickListener {
                tenantCallBack?.onItemClick(tenant)
            }
            binding.callIcon.setOnClickListener {
                tenantCallBack?.onCallClicked(tenant.phone ?: "", tenant.name ?: "")
            }
            binding.messageIcon.setOnClickListener {
                tenantCallBack?.onMessageClicked(tenant.phone ?: "")
            }
            binding.editIcon.setOnClickListener {
                tenantCallBack?.onEdit(tenant)
            }
        }
    }

    interface TenantCallBacks {
        fun onCallClicked(mobileNo: String, tenantName: String)
        fun onMessageClicked(mobileNo: String)
        fun onDelete(tenant: Tenant)
        fun onEdit(tenant: Tenant)
        fun onItemClick(tenant: Tenant)
    }
}
