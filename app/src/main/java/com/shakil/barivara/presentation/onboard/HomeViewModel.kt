package com.shakil.barivara.presentation.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.R
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.SliderItem
import com.shakil.barivara.data.model.auth.LogoutRequest
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.data.repository.AuthRepoImpl
import com.shakil.barivara.data.repository.RoomRepoImpl
import com.shakil.barivara.data.repository.TenantRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tenantRepoImpl: TenantRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
    private val authRepoImpl: AuthRepoImpl
) : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    private var tenants = MutableLiveData<List<Tenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var rooms = MutableLiveData<List<Room>>()
    private var getRoomListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var logoutResponse = MutableLiveData<BaseApiResponse>()

    fun getTenants(): LiveData<List<Tenant>> {
        return tenants
    }

    fun getRooms(): LiveData<List<Room>> {
        return rooms
    }

    fun getAllTenants() {
        viewModelScope.launch {
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

    fun getAllRooms() {
        viewModelScope.launch {
            try {
                val data = roomRepoImpl.getAllRoom()
                if (data.response != null) {
                    rooms.postValue(data.response)
                } else {
                    getRoomListErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
            } catch (e: Exception) {
                getRoomListErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
            }
        }
    }

    fun getLogoutResponse(): LiveData<BaseApiResponse> {
        return logoutResponse
    }

    fun logout(mobile: String) {
        val logoutRequest = LogoutRequest(phone = mobile)
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.logout(logoutRequest)
                if (data.response != null) {
                    logoutResponse.postValue(data.response)
                } else {
                    getRoomListErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                    isLoading.postValue(false)
                }
            } catch (e: Exception) {
                isLoading.postValue(false)
            }
        }
    }

    fun getSliderData(): List<SliderItem> {
        return listOf(
            SliderItem(imageDrawable = R.drawable.about_us),
            SliderItem(imageDrawable = R.drawable.assign_room_image),
        )
    }
}