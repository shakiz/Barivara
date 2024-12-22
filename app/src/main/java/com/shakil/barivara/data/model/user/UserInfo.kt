package com.shakil.barivara.data.model.user

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("user_id") var userId: Int,
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    @SerializedName("phone") var phone: String
)