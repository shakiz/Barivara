package com.shakil.barivara.domain.auth

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.LogoutRequest
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.model.auth.VerifyOtpBaseResponse
import com.shakil.barivara.data.model.auth.VerifyOtpRequest
import com.shakil.barivara.utils.Resource

interface AuthRepo {
    suspend fun sendOtp(sendOtpRequest: SendOtpRequest): Resource<SendOtpBaseResponse>
    suspend fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Resource<VerifyOtpBaseResponse>
    suspend fun logout(logoutRequest: LogoutRequest, token: String): Resource<BaseApiResponse>
}