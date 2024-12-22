package com.shakil.barivara.data.model.user

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BaseUserResponse(
    @SerializedName("data") var data: UserInfo? = null
) : BaseApiResponse()
