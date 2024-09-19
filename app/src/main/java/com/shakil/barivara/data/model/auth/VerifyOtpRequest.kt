package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName

data class VerifyOtpRequest(
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("otp") var otp: String? = null
)
