package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

class SendOtpBaseResponse(@SerializedName("data") val sendOtpResponse: SendOtpResponse) :
    BaseApiResponse()

data class SendOtpResponse(
    @SerializedName("otp_validation") var otpValidationTime: Long? = null
)
