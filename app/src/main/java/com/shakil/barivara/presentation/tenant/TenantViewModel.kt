package com.shakil.barivara.presentation.tenant

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

    fun getAllTenants() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = tenantRepoImpl.getAllTenant()
                if (data.data != null) {
                    tenants.postValue(data.data)
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
}