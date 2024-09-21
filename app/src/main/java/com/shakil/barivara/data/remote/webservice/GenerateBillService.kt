package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.generatebill.BaseGenerateBillResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GenerateBillService {
    @GET("generated-rent")
    suspend fun generateBill(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<BaseGenerateBillResponse>
}
