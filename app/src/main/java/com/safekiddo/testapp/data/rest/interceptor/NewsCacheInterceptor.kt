package com.safekiddo.testapp.data.rest.interceptor

import com.safekiddo.testapp.data.rest.service.NewsRestService
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class NewsCacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val cacheControl = CacheControl.Builder()
                .noCache()
                .maxAge(NewsRestService.MAX_CACHE_AGE_MS, TimeUnit.MILLISECONDS)
                .build()

        return response.newBuilder()
                .removeHeader("Cache-Control")
                .addHeader("Cache-Control", "$cacheControl")
                .build()
    }
}