package com.shakil.barivara.data.model.meter

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Meter : Parcelable {
    var meterId: String = ""
    var meterName: String = ""
    var associateRoom: String = ""
    var associateRoomId = 0
    var meterTypeName: String = ""
    var meterTypeId = 0
    var presentUnit = 0
    var pastUnit = 0
    var fireBaseKey: String = ""

    constructor(
        meterName: String,
        associateRoom: String,
        meterType: String,
        presentUnit: Int,
        pastUnit: Int
    ) {
        this.meterName = meterName
        this.associateRoom = associateRoom
        meterTypeName = meterType
        this.presentUnit = presentUnit
        this.pastUnit = pastUnit
    }

    constructor(meterName: String, associateRoom: String, meterType: String) {
        this.meterName = meterName
        this.associateRoom = associateRoom
        meterTypeName = meterType
    }

    constructor(meterName: String, owner: String, presentUnit: Int, pastUnit: Int) {
        this.meterName = meterName
        this.presentUnit = presentUnit
        this.pastUnit = pastUnit
    }

    constructor()
    constructor(meter: Parcel) {
        meterId = meter.readString() ?: ""
        meterName = meter.readString() ?: ""
        associateRoom = meter.readString() ?: ""
        associateRoomId = meter.readInt()
        meterTypeName = meter.readString() ?: ""
        meterTypeName = meter.readString() ?: ""
        meterTypeId = meter.readInt()
        presentUnit = meter.readInt()
        pastUnit = meter.readInt()
        fireBaseKey = meter.readString() ?: ""
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(meterId)
        dest.writeString(meterName)
        dest.writeString(associateRoom)
        dest.writeInt(associateRoomId)
        dest.writeString(meterTypeName)
        dest.writeString(meterTypeName)
        dest.writeInt(meterTypeId)
        dest.writeInt(presentUnit)
        dest.writeInt(pastUnit)
        dest.writeString(fireBaseKey)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["meterId"] = meterId
        result["meterName"] = meterName
        result["associateRoom"] = associateRoom
        result["associateRoomId"] = associateRoomId
        result["meterTypeName"] = meterTypeName
        result["meterTypeId"] = meterTypeId
        result["presentUnit"] = presentUnit
        result["pastUnit"] = pastUnit
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meter> {
        override fun createFromParcel(parcel: Parcel): Meter {
            return Meter(parcel)
        }

        override fun newArray(size: Int): Array<Meter?> {
            return arrayOfNulls(size)
        }
    }
}
