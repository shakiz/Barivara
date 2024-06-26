package com.shakil.barivara.domain.tenant

import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Resource

interface TenantRepo {
    suspend fun getAllTenant(): Resource<List<Tenant>>
}
