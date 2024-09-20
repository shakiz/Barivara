package com.shakil.barivara.data.remote.webservice

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
        @Body sendOtpRequest: SendOtpRequest
    ): Response<SendOtpBaseResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(
        @Header("Content-Type") contentType: String,
        @Body verifyOtpRequest: VerifyOtpRequest
    ): Response<VerifyOtpBaseResponse>
}