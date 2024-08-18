package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("send-otp")
    suspend fun sendOtp(@Body sendOtpRequest: SendOtpRequest): Response<SendOtpBaseResponse>
}