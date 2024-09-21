package com.shakil.barivara.data.model.room


enum class RoomStatus(val value: String) {
    Unknown("Unknown"),
    Occupied("Occupied"),
    Vacant("Vacant"),
    Abandoned("Abandoned");

    companion object {
        fun from(value: String): RoomStatus? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }

    fun statusToString(): String = value
}