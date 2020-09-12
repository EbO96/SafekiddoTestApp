package com.safekiddo.testapp.data.rest.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class NewsForceCacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request()
                .newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
                .let(chain::proceed)
    }
}