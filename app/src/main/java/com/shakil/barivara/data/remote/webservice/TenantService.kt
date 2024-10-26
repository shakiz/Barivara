package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.tenant.BaseTenantResponse
import com.shakil.barivara.data.model.tenant.Tenant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TenantService {
    @GET("tenants")
    suspend fun getAllTenant(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
    ): Response<BaseTenantResponse>

    @POST("tenants")
    suspend fun addTenant(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body tenant: Tenant
    ): Response<BaseApiResponse>

    @PUT("tenants/{id}")
    suspend fun updateTenant(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Path("id") tenantId: Int,
        @Body tenant: Tenant
    ): Response<BaseApiResponse>

    @DELETE("tenants/{id}")
    suspend fun deleteTenant(
        @Header("Accept") accept: String,
        @Path("id") tenantId: Int
    ): Response<BaseApiResponse>
}
