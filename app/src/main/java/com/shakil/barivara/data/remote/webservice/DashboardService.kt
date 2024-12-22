package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.dashboard.BaseDashboardResponse
import com.shakil.barivara.data.model.dashboard.DashboardByYearAndMonthBaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DashboardService {
    @GET("dashboard")
    suspend fun getDashboardInfo(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
    ): Response<BaseDashboardResponse>

    @GET("rent-amount-by-month")
    suspend fun getRentDataByYearAndMonth(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
    ): Response<DashboardByYearAndMonthBaseResponse>
}
