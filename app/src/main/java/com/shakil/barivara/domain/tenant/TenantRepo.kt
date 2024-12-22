package com.shakil.barivara.domain.tenant

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Resource

interface TenantRepo {
    suspend fun getAllTenant(): Resource<List<Tenant>>
    suspend fun addTenant(tenant: Tenant): Resource<BaseApiResponse>
    suspend fun deleteTenant(tenantId: Int): Resource<BaseApiResponse>
    suspend fun updateTenant(tenant: Tenant): Resource<BaseApiResponse>
}
