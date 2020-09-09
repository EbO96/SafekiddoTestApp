package com.safekiddo.testapp.data.rest

sealed class LoadState<out T : Any> {
    data class Success<out T : Any>(val data: T) : LoadState<T>()

    data class Error(val type: ErrorType) : LoadState<Nothing>() {

        sealed class ErrorType {
            data class Unknown(val exception: Throwable?) : ErrorType()
            object NoInternetConnection : ErrorType()
        }
    }
}