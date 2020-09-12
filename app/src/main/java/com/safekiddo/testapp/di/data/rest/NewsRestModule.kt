package com.safekiddo.testapp.di.data.rest

import android.content.Context
import com.safekiddo.testapp.data.rest.interceptor.NewsCacheInterceptor
import com.safekiddo.testapp.data.rest.interceptor.NewsForceCacheInterceptor
import com.safekiddo.testapp.data.rest.service.NewsRestService
import com.safekiddo.testapp.di.qualifier.BaseOkHttpClient
import com.safekiddo.testapp.di.qualifier.BaseRetrofit
import com.safekiddo.testapp.di.qualifier.NewsOkHttpClient
import com.safekiddo.testapp.di.qualifier.NewsRetrofit
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NewsRestModule {

    @Singleton
    @Provides
    fun provideCacheInterceptor() = NewsCacheInterceptor()

    @Singleton
    @Provides
    fun provideForceCacheInterceptor(context: Context) = NewsForceCacheInterceptor(context)

    @NewsOkHttpClient
    @Singleton
    @Provides
    fun provideOkHttpClient(context: Context,
                            @BaseOkHttpClient okHttpClient: OkHttpClient,
                            cacheInterceptor: NewsCacheInterceptor,
                            forceCacheInterceptor: NewsForceCacheInterceptor): OkHttpClient {
        val cacheSize = 50L * 1024 * 1024 // MB
        val cache = Cache(context.cacheDir, cacheSize)

        return okHttpClient
                .newBuilder()
                .cache(cache)
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(forceCacheInterceptor)
                .build()
    }

    @NewsRetrofit
    @Singleton
    @Provides
    fun provideRetrofit(@BaseRetrofit retrofit: Retrofit, @NewsOkHttpClient okHttpClient: OkHttpClient): Retrofit {
        return retrofit
                .newBuilder()
                .baseUrl(NewsRestService.BASE_URL)
                .client(okHttpClient)
                .build()
    }

    @Singleton
    @Provides
    fun provideNewsRestService(@NewsRetrofit retrofit: Retrofit): NewsRestService {
        return retrofit.create(NewsRestService::class.java)
    }
}