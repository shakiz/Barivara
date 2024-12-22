package com.shakil.barivara.data.model.auth

import com.google.gson.annotations.SerializedName

data class PasswordSetupRequest(
    @SerializedName("password") var password: String,
    @SerializedName("password_confirmation") var passwordConfirmation: String
)