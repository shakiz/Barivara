package com.shakil.barivara.domain.auth

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.VerifyOtpBaseResponse
import com.shakil.barivara.utils.Resource

interface AuthRepo {
    suspend fun sendOtp(mobileNo: String): Resource<SendOtpBaseResponse>
    suspend fun verifyOtp(mobileNo: String, code: Int): Resource<VerifyOtpBaseResponse>
    suspend fun logout(mobileNo: String, token: String): Resource<BaseApiResponse>
}