package com.shakil.barivara.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.BaseApiResponse
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

    private var addRoomResponse = MutableLiveData<BaseApiResponse>()
    private var addRoomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var updateRoomResponse = MutableLiveData<BaseApiResponse>()
    private var updateRoomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var deleteRoomResponse = MutableLiveData<BaseApiResponse>()
    private var deleteRoomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var tenants = MutableLiveData<List<Tenant>>()
    private var getTenantListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getRooms(): LiveData<List<Room>> {
        return rooms
    }

    fun getRoomsErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return getRoomListErrorResponse
    }

    fun getAddRoomResponse(): LiveData<BaseApiResponse> {
        return addRoomResponse
    }

    fun getAddRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return addRoomErrorResponse
    }

    fun getUpdateRoomResponse(): LiveData<BaseApiResponse> {
        return updateRoomResponse
    }

    fun getUpdateRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return updateRoomErrorResponse
    }

    fun getDeleteRoomResponse(): LiveData<BaseApiResponse> {
        return deleteRoomResponse
    }

    fun getDeleteRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return deleteRoomErrorResponse
    }

    fun getTenants(): LiveData<List<Tenant>> {
        return tenants
    }

    fun getAllRooms() {
        viewModelScope.launch {
            isLoading.postValue(true)
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

    fun addRoom(room: Room) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.addRoom(room = room)
                if (data.response != null) {
                    addRoomResponse.postValue(data.response)
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

    fun updateRoom(room: Room) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.updateRoom(room = room)
                if (data.response != null) {
                    updateRoomResponse.postValue(data.response)
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

    fun deleteRoom(roomId: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.deleteRoom(roomId = roomId)
                if (data.response != null) {
                    deleteRoomResponse.postValue(data.response)
                } else {
                    deleteRoomErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                deleteRoomErrorResponse.postValue(
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