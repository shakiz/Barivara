package com.shakil.barivara.data.model.generatebill

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class BillHistory(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("room") var room: String? = null,
    @SerializedName("tenant_id") var tenantId: Int? = null,
    @SerializedName("tenant_name") var tenantName: String? = null,
    @SerializedName("rent") var rent: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("is_notified") var isNotified: Int? = null,
    @SerializedName("tenant_phone") var tenantPhone: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("month") var month: Int? = null,
    @SerializedName("year") var year: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("payment_received") var paymentReceived: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(room)
        parcel.writeValue(tenantId)
        parcel.writeString(tenantName)
        parcel.writeValue(rent)
        parcel.writeString(status)
        parcel.writeValue(isNotified)
        parcel.writeString(tenantPhone)
        parcel.writeString(remarks)
        parcel.writeValue(month)
        parcel.writeValue(year)
        parcel.writeString(createdAt)
        parcel.writeString(paymentReceived)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillHistory> {
        override fun createFromParcel(parcel: Parcel): BillHistory {
            return BillHistory(parcel)
        }

        override fun newArray(size: Int): Array<BillHistory?> {
            return arrayOfNulls(size)
        }
    }
}