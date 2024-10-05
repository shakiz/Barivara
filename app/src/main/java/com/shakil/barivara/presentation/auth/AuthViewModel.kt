package com.shakil.barivara.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.LogoutRequest
import com.shakil.barivara.data.model.auth.OtpType
import com.shakil.barivara.data.model.auth.OtpUIState
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.model.auth.VerifyOtpBaseResponse
import com.shakil.barivara.data.model.auth.VerifyOtpRequest
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

    //Password setup
    var otpUiState = MutableLiveData<OtpUIState>()

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

    fun sendOtp(mobileNo: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (type == OtpType.SET_PASS.value) {
                otpUiState.postValue(OtpUIState.SEND_OTP)
            }
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.sendOtp(
                    SendOtpRequest(
                        phone = mobileNo,
                        type = type
                    )
                )
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

    fun verifyOtp(mobileNo: String, code: Int, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.verifyOtp(
                    VerifyOtpRequest(
                        phone = mobileNo,
                        otp = code,
                        type = type
                    )
                )
                if (data.response != null) {
                    if (type == OtpType.SET_PASS.value) {
                        otpUiState.postValue(OtpUIState.VERIFY_OTP)
                    }
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
        val logoutRequest = LogoutRequest(phone = mobileNo)
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val data = authRepoImpl.logout(logoutRequest, token)
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