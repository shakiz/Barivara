package com.shakil.barivara.data.repository

import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.remote.webservice.AuthService
import com.shakil.barivara.domain.auth.AuthRepo
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepo {
    override suspend fun sendOtp(mobileNumber: String): Resource<SendOtpBaseResponse> {
        try {
            val task = authService.sendOtp(sendOtpRequest = SendOtpRequest(phone = mobileNumber))
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.body()?.statusCode == 500) {
                return Resource.Error(
                    errorType = ErrorType.INTERNAL_SERVER_ERROR,
                    message = task.body()?.message.orEmpty()
                )
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }
}