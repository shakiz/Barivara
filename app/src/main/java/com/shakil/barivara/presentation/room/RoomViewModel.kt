package com.shakil.barivara.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.room.NewRoom
import com.shakil.barivara.data.repository.RoomRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(private val roomRepoImpl: RoomRepoImpl) :
    ViewModel() {

    var isLoading = MutableLiveData<Boolean>()
    private var rooms = MutableLiveData<List<NewRoom>>()
    private var getroomListErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var addroomResponse = MutableLiveData<String>()
    private var addroomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    private var updateroomResponse = MutableLiveData<String>()
    private var updateroomErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getRooms(): LiveData<List<NewRoom>> {
        return rooms
    }

    fun getRoomsErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return getroomListErrorResponse
    }

    fun getAddRoomResponse(): LiveData<String> {
        return addroomResponse
    }

    fun getAddRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return addroomErrorResponse
    }

    fun getUpdateRoomResponse(): LiveData<String> {
        return updateroomResponse
    }

    fun getUpdateRoomErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return updateroomErrorResponse
    }

    fun getAllRooms(token: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.getAllRoom(token = token)
                if (data.response != null) {
                    rooms.postValue(data.response)
                } else {
                    getroomListErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                getroomListErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun addRoom(token: String, room: NewRoom) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val data = roomRepoImpl.addRoom(token, room = room)
                if (data.response != null) {
                    addroomResponse.postValue(data.response?.message)
                } else {
                    addroomErrorResponse.postValue(
                        Resource.Error(
                            message = data.response?.message ?: "",
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                addroomErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun updateRoom(token: String, room: NewRoom) {
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