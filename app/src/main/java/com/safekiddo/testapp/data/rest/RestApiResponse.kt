package com.safekiddo.testapp.data.rest

sealed class RestApiResponse<out T : Any> {
    data class Success<out T : Any>(val data: T) : RestApiResponse<T>()

    data class Error(val type: ErrorType) : RestApiResponse<Nothing>() {

        sealed class ErrorType {
            object Unknown : ErrorType()
            object NoInternetConnection : ErrorType()
        }
    }
}