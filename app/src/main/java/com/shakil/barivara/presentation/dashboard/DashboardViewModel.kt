package com.shakil.barivara.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private var getDashboardErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getDashboardInfo(): LiveData<DashboardInfo> {
        return dashboardInfo
    }

    fun getDashboardInfo(token: String) {
        viewModelScope.launch {
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
            } catch (e: Exception) {
                getDashboardErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
            }
        }
    }
}