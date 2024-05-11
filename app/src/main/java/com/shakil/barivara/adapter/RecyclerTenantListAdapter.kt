package com.shakil.barivara.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.adapter.RecyclerTenantListAdapter.TenantItemViewHolder
import com.shakil.barivara.databinding.AdapterLayoutTenantListBinding
import com.shakil.barivara.data.model.tenant.Tenant

class RecyclerTenantListAdapter(private val arrayList: ArrayList<Tenant>) :
    RecyclerView.Adapter<TenantItemViewHolder>() {
    private var tenantCallback: TenantCallBacks? = null

    fun setOnTenantCallback(tenantCallback: TenantCallBacks?) {
        this.tenantCallback = tenantCallback
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
        val tenant = arrayList[position]
        holder.bind(tenant)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class TenantItemViewHolder(
        var binding: AdapterLayoutTenantListBinding,
        private var tenantCallBack: TenantCallBacks?
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(tenant: Tenant) {
            binding.TenantName.text = tenant.tenantName
            binding.StartingMonth.text = tenant.startingMonth
            binding.AssociateRoom.text = tenant.associateRoom
            binding.TenantTypeStr.text = tenant.tenantTypeStr
            binding.itemCardView.setOnClickListener {
                tenantCallBack?.onItemClick(tenant)
            }
            binding.callIcon.setOnClickListener {
                tenantCallBack?.onCallClicked(tenant.mobileNo ?: "", tenant.tenantName ?: "")
            }
            binding.messageIcon.setOnClickListener {
                tenantCallBack?.onMessageClicked(tenant.mobileNo ?: "")
            }
            if (tenant.isActiveValue != null && !TextUtils.isEmpty(tenant.isActiveValue)) {
                if (tenant.isActiveValue == binding.root.context.getString(R.string.yes)) {
                    binding.activeColorIndicator.setBackgroundColor(
                        binding.root.context.resources.getColor(
                            R.color.md_green_400
                        )
                    )
                } else {
                    binding.activeColorIndicator.setBackgroundColor(
                        binding.root.context.resources.getColor(
                            R.color.md_red_400
                        )
                    )
                }
            }
            binding.editIcon.setOnClickListener {
                tenantCallBack?.onEdit(tenant)
            }
            binding.deleteIcon.setOnClickListener {
                tenantCallBack?.onDelete(tenant)
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
