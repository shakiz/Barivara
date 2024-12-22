package com.shakil.barivara.data.model.generatebill

import com.google.gson.annotations.SerializedName

data class GenerateBillResponse(
    @SerializedName("total_paid") var totalPaid: Int? = null,
    @SerializedName("total_due") var totalDue: Int? = null,
    @SerializedName("bill_info") var billInfo: ArrayList<BillInfo> = arrayListOf()
)