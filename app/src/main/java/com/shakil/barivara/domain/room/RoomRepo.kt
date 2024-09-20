package com.shakil.barivara.domain.room

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.room.NewRoom
import com.shakil.barivara.utils.Resource

interface RoomRepo {
    suspend fun getAllRoom(token: String): Resource<List<NewRoom>>
    suspend fun addRoom(token: String, room: NewRoom): Resource<BaseApiResponse>
    suspend fun updateRoom(token: String, userId: Int, room: NewRoom): Resource<BaseApiResponse>
}
