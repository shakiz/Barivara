package com.shakil.barivara.data.repository

import com.shakil.barivara.data.remote.webservice.WebService
import com.shakil.barivara.domain.repository.DataRepo
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class TenantRepoImpl @Inject constructor(
    private val webService: WebService
) : DataRepo {
    override suspend fun getData(): Resource<Tenant> {
        try {
            val task = webService.getData()
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
