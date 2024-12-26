package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.generatebill.BaseGenerateBillResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GenerateBillService {
    @GET("generated-rent")
    suspend fun generateBill(
        @Header("Accept") accept: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<BaseGenerateBillResponse>

    @PUT("rent-status-update/{billId}")
    suspend fun updateBillStatus(
        @Header("Accept") accept: String,
        @Path("billId") billId: Int,
    ): Response<BaseApiResponse>
}
