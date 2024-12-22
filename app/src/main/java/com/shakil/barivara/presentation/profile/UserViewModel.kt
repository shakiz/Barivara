package com.shakil.barivara.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.user.UserInfo
import com.shakil.barivara.data.repository.UserRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepoImpl: UserRepoImpl) : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    private var userInfo = MutableLiveData<UserInfo>()
    private var updateProfileResponse = MutableLiveData<BaseApiResponse>()
    private var userInfoErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()
    private var userInfoUpdateErrorResponse = MutableLiveData<Resource.Error<ErrorType>>()

    fun getUserInfo(): LiveData<UserInfo> {
        return userInfo
    }

    fun getUpdateProfileResponse(): LiveData<BaseApiResponse> {
        return updateProfileResponse
    }

    fun getProfile(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = userRepoImpl.getProfile(token)
                if (data.response != null) {
                    userInfo.postValue(data.response)
                } else {
                    userInfoErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                userInfoErrorResponse.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun updateProfile(userInfo: UserInfo, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = userRepoImpl.updateProfile(userInfo, token)
                if (data.response != null) {
                    updateProfileResponse.postValue(data.response)
                } else {
                    userInfoUpdateErrorResponse.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                userInfoUpdateErrorResponse.postValue(
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