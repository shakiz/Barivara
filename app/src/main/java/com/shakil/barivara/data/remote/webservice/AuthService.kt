package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.LogoutRequest
import com.shakil.barivara.data.model.auth.PasswordLoginRequest
import com.shakil.barivara.data.model.auth.PasswordSetupRequest
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.model.auth.VerifyOtpBaseResponse
import com.shakil.barivara.data.model.auth.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("send-otp")
    suspend fun sendOtp(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body sendOtpRequest: SendOtpRequest
    ): Response<SendOtpBaseResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body verifyOtpRequest: VerifyOtpRequest
    ): Response<VerifyOtpBaseResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body logoutRequest: LogoutRequest
    ): Response<BaseApiResponse>

    @POST("set-password")
    suspend fun setPassword(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body passwordSetupRequest: PasswordSetupRequest
    ): Response<BaseApiResponse>

    @POST("login-with-password")
    suspend fun passwordLogin(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Accept") accept: String,
        @Body passwordLoginRequest: PasswordLoginRequest
    ): Response<BaseApiResponse>
}