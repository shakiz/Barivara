package com.shakil.barivara.data.model.dashboard

import com.google.gson.annotations.SerializedName

data class MonthlyRentInfo(
    @SerializedName("month") var month: Int? = null,
    @SerializedName("total_paid") var totalPaid: Int? = null,
    @SerializedName("total_due") var totalDue: Int? = null
)
