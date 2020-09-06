package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.rest.RestApiResponse
import io.reactivex.Single
import java.net.UnknownHostException

abstract class BaseRepository {

    fun <T : Any> Single<T>.asRestApiResponse(): Single<RestApiResponse<T>> {
        return this.map<RestApiResponse<T>> { RestApiResponse.Success(it) }
                .onErrorReturn {
                    val type = when (it) {
                        is UnknownHostException -> RestApiResponse.Error.ErrorType.NoInternetConnection
                        else -> RestApiResponse.Error.ErrorType.Unknown
                    }
                    RestApiResponse.Error(type)
                }
    }
}