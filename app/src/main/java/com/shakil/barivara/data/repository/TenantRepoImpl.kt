package com.shakil.barivara.data.repository

import com.shakil.barivara.data.remote.webservice.TenantService
import com.shakil.barivara.domain.tenant.TenantRepo
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class TenantRepoImpl @Inject constructor(
    private val tenantService: TenantService
) : TenantRepo {
    override suspend fun getAllTenant(): Resource<List<Tenant>> {
        try {
            val task = tenantService.getAllTenant()
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(data = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else {
                return Resource.Error()
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }
}
