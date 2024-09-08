package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

class VerifyOtpBaseResponse(@SerializedName("data") val verifyOtpResponse: VerifyOtpResponse) :
    BaseApiResponse()

data class VerifyOtpResponse(
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("token_type") var tokenType: String? = null,
    @SerializedName("expires_in") var expiresIn: Int? = null
)
