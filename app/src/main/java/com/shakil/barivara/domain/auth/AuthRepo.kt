package com.shakil.barivara.domain.auth

import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.utils.Resource

interface AuthRepo {
    suspend fun sendOtp(mobileNo: String): Resource<SendOtpBaseResponse>
}