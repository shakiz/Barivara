package com.shakil.barivara.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.data.repository.RoomRepoImpl
import com.shakil.barivara.data.repository.TenantRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepoImpl: RoomRepoImpl,
    private val tenantRepoImpl: TenantRepoImpl
) :
    ViewModel() {

    var isLoading = MutableLiveData<Boolean>()
    private var rooms = MutableLiveData<List<Room>>()
    private var getRoomListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var addRoomResponse = MutableLiveData<String>()
    private var addRoomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var updateRoomResponse = MutableLiveData<String>()
    private var updateRoomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var tenants = MutableLiveData<List<Tenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getRooms(): LiveData<List<Room>> {
        return rooms
    }

    fun getRoomsErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return getRoomListErrorResponse
    }

    fun getAddRoomResponse(): LiveData<String> {
        return addRoomResponse
    }

    fun getAddRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return addRoomErrorResponse
    }

    fun getUpdateRoomResponse(): LiveData<String> {
        return updateRoomResponse
    }

    fun getUpdateRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return updateRoomErrorResponse
    }

    fun getTenants(): LiveData<List<Tenant>> {
        return tenants
    }

    fun getAllRooms(token: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.getAllRoom(token = token)
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
                isLoading.postValue(false)
            } catch (e: Exception) {
                getRoomListErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun addRoom(token: String, room: Room) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.addRoom(token, room = room)
                if (data.response != null) {
                    addRoomResponse.postValue(data.response?.message)
                } else {
                    addRoomErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                addRoomErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun updateRoom(token: String, userId: Int, room: Room) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.updateRoom(token, userId, room = room)
                if (data.response != null) {
                    updateRoomResponse.postValue(data.response?.message)
                } else {
                    updateRoomErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                updateRoomErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
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
}