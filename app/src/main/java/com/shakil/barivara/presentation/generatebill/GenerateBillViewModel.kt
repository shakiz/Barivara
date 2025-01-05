package com.shakil.barivara.presentation.generatebill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.generatebill.BillHistory
import com.shakil.barivara.data.model.generatebill.BillInfo
import com.shakil.barivara.data.model.generatebill.GenerateBillResponse
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.data.repository.GenerateBillRepoImpl
import com.shakil.barivara.data.repository.TenantRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateBillViewModel @Inject constructor(
    private val generalBillRepoImpl: GenerateBillRepoImpl,
    private val tenantRepoImpl: TenantRepoImpl
) :
    ViewModel() {
    var isLoading = MutableLiveData<Boolean>()

    private var bills = MutableLiveData<List<BillInfo>>()
    private var generateBillResponse = MutableLiveData<GenerateBillResponse>()
    private var generateBillErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var updateRentStatusResponse = MutableLiveData<BaseApiResponse>()
    private var updateRentStatusErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var tenants = MutableLiveData<List<Tenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var billHistory = MutableLiveData<List<BillHistory>>()
    private var getBillHistoryErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getBills(): LiveData<List<BillInfo>> {
        return bills
    }

    fun getBillHistoryList(): LiveData<List<BillHistory>> {
        return billHistory
    }

    fun getGenerateBillResponse(): LiveData<GenerateBillResponse> {
        return generateBillResponse
    }

    fun getGenerateBillErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return generateBillErrorResponse
    }

    fun getUpdateRentStatusResponse(): LiveData<BaseApiResponse> {
        return updateRentStatusResponse
    }

    fun getUpdateRentStatusErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return updateRentStatusErrorResponse
    }

    fun getTenants(): LiveData<List<Tenant>> {
        return tenants
    }

    fun generateBill(year: Int, month: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data =
                    generalBillRepoImpl.generateBill(month = month, year = year)
                if (data.response != null) {
                    bills.postValue(data.response?.billInfo)
                    generateBillResponse.postValue(data.response)
                } else {
                    generateBillErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                generateBillErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun updateBillStatus(token: String, billId: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data =
                    generalBillRepoImpl.updateBillStatus(billId = billId)
                if (data.response != null) {
                    updateRentStatusResponse.postValue(data.response)
                } else {
                    updateRentStatusErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                updateRentStatusErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
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

    fun getBillHistory() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = generalBillRepoImpl.billHistory()
                if (data.response != null) {
                    billHistory.postValue(data.response?.data)
                } else {
                    getBillHistoryErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                getBillHistoryErrorResponse.postValue(
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