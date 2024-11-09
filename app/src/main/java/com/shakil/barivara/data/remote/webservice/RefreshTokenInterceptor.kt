package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.repository.AuthRepoImpl
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

class RefreshTokenInterceptor(
    private val tokenManager: PrefManager
) : Interceptor {
    @Volatile
    private var isRefreshing = false
    private lateinit var authRepoImpl: AuthRepoImpl

    private fun createAuthService(): AuthService {
        val okHttpClient =
            OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    private val authService = createAuthService()

    fun create(): RefreshTokenInterceptor {
        return RefreshTokenInterceptor(tokenManager)
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        authRepoImpl = AuthRepoImpl(authService)
        var request = chain.request()

        val accessToken = tokenManager.getString(mAccessToken)
        request = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)

        // If unauthorized, refresh token and retry request
        if (response.code == 401) {
            synchronized(this) {
                response.close()
                if (!isRefreshing) {
                    isRefreshing = true
                    val newAccessToken = refreshToken()
                    isRefreshing = false

                    if (newAccessToken != null) {
                        // Retry the request with the new access token
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                        return chain.proceed(newRequest)
                    }
                }
            }
        }

        return response
    }

    private fun refreshToken(): String? {
        val refreshToken = tokenManager.getString(mRefreshToken)

        return runBlocking {
            try {
                // Make the refresh token call synchronously
                val response = authRepoImpl.refreshToken(refreshToken)

                if (response.response?.statusCode == 200) {
                    val newAccessToken = response.response?.loginResponse?.accessToken
                    val newRefreshToken = response.response?.loginResponse?.refreshToken

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
