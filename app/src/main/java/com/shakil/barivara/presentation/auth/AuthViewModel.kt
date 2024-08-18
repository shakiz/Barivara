package com.shakil.barivara.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.repository.AuthRepoImpl
import com.shakil.barivara.utils.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepoImpl: AuthRepoImpl) : ViewModel() {

    private var sendOtpResponse = MutableLiveData<SendOtpBaseResponse>()
    private var sendOtpResponseError = MutableLiveData<ErrorType>()

    fun getSendOtpResponse(): LiveData<SendOtpBaseResponse> {
        return sendOtpResponse
    }

    fun getSendOtpErrorResponse(): LiveData<ErrorType> {
        return sendOtpResponseError
    }

    fun sendOtp(mobileNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = authRepoImpl.sendOtp(mobileNo)
            if (data.response != null) {
                sendOtpResponse.postValue(data.response)
            } else {
                sendOtpResponseError.postValue(data.errorType)
            }
        }
    }
}