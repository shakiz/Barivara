package com.shakil.barivara.data.repository

import com.google.gson.Gson
import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.room.NewRoom
import com.shakil.barivara.data.remote.webservice.RoomService
import com.shakil.barivara.domain.room.RoomRepo
import com.shakil.barivara.utils.Constants.ACCEPT
import com.shakil.barivara.utils.Constants.CONTENT_TYPE
import com.shakil.barivara.utils.ErrorType
import com.shakil.barivara.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class RoomRepoImpl @Inject constructor(
    private val tenantService: RoomService
) : RoomRepo {
    override suspend fun getAllRoom(token: String): Resource<List<NewRoom>> {
        try {
            val task = tenantService.getAllRoom(
                token = "Bearer $token",
                accept = ACCEPT,
                contentType = CONTENT_TYPE,
            )
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }

    override suspend fun addRoom(token: String, room: NewRoom): Resource<BaseApiResponse> {
        try {
            val task = tenantService.addRoom(
                token = "Bearer $token",
                accept = ACCEPT,
                contentType = CONTENT_TYPE,
                room = room
            )
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }

    override suspend fun updateRoom(
        token: String,
        userId: Int,
        room: NewRoom
    ): Resource<BaseApiResponse> {
        try {
            val task = tenantService.updateRoom(
                token = "Bearer $token",
                userId = userId,
                accept = ACCEPT,
                contentType = CONTENT_TYPE,
                room = room
            )
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }
}
