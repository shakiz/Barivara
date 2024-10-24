package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.room.BaseRoomResponse
import com.shakil.barivara.data.model.room.Room
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomService {
    @GET("rooms")
    suspend fun getAllRoom(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
    ): Response<BaseRoomResponse>

    @POST("rooms")
    suspend fun addRoom(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body room: Room
    ): Response<BaseApiResponse>

    @PUT("rooms/{id}")
    suspend fun updateRoom(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body room: Room
    ): Response<BaseApiResponse>
}
