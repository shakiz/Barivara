package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.tenant.Tenant
import retrofit2.Response
import retrofit2.http.GET

interface TenantService {
    @GET("endpoint")
    suspend fun getAllTenant(): Response<List<Tenant>>
}
