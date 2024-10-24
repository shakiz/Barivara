package com.shakil.barivara.domain.room

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.utils.Resource

interface RoomRepo {
    suspend fun getAllRoom(token: String): Resource<List<Room>>
    suspend fun addRoom(token: String, room: Room): Resource<BaseApiResponse>
    suspend fun updateRoom(token: String, room: Room): Resource<BaseApiResponse>
}
