package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.repository.AuthRepoImpl
import com.shakil.barivara.utils.ApiConstants.BASE_URL
import com.shakil.barivara.utils.Constants.mAccessToken
import com.shakil.barivara.utils.Constants.mRefreshToken
import com.shakil.barivara.utils.Constants.mUserMobile
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
    private val lock = Any()
    private lateinit var authRepoImpl: AuthRepoImpl

    private fun createAuthService(): AuthService {
        val okHttpClient =
            OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
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

        if (chain.request().url.toString() != (BASE_URL + "logout")) {
            // Attach the current access token
            val accessToken = tokenManager.getString(mAccessToken)
            val mobileNo = tokenManager.getString(mUserMobile)
            val isTokenFound = accessToken.isNotEmpty()
            val isRefreshTokenFound = tokenManager.getString(mRefreshToken).isNotEmpty()
            request = request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .header("phone", mobileNo)
                .header("IsTokenFound", isTokenFound.toString())
                .header("IsRefreshTokenFound", isRefreshTokenFound.toString())
                .build()
        }

        var response = chain.proceed(request)

        // If unauthorized, attempt token refresh
        if (response.code == 401) {
            response.close()
            synchronized(lock) {
                if (!isRefreshing) {
                    isRefreshing = true
                    try {
                        val newAccessToken = refreshToken()
                        if (newAccessToken != null) {
                            // Update the token and retry the request
                            tokenManager[mAccessToken] = newAccessToken
                        }
                    } catch (e: Exception) {
                        println("Token refresh failed: ${e.message}")
                    } finally {
                        isRefreshing = false
                    }
                }
            }

            // Retry the original request with the new token
            val updatedAccessToken = tokenManager.getString(mAccessToken)
            val mobileNo = tokenManager.getString(mUserMobile)
            val isTokenFound = updatedAccessToken.isNotEmpty()
            val isRefreshTokenFound = tokenManager.getString(mRefreshToken).isNotEmpty()
            if (updatedAccessToken != null) {
                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $updatedAccessToken")
                    .header("phone", mobileNo)
                    .header("IsTokenFound", isTokenFound.toString())
                    .header("IsRefreshTokenFound", isRefreshTokenFound.toString())
                    .build()
                response = chain.proceed(newRequest)
            }
        }

        return response
    }

    private fun refreshToken(): String? {
        val refreshToken = tokenManager.getString(mRefreshToken)
        val mobile = tokenManager.getString(mUserMobile)

        return runBlocking {
            try {
                val response = authRepoImpl.refreshToken(refreshToken, mobile)
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
                println("Error refreshing token: ${e.message}")
                null
            }
        }
    }
}
