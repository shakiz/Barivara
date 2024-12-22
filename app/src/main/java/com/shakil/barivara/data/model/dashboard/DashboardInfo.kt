package com.shakil.barivara.data.model.dashboard

import com.google.gson.annotations.SerializedName

data class DashboardInfo(
    @SerializedName("total_active_room") var totalActiveRoom: Int? = null,
    @SerializedName("total_active_tenant") var totalActiveTenant: Int? = null,
    @SerializedName("total_collected_rent") var totalCollectedRent: Int? = null
)
