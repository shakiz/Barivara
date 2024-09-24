package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.dashboard.BaseDashboardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardService {
    @GET("dashboard")
    suspend fun getDashboardInfo(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
    ): Response<BaseDashboardResponse>
}
