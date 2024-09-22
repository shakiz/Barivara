package com.shakil.barivara.data.model.generatebill

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BaseGenerateBillResponse(
    @SerializedName("data") var data: GenerateBillResponse? = null
) : BaseApiResponse()
