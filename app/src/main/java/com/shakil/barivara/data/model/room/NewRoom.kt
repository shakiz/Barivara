package com.shakil.barivara.data.model.room


import com.google.gson.annotations.SerializedName


data class NewRoom(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("tenant_id") var tenantId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("rent") var rent: Int? = null,
    @SerializedName("no_of_room") var noOfRoom: Int? = null,
    @SerializedName("no_of_bathroom") var noOfBathroom: Int? = null,
    @SerializedName("no_of_balcony") var noOfBalcony: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("electricity_meter_no") var electricityMeterNo: String? = null,
    @SerializedName("gas_meter_no") var gasMeterNo: String? = null,
    @SerializedName("gas_source") var gasSource: String? = null
)
