package com.shakil.barivara.presentation.tenant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.data.repository.TenantRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TenantViewModel @Inject constructor(private val tenantRepoImpl: TenantRepoImpl) :
    ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    private var tenants = MutableLiveData<List<Tenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var addTenantResponse = MutableLiveData<String>()
    private var addTenantErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var updateTenantResponse = MutableLiveData<BaseApiResponse>()
    private var updateTenantErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var deleteTenantResponse = MutableLiveData<BaseApiResponse>()
    private var deleteTenantErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getTenants(): LiveData<List<Tenant>> {
        return tenants
    }

    fun getTenantsErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return getTenantListErrorResponse
    }

    fun getAddTenantResponse(): LiveData<String> {
        return addTenantResponse
    }

    fun getAddTenantErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return addTenantErrorResponse
    }

    fun getUpdateTenantResponse(): LiveData<BaseApiResponse> {
        return updateTenantResponse
    }

    fun getUpdateTenantErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return updateTenantErrorResponse
    }

    fun getDeleteTenantResponse(): LiveData<BaseApiResponse> {
        return deleteTenantResponse
    }

    fun getDeleteTenantErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return deleteTenantErrorResponse
    }

    fun getAllTenants() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = tenantRepoImpl.getAllTenant()
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
                isLoading.postValue(false)
            } catch (e: Exception) {
                getTenantListErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun addTenant(tenant: Tenant) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = tenantRepoImpl.addTenant(tenant)
                if (data.response != null) {
                    addTenantResponse.postValue(data.response?.message)
                } else {
                    addTenantErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                addTenantErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun updateTenant(tenant: Tenant) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = tenantRepoImpl.updateTenant(tenant)
                if (data.response != null) {
                    updateTenantResponse.postValue(data.response)
                } else {
                    updateTenantErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                updateTenantErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun deleteTenant(tenantId: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = tenantRepoImpl.deleteTenant(tenantId)
                if (data.response != null) {
                    deleteTenantResponse.postValue(data.response)
                } else {
                    deleteTenantErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                deleteTenantErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }
}