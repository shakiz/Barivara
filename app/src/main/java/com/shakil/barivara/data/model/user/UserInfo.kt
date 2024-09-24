package com.shakil.barivara.data.model.user

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("name") var name: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null
)