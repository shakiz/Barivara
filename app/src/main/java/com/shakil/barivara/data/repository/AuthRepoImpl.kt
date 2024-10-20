package com.shakil.barivara.data.repository

import com.google.gson.Gson
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.ChangePasswordRequest
import com.shakil.barivara.data.model.auth.LoginBaseResponse
import com.shakil.barivara.data.model.auth.LogoutRequest
import com.shakil.barivara.data.model.auth.PasswordLoginRequest
import com.shakil.barivara.data.model.auth.PasswordSetupRequest
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.model.auth.VerifyOtpRequest
import com.shakil.barivara.data.remote.webservice.AuthService
import com.shakil.barivara.domain.auth.AuthRepo
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepo {
    override suspend fun sendOtp(sendOtpRequest: SendOtpRequest): Resource<SendOtpBaseResponse> {
        try {
            val task = authService.sendOtp(
                contentType = Constants.CONTENT_TYPE,
                accept = Constants.ACCEPT,
                sendOtpRequest = sendOtpRequest
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

    override suspend fun verifyOtp(
        verifyOtpRequest: VerifyOtpRequest
    ): Resource<LoginBaseResponse> {
        try {
            val task = authService.verifyOtp(
                accept = Constants.ACCEPT,
                contentType = Constants.CONTENT_TYPE,
                verifyOtpRequest = verifyOtpRequest
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

    override suspend fun logout(
        logoutRequest: LogoutRequest,
        token: String
    ): Resource<BaseApiResponse> {
        try {
            val task = authService.logout(
                token = "Bearer $token",
                accept = Constants.ACCEPT,
                contentType = Constants.CONTENT_TYPE,
                logoutRequest = logoutRequest
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

    override suspend fun setPassword(
        passwordSetupRequest: PasswordSetupRequest,
        token: String
    ): Resource<BaseApiResponse> {
        try {
            val task = authService.setPassword(
                token = "Bearer $token",
                accept = Constants.ACCEPT,
                contentType = Constants.CONTENT_TYPE,
                passwordSetupRequest = passwordSetupRequest
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

    override suspend fun passwordLogin(
        passwordLoginRequest: PasswordLoginRequest,
        token: String
    ): Resource<LoginBaseResponse> {
        try {
            val task = authService.passwordLogin(
                token = "Bearer $token",
                accept = Constants.ACCEPT,
                contentType = Constants.CONTENT_TYPE,
                passwordLoginRequest = passwordLoginRequest
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

    override suspend fun changePassword(
        changePasswordRequest: ChangePasswordRequest,
        token: String
    ): Resource<BaseApiResponse> {
        try {
            val task = authService.changePassword(
                token = "Bearer $token",
                accept = Constants.ACCEPT,
                contentType = Constants.CONTENT_TYPE,
                changePasswordRequest = changePasswordRequest
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