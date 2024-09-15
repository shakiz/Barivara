package com.shakil.barivara.data.model.tenant

import com.google.gson.annotations.SerializedName

data class NewTenant(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("nid_number") var nidNumber: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("start_date") var startDate: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("advanced_amount") var advancedAmount: Int? = null
)
