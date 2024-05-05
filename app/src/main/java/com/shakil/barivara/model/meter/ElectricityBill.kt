package com.shakil.barivara.model.meter

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class ElectricityBill : Parcelable {
    var billId: String = ""
    var meterId = 0
    var meterName: String = ""
    var roomId = 0
    var roomName: String = ""
    var unitPrice = 0.0
    var presentUnit = 0
    var pastUnit = 0
    var totalUnit = 0.0
    var totalBill = 0.0
    var fireBaseKey: String = ""

    constructor()
    constructor(electricityBill: Parcel) {
        billId = electricityBill.readString() ?: ""
        meterId = electricityBill.readInt()
        meterName = electricityBill.readString() ?: ""
        roomId = electricityBill.readInt()
        roomName = electricityBill.readString() ?: ""
        unitPrice = electricityBill.readDouble()
        presentUnit = electricityBill.readInt()
        pastUnit = electricityBill.readInt()
        totalUnit = electricityBill.readDouble()
        totalBill = electricityBill.readDouble()
        fireBaseKey = electricityBill.readString() ?: ""
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(billId)
        dest.writeInt(meterId)
        dest.writeString(meterName)
        dest.writeInt(roomId)
        dest.writeString(roomName)
        dest.writeDouble(unitPrice)
        dest.writeInt(presentUnit)
        dest.writeInt(pastUnit)
        dest.writeDouble(totalUnit)
        dest.writeDouble(totalBill)
        dest.writeString(fireBaseKey)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["bllId"] = billId
        result["meterId"] = meterId
        result["meterName"] = meterName
        result["roomId"] = roomId
        result["roomName"] = roomName
        result["presentUnit"] = presentUnit
        result["pastUnit"] = pastUnit
        result["totalUnit"] = totalUnit
        result["totalBill"] = totalBill
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ElectricityBill> {
        override fun createFromParcel(parcel: Parcel): ElectricityBill {
            return ElectricityBill(parcel)
        }

        override fun newArray(size: Int): Array<ElectricityBill?> {
            return arrayOfNulls(size)
        }
    }
}
