package com.shakil.barivara.data.model.tenant

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Tenant(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("nid_number") var nidNumber: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("start_date") var startDate: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("advanced_amount") var advancedAmount: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(nidNumber)
        parcel.writeString(phone)
        parcel.writeInt(type ?: 0)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeInt(advancedAmount ?: 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tenant> {
        override fun createFromParcel(parcel: Parcel): Tenant {
            return Tenant(parcel)
        }

        override fun newArray(size: Int): Array<Tenant?> {
            return arrayOfNulls(size)
        }
    }
}
