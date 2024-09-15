package com.shakil.barivara.presentation.tenant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.tenant.NewTenant
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
    private var tenants = MutableLiveData<List<NewTenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var addTenantResponse = MutableLiveData<String>()
    private var addTenantErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var updateTenantResponse = MutableLiveData<String>()
    private var updateTenantErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getTenants(): LiveData<List<NewTenant>> {
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

    fun getUpdateTenantResponse(): LiveData<String> {
        return updateTenantResponse
    }

    fun getUpdateTenantErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return updateTenantErrorResponse
    }

    fun getAllTenants(token: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
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

    fun addTenant(token: String, tenant: NewTenant) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = tenantRepoImpl.addTenant(token, tenant)
                if (data.response != null) {
                    addTenantResponse.postValue(data.response?.message)
                } else {
                    addTenantErrorResponse.postValue(
                        Resource.Error(
                            message = data.response?.message ?: "",
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

    fun updateTenant(token: String, tenant: NewTenant) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {

                isLoading.postValue(false)
            } catch (e: Exception) {

                isLoading.postValue(false)
            }
        }
    }
}