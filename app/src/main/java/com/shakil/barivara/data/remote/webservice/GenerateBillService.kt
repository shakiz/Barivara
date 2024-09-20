package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.generatebill.BaseGenerateBillResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface GenerateBillService {
    @PUT("generated-rent?{year}&{month}")
    suspend fun generateBill(
        @Header("Authorization") token: String,
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Response<BaseGenerateBillResponse>
}
