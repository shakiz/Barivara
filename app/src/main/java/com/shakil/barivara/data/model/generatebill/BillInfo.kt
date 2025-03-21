package com.shakil.barivara.data.model.generatebill

import com.google.gson.annotations.SerializedName


data class BillInfo(

    @SerializedName("id") var id: Int = 0,
    @SerializedName("room") var room: String? = null,
    @SerializedName("tenant") var tenant: String? = null,
    @SerializedName("tenant_phone") var tenantPhone: String? = null,
    @SerializedName("rent") var rent: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("is_notified") var isNotified: Int? = null,
    @SerializedName("month") var month: Int? = null,
    @SerializedName("year") var year: Int? = null,
    @SerializedName("remarks") var remarks: String? = null

)