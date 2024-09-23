package com.shakil.barivara.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.VerifyOtpBaseResponse
import com.shakil.barivara.data.repository.AuthRepoImpl
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepoImpl: AuthRepoImpl) : ViewModel() {

    private var sendOtpResponse = MutableLiveData<SendOtpBaseResponse>()
    private var sendOtpResponseError = MutableLiveData<Resource.Error<ErrorType>>()

    private var verifyOtpResponse = MutableLiveData<VerifyOtpBaseResponse>()
    private var verifyOtpResponseError = MutableLiveData<Resource.Error<ErrorType>>()

    private var logoutResponse = MutableLiveData<BaseApiResponse>()

    var isLoading = MutableLiveData<Boolean>()

    fun getSendOtpResponse(): LiveData<SendOtpBaseResponse> {
        return sendOtpResponse
    }

    fun getSendOtpErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return sendOtpResponseError
    }

    fun getVerifyOtpResponse(): LiveData<VerifyOtpBaseResponse> {
        return verifyOtpResponse
    }

    fun getVerifyOtpErrorResponse(): LiveData<Resource.Error<ErrorType>> {
        return verifyOtpResponseError
    }

    fun getLogoutResponse(): LiveData<BaseApiResponse> {
        return logoutResponse
    }

    fun sendOtp(mobileNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.sendOtp(mobileNo)
                if (data.response != null) {
                    sendOtpResponse.postValue(data.response)
                } else {
                    sendOtpResponseError.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                sendOtpResponseError.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun verifyOtp(mobileNo: String, code: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.verifyOtp(mobileNo, code)
                if (data.response != null) {
                    verifyOtpResponse.postValue(data.response)
                } else {
                    sendOtpResponseError.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                verifyOtpResponseError.postValue(
                    Resource.Error(
                        message = "Something went wrong, please try again",
                        errorType = ErrorType.UNKNOWN
                    )
                )
                isLoading.postValue(false)
            }
        }
    }

    fun logout(mobileNo: String, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.logout(mobileNo, token)
                if (data.response != null) {
                    logoutResponse.postValue(data.response)
                } else {
                    sendOtpResponseError.postValue(
                        Resource.Error(
                            message = data.message,
                            errorType = data.errorType
                        )
                    )
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                verifyOtpResponseError.postValue(
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