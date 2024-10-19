package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.user.UserInfo

class VerifyOtpBaseResponse(@SerializedName("data") val verifyOtpResponse: VerifyOtpResponse) :
    BaseApiResponse()

data class VerifyOtpResponse(
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("token_type") var tokenType: String? = null,
    @SerializedName("expires_at") var expiresAt: Int? = null,
    @SerializedName("refresh_token") var refreshToken: String? = null,
    @SerializedName("refresh_token_expires_at") var refreshTokenExpiresAt: Int? = null,
    @SerializedName("profile") var userInfo: UserInfo
)
