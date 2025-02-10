package com.shakil.barivara.data.model.dashboard

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BaseYearlyRentResponse(
    @SerializedName("data") var data: ArrayList<MonthlyRentInfo> = arrayListOf()
) : BaseApiResponse()
