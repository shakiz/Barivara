package com.shakil.barivara.data.model.room


enum class RoomStatus(val value: String) {
    Unknown("unknown"),
    Occupied("occupied"),
    Vacant("vacant"),
    Abandoned("abandoned");

    companion object {
        fun from(value: String): RoomStatus? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }

    fun statusToString(): String = value
}