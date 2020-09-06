package com.safekiddo.testapp.di.data.rest

import com.safekiddo.testapp.di.qualifier.BaseOkHttpClient
import com.safekiddo.testapp.di.qualifier.BaseRetrofit
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class BaseRestModule {

    @BaseOkHttpClient
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(45, TimeUnit.SECONDS)
                .build()
    }

    @BaseRetrofit
    @Singleton
    @Provides
    fun provideRetrofit(@BaseOkHttpClient okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://www.google.com/") // It isn't valid baseUrl. You should provide your own 'baseUrl' when calling new builder on this 'Retrofit' instance.
                .client(okHttpClient)
                .build()
    }
}