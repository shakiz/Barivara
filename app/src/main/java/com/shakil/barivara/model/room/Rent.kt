package com.shakil.barivara.model.room

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Rent : Parcelable {
    var rentId: String = ""
    var monthName: String = ""
    var monthId = 0
    var yearName: String = ""
    var yearId = 0
    var associateRoomName: String = ""
    var associateRoomId = 0
    var rentAmount = 0
    var note: String = ""
    var fireBaseKey: String = ""

    constructor()
    constructor(rent: Parcel) {
        rentId = rent.readString() ?: ""
        monthName = rent.readString() ?: ""
        monthId = rent.readInt()
        yearName = rent.readString() ?: ""
        yearId = rent.readInt()
        associateRoomName = rent.readString() ?: ""
        associateRoomId = rent.readInt()
        rentAmount = rent.readInt()
        note = rent.readString() ?: ""
        fireBaseKey = rent.readString() ?: ""
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["rentId"] = rentId
        result["monthName"] = monthName
        result["monthId"] = monthId
        result["yearName"] = yearName
        result["yearId"] = yearId
        result["associateRoomName"] = associateRoomName
        result["associateRoomId"] = associateRoomId
        result["rentAmount"] = rentAmount
        result["note"] = note
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(rentId)
        dest.writeString(monthName)
        dest.writeInt(monthId)
        dest.writeString(yearName)
        dest.writeInt(yearId)
        dest.writeString(associateRoomName)
        dest.writeInt(associateRoomId)
        dest.writeInt(rentAmount)
        dest.writeString(note)
        dest.writeString(fireBaseKey)
    }

    companion object CREATOR : Parcelable.Creator<Rent> {
        override fun createFromParcel(parcel: Parcel): Rent {
            return Rent(parcel)
        }

        override fun newArray(size: Int): Array<Rent?> {
            return arrayOfNulls(size)
        }
    }
}
