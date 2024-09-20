package com.shakil.barivara.domain.generatebill

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Resource

interface GenerateBillRepo {
    suspend fun getAllTenant(token: String): Resource<List<Tenant>>
    suspend fun addTenant(token: String, tenant: Tenant): Resource<BaseApiResponse>
}
