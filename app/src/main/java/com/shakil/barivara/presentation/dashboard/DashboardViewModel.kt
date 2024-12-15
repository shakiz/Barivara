package com.shakil.barivara.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.dashboard.DashboardData
import com.shakil.barivara.data.model.dashboard.DashboardInfo
import com.shakil.barivara.data.repository.DashboardRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepoImpl: DashboardRepoImpl
) : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    private var dashboardInfo = MutableLiveData<DashboardInfo>()
    private var rentDataByMonthAndYear = MutableLiveData<DashboardData>()
    private var getDashboardErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getDashboardInfo(): LiveData<DashboardInfo> {
        return dashboardInfo
    }

    fun getRentDataByYearAndMonth(): LiveData<DashboardData> {
        return rentDataByMonthAndYear
    }

    fun getDashboardInfo(token: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = dashboardRepoImpl.getDashboardInfo(token = token)
                if (data.response != null) {
                    dashboardInfo.postValue(data.response?.data)
                } else {
                    getDashboardErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                isLoading.postValue(false)
                getDashboardErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
            }
        }
    }

    fun getRentDataByYearAndMonth(token: String, year: Int, month: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = dashboardRepoImpl.getRentByYearAndMonth(
                    token = token,
                    year = year,
                    month = month
                )
                if (data.response != null) {
                    rentDataByMonthAndYear.postValue(data.response?.data)
                } else {
                    getDashboardErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                isLoading.postValue(false)
            }
        }
    }
}