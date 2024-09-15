package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.tenant.NewTenant
import retrofit2.Response
import retrofit2.http.GET

interface TenantService {
    @GET("tenants")
    suspend fun getAllTenant(): Response<List<NewTenant>>
}
