package com.shakil.barivara.data.model.generatebill

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BillHistoryBaseResponse(
    @SerializedName("data") var data: List<BillHistory>? = null
) : BaseApiResponse()
