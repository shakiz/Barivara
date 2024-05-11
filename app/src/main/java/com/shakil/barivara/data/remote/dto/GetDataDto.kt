package com.shakil.barivara.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetDataDto(
    @SerializedName("result")
    val result: String
)
