package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.rest.LoadState
import io.reactivex.Observable
import io.reactivex.Single
import java.net.UnknownHostException

abstract class BaseRepository {

    fun <T : Any> Observable<T>.asLoadState(): Observable<LoadState<T>> {
        return this.map<LoadState<T>> { LoadState.Success(it) }
                .onErrorReturn {
                    val type = when (it) {
                        is UnknownHostException -> LoadState.Error.ErrorType.NoInternetConnection
                        else -> LoadState.Error.ErrorType.Unknown(it)
                    }
                    LoadState.Error(type)
                }
    }
}