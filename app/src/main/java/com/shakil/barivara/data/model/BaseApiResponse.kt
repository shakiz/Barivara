package com.shakil.barivara.data.model

import com.google.gson.annotations.SerializedName

open class BaseApiResponse {
    @SerializedName("status")
    var status: String = ""

    @SerializedName("status_code")
    open var statusCode: Int = 0

    @SerializedName("message")
    var message: String = ""
}

