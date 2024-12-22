package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("old_password") var oldPassword: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("password_confirmation") var passwordConfirmation: String? = null
)