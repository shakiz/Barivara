package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("type") var type: String? = null
)
