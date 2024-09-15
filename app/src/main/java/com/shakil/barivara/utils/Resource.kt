package com.shakil.barivara.utils

sealed class Resource<T>(
    open val response: T? = null,
    open val message: String = "",
    open val errorType: ErrorType = ErrorType.UNKNOWN
) {
    class Loading<T>() : Resource<T>()

    data class Success<T>(override val response: T?, override val message: String = "") :
        Resource<T>()

    data class Error<T>(
        override val errorType: ErrorType = ErrorType.UNKNOWN,
        override val message: String = errorType.errorMessage
    ) : Resource<T>()
}

enum class ErrorType(val errorMessage: String) {
    UNKNOWN(errorMessage = "Error"),
    EMPTY_DATA("Empty Data"),
    NO_INTERNET("Empty Data"),
    INTERNAL_SERVER_ERROR("Empty Data"),
    TIME_OUT("Time Out");

    fun getErrorTypeFromString(value: String): ErrorType {
        return try {
            ErrorType.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            UNKNOWN
        }
    }
}
