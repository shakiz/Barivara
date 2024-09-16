package com.shakil.barivara.data.model.room


import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(tenantId)
        parcel.writeString(name)
        parcel.writeValue(rent)
        parcel.writeValue(noOfRoom)
        parcel.writeValue(noOfBathroom)
        parcel.writeValue(noOfBalcony)
        parcel.writeString(status)
        parcel.writeString(electricityMeterNo)
        parcel.writeString(gasMeterNo)
        parcel.writeString(gasSource)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewRoom> {
        override fun createFromParcel(parcel: Parcel): NewRoom {
            return NewRoom(parcel)
        }

        override fun newArray(size: Int): Array<NewRoom?> {
            return arrayOfNulls(size)
        }
    }
}
