package com.shakil.barivara.domain.auth

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.auth.ChangePasswordRequest
import com.shakil.barivara.data.model.auth.LoginBaseResponse
import com.shakil.barivara.data.model.auth.LogoutRequest
import com.shakil.barivara.data.model.auth.PasswordLoginRequest
import com.shakil.barivara.data.model.auth.PasswordSetupRequest
import com.shakil.barivara.data.model.auth.SendOtpBaseResponse
import com.shakil.barivara.data.model.auth.SendOtpRequest
import com.shakil.barivara.data.model.auth.VerifyOtpRequest
import com.shakil.barivara.utils.Resource

interface AuthRepo {
    suspend fun sendOtp(sendOtpRequest: SendOtpRequest): Resource<SendOtpBaseResponse>
    suspend fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Resource<LoginBaseResponse>
    suspend fun logout(logoutRequest: LogoutRequest, token: String): Resource<BaseApiResponse>
    suspend fun setPassword(
        passwordSetupRequest: PasswordSetupRequest,
        token: String
    ): Resource<BaseApiResponse>
    suspend fun passwordLogin(
        passwordLoginRequest: PasswordLoginRequest,
        token: String
    ): Resource<LoginBaseResponse>
    suspend fun changePassword(
        changePasswordRequest: ChangePasswordRequest,
        token: String
    ): Resource<BaseApiResponse>
}