package com.shakil.barivara.data.model.tenant

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BaseTenantResponse(
    @SerializedName("data") var data: ArrayList<Tenant> = arrayListOf()
) : BaseApiResponse()
