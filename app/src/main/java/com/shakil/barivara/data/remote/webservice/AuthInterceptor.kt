package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.utils.ApiConstants
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.Constants.mRefreshToken
import com.shakil.barivara.utils.PrefManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(
    private val tokenManager: PrefManager
) : Interceptor {

    private fun createAuthService(): AuthService {
        val okHttpClient =
            OkHttpClient.Builder().build() // Customize as needed, like adding interceptors
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL) // Use your base URL here
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson or any other converter
            .build()
            .create(AuthService::class.java) // Retrofit creates an implementation of AuthService
    }

    val authService = createAuthService()

    fun create(): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Add the access token to the request header
        val accessToken = tokenManager.getString(mRefreshToken)
        request = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)

        // If unauthorized, refresh token and retry request
        if (response.code == 401) {
            response.close() // Close the previous response

            synchronized(this) {
                // Try refreshing the token synchronously
                val newAccessToken =
                    refreshToken() ?: return response // Return original response if refresh fails

                // Retry the request with the new token
                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()

                // Proceed with the new request
                return chain.proceed(newRequest)
            }
        }

        return response
    }


    private fun refreshToken(): String? {
        val refreshToken = tokenManager.getString(mRefreshToken)

        return runBlocking {
            try {
                // Make the refresh token call synchronously
                val response = authService.refreshToken(refreshToken)

                if (response.isSuccessful) {
                    val newAccessToken = response.body()?.loginResponse?.accessToken
                    val newRefreshToken = response.body()?.loginResponse?.refreshToken

                    if (newAccessToken != null && newRefreshToken != null) {
                        tokenManager[mAccessToken] = newAccessToken
                        tokenManager[mRefreshToken] = newRefreshToken
                        return@runBlocking newAccessToken
                    }
                }
                null
            } catch (e: Exception) {
                println("refreshToken error: ${e.printStackTrace()}")
                null
            }
        }
    }
}
