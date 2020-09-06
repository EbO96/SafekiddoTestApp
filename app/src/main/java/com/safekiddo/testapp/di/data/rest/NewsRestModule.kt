package com.safekiddo.testapp.di.data.rest

import com.safekiddo.testapp.data.rest.service.NewsRestService
import com.safekiddo.testapp.di.qualifier.BaseRetrofit
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NewsRestModule {

    @Singleton
    @Provides
    fun provideNewsRestService(@BaseRetrofit retrofit: Retrofit): NewsRestService {
        return retrofit
                .newBuilder()
                .baseUrl(NewsRestService.BASE_URL)
                .build()
                .create(NewsRestService::class.java)
    }
}