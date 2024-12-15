package com.shakil.barivara.data.model.dashboard

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class DashboardByYearAndMonthBaseResponse(
    @SerializedName("data") var data: DashboardData? = DashboardData()
) : BaseApiResponse()
