package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.User
import retrofit2.Response
import retrofit2.http.GET

interface AuthService {
    @GET("endpoint")
    suspend fun login(): Response<User>
}