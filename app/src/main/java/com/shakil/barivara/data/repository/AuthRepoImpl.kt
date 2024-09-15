package com.shakil.barivara.data.repository

import com.google.gson.Gson
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.model.auth.VerifyOtpBaseResponse
import com.shakil.barivara.data.model.auth.VerifyOtpRequest
import com.shakil.barivara.data.remote.webservice.AuthService
import com.shakil.barivara.domain.auth.AuthRepo
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepo {
    override suspend fun sendOtp(mobileNo: String): Resource<SendOtpBaseResponse> {
        try {
            val task = authService.sendOtp(sendOtpRequest = SendOtpRequest(phone = mobileNo))
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }

    override suspend fun verifyOtp(
        mobileNo: String,
        code: String
    ): Resource<VerifyOtpBaseResponse> {
        try {
            val task = authService.verifyOtp(
                verifyOtpRequest = VerifyOtpRequest(
                    phone = mobileNo,
                    otp = code
                )
            )
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
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