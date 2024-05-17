package com.shakil.barivara.data.model.room

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Room : Parcelable {
    var roomId: String = ""
    var roomName: String = ""
    var tenantName: String = ""
    var tenantNameId = 0
    var noOfRoom = 0
    var noOfBathroom = 0
    var noOfBalcony = 0
    var startMonthName: String = ""
    var startMonthId = 0
    var associateMeterName: String = ""
    var associateMeterId = 0
    var advancedAmount = 0
    var fireBaseKey: String = ""

    constructor()
    constructor(room: Parcel) {
        roomId = room.readString() ?: ""
        roomName = room.readString() ?: ""
        tenantName = room.readString() ?: ""
        tenantNameId = room.readInt()
        noOfRoom = room.readInt()
        noOfBathroom = room.readInt()
        noOfBalcony = room.readInt()
        startMonthName = room.readString() ?: ""
        startMonthId = room.readInt()
        associateMeterName = room.readString() ?: ""
        associateMeterId = room.readInt()
        advancedAmount = room.readInt()
        fireBaseKey = room.readString() ?: ""
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(roomId)
        dest.writeString(roomName)
        dest.writeString(tenantName)
        dest.writeInt(tenantNameId)
        dest.writeInt(noOfRoom)
        dest.writeInt(noOfBathroom)
        dest.writeInt(noOfBalcony)
        dest.writeString(startMonthName)
        dest.writeInt(startMonthId)
        dest.writeString(associateMeterName)
        dest.writeInt(associateMeterId)
        dest.writeInt(advancedAmount)
        dest.writeString(fireBaseKey)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["roomId"] = roomId
        result["roomName"] = roomName
        result["tenantName"] = tenantName
        result["tenantNameId"] = tenantNameId
        result["startMonthName"] = startMonthName
        result["startMonthId"] = startMonthId
        result["associateMeterName"] = associateMeterName
        result["associateMeterId"] = associateMeterId
        result["advancedAmount"] = advancedAmount
        result["noOfRoom"] = noOfRoom
        result["noOfBathroom"] = noOfBathroom
        result["noOfBalcony"] = noOfBalcony
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Room> {
        override fun createFromParcel(parcel: Parcel): Room {
            return Room(parcel)
        }

        override fun newArray(size: Int): Array<Room?> {
            return arrayOfNulls(size)
        }
    }
}
