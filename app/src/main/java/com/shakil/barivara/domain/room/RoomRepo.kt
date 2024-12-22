package com.shakil.barivara.domain.room

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.utils.Resource

interface RoomRepo {
    suspend fun getAllRoom(): Resource<List<Room>>
    suspend fun addRoom(room: Room): Resource<BaseApiResponse>
    suspend fun updateRoom(room: Room): Resource<BaseApiResponse>
    suspend fun deleteRoom(roomId: Int): Resource<BaseApiResponse>
}
