package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName

data class PasswordLoginRequest(
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("password") var password: String? = null
)