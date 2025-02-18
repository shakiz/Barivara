package com.shakil.barivara.data.model.generatebill

import com.google.gson.annotations.SerializedName

data class BillHistory(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("room") var room: String? = null,
    @SerializedName("tenant_id") var tenantId: Int? = null,
    @SerializedName("tenant_name") var tenantName: String? = null,
    @SerializedName("rent") var rent: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("is_notified") var isNotified: Int? = null,
    @SerializedName("tenant_phone") var tenantPhone: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("month") var month: Int? = null,
    @SerializedName("year") var year: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("payment_received") var paymentReceived: String? = null
)