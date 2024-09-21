package com.shakil.barivara.di

import com.shakil.barivara.data.remote.webservice.AuthInterceptor
import com.shakil.barivara.data.remote.webservice.AuthService
import com.shakil.barivara.data.remote.webservice.GenerateBillService
import com.shakil.barivara.data.remote.webservice.RoomService
import com.shakil.barivara.data.remote.webservice.TenantService
import com.shakil.barivara.utils.ApiConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideBasicAuth() = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttp(
        loggingInterceptor: HttpLoggingInterceptor,
    ): okhttp3.Call.Factory {
        // Create a trust manager that does not validate certificate chains
        val trustAllCertificates = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustAllCertificates), SecureRandom())

        // Create an SSL socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor.apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .sslSocketFactory(sslSocketFactory, trustAllCertificates)
            .hostnameVerifier { _, _ -> true }
            .callTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .connectTimeout(10000, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        callFactory: okhttp3.Call.Factory
    ): AuthService = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .callFactory(callFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideTenantService(
        callFactory: okhttp3.Call.Factory
    ): TenantService = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .callFactory(callFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TenantService::class.java)

    @Provides
    @Singleton
    fun provideRoomService(
        callFactory: okhttp3.Call.Factory
    ): RoomService = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .callFactory(callFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RoomService::class.java)

    @Provides
    @Singleton
    fun provideGenerateBillService(
        callFactory: okhttp3.Call.Factory
    ): GenerateBillService = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .callFactory(callFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GenerateBillService::class.java)
}
