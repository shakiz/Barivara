package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("phone") var phone: String? = null
)