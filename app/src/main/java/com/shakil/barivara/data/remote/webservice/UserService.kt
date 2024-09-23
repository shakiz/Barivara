package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.user.BaseUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserService {
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
    ): Response<BaseUserResponse>
}
