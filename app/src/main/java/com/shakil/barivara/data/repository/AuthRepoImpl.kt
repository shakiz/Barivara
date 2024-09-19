package com.shakil.barivara.data.repository

import com.shakil.barivara.data.model.User
import com.shakil.barivara.data.remote.webservice.AuthService
import com.shakil.barivara.domain.auth.AuthRepo
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepo {
    override suspend fun getAuthData(): Resource<User> {
        try {
            val task = authService.login()
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(data = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else {
                return Resource.Error()
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }
}