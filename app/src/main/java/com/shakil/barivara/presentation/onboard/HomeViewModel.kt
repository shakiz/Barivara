package com.shakil.barivara.presentation.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.data.repository.TenantRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val tenantRepoImpl: TenantRepoImpl) : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    private var tenants = MutableLiveData<List<Tenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getTenants(): LiveData<List<Tenant>> {
        return tenants
    }

    fun getAllTenants(token: String) {
        viewModelScope.launch {
            try {
                val data = tenantRepoImpl.getAllTenant(token = token)
                if (data.response != null) {
                    tenants.postValue(data.response)
                } else {
                    getTenantListErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
            } catch (e: Exception) {
                getTenantListErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
            }
        }
    }
}