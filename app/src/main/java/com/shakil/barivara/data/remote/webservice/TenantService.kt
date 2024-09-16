package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.tenant.Tenant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TenantService {
    @GET("tenants")
    suspend fun getAllTenant(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
    ): Response<List<Tenant>>

    @POST("tenants")
    suspend fun addTenant(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body tenant: Tenant
    ): Response<BaseApiResponse>
}
