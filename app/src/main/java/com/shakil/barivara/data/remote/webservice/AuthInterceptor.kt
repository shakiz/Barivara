package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.utils.ApiConstants.BASE_URL
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mRefreshToken
import com.shakil.barivara.utils.PrefManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: PrefManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (chain.request().header("Authorization").isNullOrBlank()) {
            if (chain.request().url.toString() != (BASE_URL + "logout")) {
                val accessToken = tokenManager.getString(Constants.mAccessToken)
                val phone = tokenManager.getString(Constants.mUserMobile)
                val isTokenFound = accessToken.isNotEmpty()
                val isRefreshTokenFound = tokenManager.getString(mRefreshToken).isNotEmpty()
                if (accessToken.isNotEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $accessToken")
                    requestBuilder.header("phone", phone)
                    requestBuilder.header("IsTokenFound", isTokenFound.toString())
                    requestBuilder.header("IsRefreshTokenFound", isRefreshTokenFound.toString())
                }
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}
