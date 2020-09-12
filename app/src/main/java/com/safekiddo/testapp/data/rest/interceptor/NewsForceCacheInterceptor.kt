package com.safekiddo.testapp.data.rest.interceptor

import android.content.Context
import com.safekiddo.testapp.functional.util.NetworkUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class NewsForceCacheInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (NetworkUtil.isConnectedToNetwork(context)) {
            return chain.proceed(chain.request())
        }

        return chain.request()
                .newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
                .let(chain::proceed)
    }
}