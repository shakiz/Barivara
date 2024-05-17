package com.shakil.barivara.data.model.tenant

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Tenant : Parcelable {
    var tenantId: String = ""
    var tenantName: String = ""
    var gender: String = ""
    var startingMonth: String = ""
    var nID: String = ""
    var mobileNo: String = ""
    var numberOfPerson = 0
    var advancedAmount = 0
    var startingMonthId = 0
    var associateRoom: String = ""
    var associateRoomId = 0
    var tenantTypeStr: String = ""
    var tenantTypeId = 0
    var isActiveId = 0
    var isActiveValue: String = ""
    var fireBaseKey: String = ""

    constructor()
    constructor(tenant: Parcel) {
        tenantId = tenant.readString() ?: ""
        tenantName = tenant.readString() ?: ""
        gender = tenant.readString() ?: ""
        startingMonth = tenant.readString() ?: ""
        nID = tenant.readString() ?: ""
        mobileNo = tenant.readString() ?: ""
        numberOfPerson = tenant.readInt()
        advancedAmount = tenant.readInt()
        startingMonthId = tenant.readInt()
        associateRoom = tenant.readString() ?: ""
        associateRoomId = tenant.readInt()
        tenantTypeStr = tenant.readString() ?: ""
        tenantTypeId = tenant.readInt()
        isActiveId = tenant.readInt()
        isActiveValue = tenant.readString() ?: ""
        fireBaseKey = tenant.readString() ?: ""
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(tenantId)
        dest.writeString(tenantName)
        dest.writeString(gender)
        dest.writeString(startingMonth)
        dest.writeString(nID)
        dest.writeString(mobileNo)
        dest.writeInt(numberOfPerson)
        dest.writeInt(advancedAmount)
        dest.writeInt(startingMonthId)
        dest.writeString(associateRoom)
        dest.writeInt(associateRoomId)
        dest.writeString(tenantTypeStr)
        dest.writeInt(tenantTypeId)
        dest.writeInt(isActiveId)
        dest.writeString(isActiveValue)
        dest.writeString(fireBaseKey)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["tenantId"] = tenantId
        result["tenantName"] = tenantName
        result["gender"] = gender
        result["startingMonth"] = startingMonth
        result["startingMonthId"] = startingMonthId
        result["nID"] = nID
        result["mobileNo"] = mobileNo
        result["numberOfPerson"] = numberOfPerson
        result["advancedAmount"] = advancedAmount
        result["associateRoom"] = associateRoom
        result["associateRoomId"] = associateRoomId
        result["tenantTypeStr"] = tenantTypeStr
        result["isActiveValue"] = isActiveValue
        result["isActiveId"] = isActiveId
        result["tenantTypeId"] = tenantTypeId
        return result
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
