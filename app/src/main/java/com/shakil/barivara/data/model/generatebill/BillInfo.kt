package com.shakil.barivara.data.model.generatebill

import com.google.gson.annotations.SerializedName


data class BillInfo(

    @SerializedName("room") var room: String? = null,
    @SerializedName("tenant") var tenant: String? = null,
    @SerializedName("rent") var rent: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("is_notified") var isNotified: Int? = null,
    @SerializedName("remarks") var remarks: String? = null

)