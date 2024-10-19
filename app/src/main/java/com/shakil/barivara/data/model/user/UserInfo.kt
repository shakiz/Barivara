package com.shakil.barivara.data.model.user

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null
)