package com.shakil.barivara.domain.tenant

import com.shakil.barivara.data.model.tenant.NewTenant
import com.shakil.barivara.utils.Resource

interface TenantRepo {
    suspend fun getAllTenant(): Resource<List<NewTenant>>
}
