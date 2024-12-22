package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.user.BaseUserResponse
import com.shakil.barivara.data.model.user.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserService {
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
    ): Response<BaseUserResponse>

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body userInfo: UserInfo
    ): Response<BaseApiResponse>
}
