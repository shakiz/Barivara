package com.shakil.barivara.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.dashboard.DashboardData
import com.shakil.barivara.data.model.dashboard.DashboardInfo
import com.shakil.barivara.data.model.dashboard.MonthlyRentInfo
import com.shakil.barivara.data.repository.DashboardRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import com.shakil.barivara.utils.orZero
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

    private var rentDataByYear = MutableLiveData<ArrayList<MonthlyRentInfo>>()
    private var mappedYearlyRentData = MutableLiveData<MutableMap<Int, Int>>()
    private var rentDataByYearErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getDashboardInfo(): LiveData<DashboardInfo> {
        return dashboardInfo
    }

    fun getRentDataByYearAndMonth(): LiveData<DashboardData> {
        return rentDataByMonthAndYear
    }

    fun getRentDataByYear(): LiveData<MutableMap<Int, Int>> {
        return mappedYearlyRentData
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

    fun getRentDataByYearAndMonth(year: Int, month: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = dashboardRepoImpl.getRentByYearAndMonth(
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

    fun getRentDataByYear(year: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = dashboardRepoImpl.getRentByYear(
                    year = year,
                )
                if (data.response != null) {
                    val list = data.response?.data
                    val rentMap = mutableMapOf<Int, Int>()
                    for (i in 0 until list?.size.orZero()) {
                        val item = list?.get(i)
                        val month = item?.month.orZero()
                        val totalPaid = item?.totalPaid.orZero()
                        rentMap[month] = totalPaid
                    }
                    mappedYearlyRentData.postValue(rentMap)
                } else {
                    rentDataByYearErrorResponse.postValue(
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