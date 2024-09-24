package com.shakil.barivara.presentation.generatebill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.generatebill.BillInfo
import com.shakil.barivara.data.model.generatebill.GenerateBillResponse
import com.shakil.barivara.data.repository.GenerateBillRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateBillViewModel @Inject constructor(private val generalBillRepoImpl: GenerateBillRepoImpl) :
    ViewModel() {
    var isLoading = MutableLiveData<Boolean>()

    private var bills = MutableLiveData<List<BillInfo>>()
    private var generateBillResponse = MutableLiveData<GenerateBillResponse>()
    private var generateBillErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getBills(): LiveData<List<BillInfo>> {
        return bills
    }

    fun getGenerateBillResponse(): LiveData<GenerateBillResponse> {
        return generateBillResponse
    }

    fun getGenerateBillErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return generateBillErrorResponse
    }

    fun generateBill(token: String, year: Int, month: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data =
                    generalBillRepoImpl.generateBill(token = token, month = month, year = year)
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
                    generalBillRepoImpl.updateBillStatus(token = token, billId = billId)
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
}