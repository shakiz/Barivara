package com.shakil.barivara.domain.tenant

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Resource

interface TenantRepo {
    suspend fun getAllTenant(token: String): Resource<List<Tenant>>
    suspend fun addTenant(token: String, tenant: Tenant): Resource<BaseApiResponse>
}
