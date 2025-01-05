package com.shakil.barivara.data.model.dashboard

import com.google.gson.annotations.SerializedName

data class DashboardData(
    @SerializedName("total_paid") var totalPaid: Int? = null,
    @SerializedName("total_due") var totalDue: Int? = null,
    @SerializedName("status") var status: Boolean? = null
)
