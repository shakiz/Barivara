package com.shakil.barivara.data.model.dashboard

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BaseDashboardResponse(
    @SerializedName("data") var data: DashboardInfo? = null
) : BaseApiResponse()
